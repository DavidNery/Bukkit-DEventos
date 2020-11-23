package me.dery.deventos.managers;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Collectors;

import me.dery.deventos.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.dery.deventos.DEventos;
import me.dery.deventos.databases.DBManager;
import me.dery.deventos.enums.eventos.EventProperty;
import me.dery.deventos.enums.eventos.EventState;
import me.dery.deventos.enums.eventos.EventStopReason;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.integrations.SimpleClansAPI;
import me.dery.deventos.pluginlisteners.DEPlayerLoseEvent;
import me.dery.deventos.pluginlisteners.DEPlayerWinEvent;
import me.dery.deventos.utils.ItemStackUtils;
import me.dery.deventos.utils.LocationUtils;

public class EventsStateManager {

	private final DEventos instance;

	private final EventsManager eventsManager;

	private final ListenersManager listenersManager;

	public EventsStateManager(DEventos instance) {

		this.instance = instance;

		eventsManager = instance.getEventosManager();
		listenersManager = instance.getListenersManager();

	}

	public void startEvento(Event event) {

		listenersManager.registerListeners(event);

		eventsManager.getEmAndamento().add(event);

		event.setEventoState(EventState.INICIANDO);

		event.getTasks().add(new BukkitRunnable() {

			FileConfiguration config = eventsManager.getEventoConfig(event);

			int anuncios = config.getInt(EventProperty.ANNOUNCEMENTS.keyInConfig);

			@Override
			public void run() {

				if (anuncios > 0) {
					String msg = config.getStringList(
						EventProperty.MSGEVENTSTARTING.keyInConfig).stream().map(
							s -> s.replace("&", "§")
								.replace("{tempo}", (anuncios * event.getTAnuncios()) + "")
								.replace("{anuncios}", anuncios + "")
								.replace("{evento}", event.getNome() + "")
								.replace("{minplayers}", event.getMinPlayers() + "")
								.replace("{maxplayers}",
									(event.getMaxPlayers() == 0 ? "sem limite" : event.getMaxPlayers() + ""))
								.replace("{premio}", event.getPremioFormatado())
								.replace("{players}", event.getPlayers().size() + ""))
						.collect(Collectors.joining("\n"));

					Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));

					anuncios -= 1;
				} else {
					this.cancel();

					if (event.getPlayers().size() >= event.getMinPlayers()) {

						LocationUtils locationUtils = instance.getLocationUtils();
						ItemStackUtils itemStackUtils = instance.getItemStackUtils();
						SimpleClansAPI sc = instance.getSimpleClansAPI();
						DBManager dbManager = instance.getDBManager();

						config.getStringList(
							EventProperty.COMMANDSONSTART.keyInConfig).forEach(
								cmd -> Bukkit.getServer().dispatchCommand(
									Bukkit.getServer().getConsoleSender(), cmd));

						event.setEventoState(EventState.EMANDAMENTO);

						ConfigurationSection bausSection =
							config.getConfigurationSection(EventProperty.CHESTS.keyInConfig);
						if (bausSection != null)
							bausSection.getKeys(false).forEach(bau -> {

								Location loc = locationUtils.deserializeLocation(
									config.getString(
										EventProperty.CHESTS.keyInConfig
											+ "."
											+ bau
											+ ".Location"));

								loc.getBlock().setType(Material.CHEST);
								final Inventory inv = ((Chest) loc.getBlock()
									.getState()).getInventory();

								for (String item : config.getStringList(
									EventProperty.CHESTS.keyInConfig
										+ "."
										+ bau
										+ ".Items")) {
									if (inv.firstEmpty() != -1)
										inv.addItem(itemStackUtils.criarItem(item));
									else
										break;
								}
							});

						Location spawnLocation = locationUtils.deserializeLocation(event.getSpawn());
						for (String p : event.getPlayers()) {
							Player player = Bukkit.getServer().getPlayer(p);

							player.teleport(spawnLocation);
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);

							if (event.desativarFF() && sc != null)
								sc.disableFriendlyFire(player);

							config.getStringList(
								EventProperty.EFFECTSONSTART.keyInConfig).forEach(efeito -> {
									String[] partes = efeito.split(" ");
									player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(
										itemStackUtils.traduzirPocao(partes[0])),
										Integer.parseInt(partes[1]) * 20, Integer.parseInt(partes[2]) - 1));
								});

							dbManager.changePlayerStatus(p, 0, 1, 0); // Add participacao
						}

						String msg = config.getStringList(
							EventProperty.MSGEVENTSTARTED.keyInConfig).stream().map(
								s -> s.replace("&", "§").replace("{evento}", event.getNome())
									.replace("{players}", event.getPlayers().size() + "")
									.replace("{premio}", event.getPremioFormatado()))
							.collect(Collectors.joining("\n"));

						for (Player on : Bukkit.getOnlinePlayers())
							on.sendMessage(msg);

						if (event.getTAcabar() > 0) {
							event.getTasks().add(new BukkitRunnable() {

								@Override
								public void run() {
									try {
										stopEventoWithoutWinner(event, EventStopReason.TEMPOATINGIDO);
									} catch (EventoException e) {
										e.printStackTrace();
									}
								}

							}.runTaskLater(instance, event.getTAcabar() * 20 * 60));
						}

						for (String s : config.getConfigurationSection("Avisos_Evento").getKeys(false)) {

							String aviso = config.getStringList("Avisos_Evento." + s).stream()
								.map(avisos -> avisos.replace("&", "§")
									.replace("{evento}", event.getNome())
									.replace("{players}", event.getPlayers().size() + "")
									.replace("{premio}", event.getPremioFormatado()))
								.collect(Collectors.joining("\n"));

							event.getTasks().add(new BukkitRunnable() {

								@Override
								public void run() {
									event.getPlayers().forEach(
										p -> Bukkit.getServer().getPlayer(p).sendMessage(aviso));
								}
							}.runTaskLater(instance, Integer.parseInt(s) * 20));

						}

					} else {
						try {
							stopEventoWithoutWinner(event, EventStopReason.POUCOSPLAYERS);
						} catch (EventoException e) {
							e.printStackTrace();
						}
					}
				}

			}

		}.runTaskTimer(instance, 0, event.getTAnuncios() * 20));

	}

	public void stopEventoWithoutWinner(Event event, EventStopReason reason) throws EventoException {

		eventsManager.getEmAndamento().remove(event);

		listenersManager.unregisterListeners(event);

		event.setEventoState(EventState.FECHADO);

		FileConfiguration config = eventsManager.getEventoConfig(event);

		if (reason != null) {

			String msg = config.getStringList(EventProperty.MSGEVENTCANCELLED.keyInConfig)
				.stream()
				.map(s -> s.replace("&", "§").replace("{evento}", event.getNome())
					.replace("{motivo}", instance.getConfig().getString("Motivos." + reason.propertyInConfig))
					.replace("{players}", event.getPlayers().size() + "")
					.replace("{premio}", event.getPremioFormatado()))
				.collect(Collectors.joining("\n"));

			for (Player on : Bukkit.getOnlinePlayers())
				on.sendMessage(msg);
		}

		LocationUtils locationUtils = instance.getLocationUtils();

		Iterator<String> players = event.getPlayers().iterator();
		while (players.hasNext()) {
			String p = players.next();
			players.remove();
			eventsManager.removePlayerFromEvent(p, event, null, false);
		}

		players = event.getEspectadores().iterator();
		while (players.hasNext()) {
			String p = players.next();
			players.remove();
			eventsManager.removePlayerFromEvent(p, event, null, false);
		}

		for (BukkitTask task : event.getTasks())
			task.cancel();
		event.getTasks().clear();

		ConfigurationSection bausSection = config.getConfigurationSection(EventProperty.CHESTS.keyInConfig);
		if (bausSection != null)
			bausSection.getKeys(false).forEach(bau -> {

				Location loc = locationUtils.deserializeLocation(
					config.getString(EventProperty.CHESTS.keyInConfig + "." + bau + ".Location"));

				if (loc.getBlock().getType() == Material.CHEST) {
					((Chest) loc.getBlock().getState()).getInventory().clear();
					loc.getBlock().setType(Material.AIR);
				}

			});

	}

	public void stopEventoWithWinner(Event event, Player winner)
		throws IOException, InvalidConfigurationException, EventoException {

		FileConfiguration config = eventsManager.getEventoConfig(event);

		String msg = config.getStringList(EventProperty.MSGWINNER.keyInConfig).stream()
			.map(s -> s.replace("&", "§").replace("{evento}", event.getNome())
				.replace("{player}", winner.getName())
				.replace("{players}", event.getPlayers().size() + "")
				.replace("{premio}", event.getPremioFormatado()))
			.collect(Collectors.joining("\n"));

		for (Player on : Bukkit.getOnlinePlayers())
			on.sendMessage(msg);

		event.setLastWinner(winner.getName());

		DBManager dbManager = instance.getDBManager();

		dbManager.changePlayerStatus(winner.getName(), 1, 0, 0); // Add vitoria

		String winnerName = winner.getName();
		for (String p : event.getPlayers()) {
			if (!winnerName.equalsIgnoreCase(p)) {
				Player player = Bukkit.getServer().getPlayer(p);
				dbManager.changePlayerStatus(p, 0, 0, 1); // Add derrota

				DEPlayerLoseEvent bukkitEvent = new DEPlayerLoseEvent(player, event);
				Bukkit.getServer().getPluginManager().callEvent(bukkitEvent);
			}
		}

		instance.getEconomy().depositPlayer(winner.getName(), event.getPremio());

		config.getStringList(EventProperty.COMMANDSWINNER.keyInConfig)
			.forEach(cmd -> instance.getServer().dispatchCommand(
				instance.getServer().getConsoleSender(),
				cmd.replace("{player}", winner.getName()).replace("/", "")));

		DEPlayerWinEvent bukkitEvent = new DEPlayerWinEvent(winner, event);
		instance.getServer().getPluginManager().callEvent(bukkitEvent);

		stopEventoWithoutWinner(event, null);

	}

}
