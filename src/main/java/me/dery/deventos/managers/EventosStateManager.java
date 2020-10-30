package me.dery.deventos.managers;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Collectors;

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
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.eventos.EventoStopReason;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.integrations.SimpleClansAPI;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.pluginlisteners.DEPlayerLoseEvent;
import me.dery.deventos.pluginlisteners.DEPlayerWinEvent;
import me.dery.deventos.utils.ItemStackUtils;
import me.dery.deventos.utils.LocationUtils;

public class EventosStateManager {

	private final DEventos instance;

	private final EventosManager eventosManager;

	private final ListenersManager listenersManager;

	public EventosStateManager(DEventos instance) {

		this.instance = instance;

		eventosManager = instance.getEventosManager();
		listenersManager = instance.getListenersManager();

	}

	public void startEvento(Evento evento) {

		listenersManager.registerListeners(evento);

		eventosManager.getEmAndamento().add(evento);

		evento.setEventoState(EventoState.INICIANDO);

		evento.getTasks().add(new BukkitRunnable() {

			FileConfiguration config = eventosManager.getEventoConfig(evento);

			int anuncios = config.getInt(EventoProperty.ANUNCIOS.keyInConfig);

			@Override
			public void run() {

				if (anuncios > 0) {
					String msg = config.getStringList(
						EventoProperty.MSGEVENTOINICIANDO.keyInConfig).stream().map(
							s -> s.replace("&", "§")
								.replace("{tempo}", (anuncios * evento.getTAnuncios()) + "")
								.replace("{anuncios}", anuncios + "")
								.replace("{evento}", evento.getNome() + "")
								.replace("{minplayers}", evento.getMinPlayers() + "")
								.replace("{maxplayers}",
									(evento.getMaxPlayers() == 0 ? "sem limite" : evento.getMaxPlayers() + ""))
								.replace("{premio}", evento.getPremioFormatado())
								.replace("{players}", evento.getPlayers().size() + ""))
						.collect(Collectors.joining("\n"));

					Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));

					anuncios -= 1;
				} else {
					this.cancel();

					if (evento.getPlayers().size() >= evento.getMinPlayers()) {

						LocationUtils locationUtils = instance.getLocationUtils();
						ItemStackUtils itemStackUtils = instance.getItemStackUtils();
						SimpleClansAPI sc = instance.getSimpleClansAPI();
						DBManager dbManager = instance.getDBManager();

						config.getStringList(
							EventoProperty.COMANDOSAOINICIAR.keyInConfig).forEach(
								cmd -> Bukkit.getServer().dispatchCommand(
									Bukkit.getServer().getConsoleSender(), cmd));

						evento.setEventoState(EventoState.EMANDAMENTO);

						ConfigurationSection bausSection =
							config.getConfigurationSection(EventoProperty.BAUS.keyInConfig);
						if (bausSection != null)
							bausSection.getKeys(false).forEach(bau -> {

								Location loc = locationUtils.deserializeLocation(
									config.getString(
										EventoProperty.BAUS.keyInConfig
											+ "."
											+ bau
											+ ".Location"));

								loc.getBlock().setType(Material.CHEST);
								final Inventory inv = ((Chest) loc.getBlock()
									.getState()).getInventory();

								for (String item : config.getStringList(
									EventoProperty.BAUS.keyInConfig
										+ "."
										+ bau
										+ ".Items")) {
									if (inv.firstEmpty() != -1)
										inv.addItem(itemStackUtils.criarItem(item));
									else
										break;
								}
							});

						Location spawnLocation = locationUtils.deserializeLocation(evento.getSpawn());
						for (String p : evento.getPlayers()) {
							Player player = Bukkit.getServer().getPlayer(p);

							player.teleport(spawnLocation);
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);

							if (evento.desativarFF() && sc != null)
								sc.disableFriendlyFire(player);

							config.getStringList(
								EventoProperty.EFEITOSAOINICIAR.keyInConfig).forEach(efeito -> {
									String[] partes = efeito.split(" ");
									player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(
										itemStackUtils.traduzirPocao(partes[0])),
										Integer.parseInt(partes[1]) * 20, Integer.parseInt(partes[2]) - 1));
								});

							dbManager.changePlayerStatus(p, 0, 1, 0); // Add participacao
						}

						String msg = config.getStringList(
							EventoProperty.MSGEVENTOINICIADO.keyInConfig).stream().map(
								s -> s.replace("&", "§").replace("{evento}", evento.getNome())
									.replace("{players}", evento.getPlayers().size() + "")
									.replace("{premio}", evento.getPremioFormatado()))
							.collect(Collectors.joining("\n"));

						for (Player on : Bukkit.getOnlinePlayers())
							on.sendMessage(msg);

						if (evento.getTAcabar() > 0) {
							evento.getTasks().add(new BukkitRunnable() {

								@Override
								public void run() {
									try {
										stopEventoWithoutWinner(evento, EventoStopReason.TEMPOATINGIDO);
									} catch (EventoException e) {
										e.printStackTrace();
									}
								}

							}.runTaskLater(instance, evento.getTAcabar() * 20 * 60));
						}

						for (String s : config.getConfigurationSection("Avisos_Evento").getKeys(false)) {

							String aviso = config.getStringList("Avisos_Evento." + s).stream()
								.map(avisos -> avisos.replace("&", "§")
									.replace("{evento}", evento.getNome())
									.replace("{players}", evento.getPlayers().size() + "")
									.replace("{premio}", evento.getPremioFormatado()))
								.collect(Collectors.joining("\n"));

							evento.getTasks().add(new BukkitRunnable() {

								@Override
								public void run() {
									evento.getPlayers().forEach(
										p -> Bukkit.getServer().getPlayer(p).sendMessage(aviso));
								}
							}.runTaskLater(instance, Integer.parseInt(s) * 20));

						}

					} else {
						try {
							stopEventoWithoutWinner(evento, EventoStopReason.POUCOSPLAYERS);
						} catch (EventoException e) {
							e.printStackTrace();
						}
					}
				}

			}

		}.runTaskTimer(instance, 0, evento.getTAnuncios() * 20));

	}

	public void stopEventoWithoutWinner(Evento evento, EventoStopReason reason) throws EventoException {

		eventosManager.getEmAndamento().remove(evento);

		listenersManager.unregisterListeners(evento);

		evento.setEventoState(EventoState.FECHADO);

		FileConfiguration config = eventosManager.getEventoConfig(evento);

		if (reason != null) {

			String msg = config.getStringList(EventoProperty.MSGEVENTOCANCELADO.keyInConfig)
				.stream()
				.map(s -> s.replace("&", "§").replace("{evento}", evento.getNome())
					.replace("{motivo}", instance.getConfig().getString("Motivos." + reason.propertyInConfig))
					.replace("{players}", evento.getPlayers().size() + "")
					.replace("{premio}", evento.getPremioFormatado()))
				.collect(Collectors.joining("\n"));

			for (Player on : Bukkit.getOnlinePlayers())
				on.sendMessage(msg);
		}

		LocationUtils locationUtils = instance.getLocationUtils();

		Iterator<String> players = evento.getPlayers().iterator();
		while (players.hasNext()) {
			String p = players.next();
			players.remove();
			eventosManager.removePlayerFromEvent(p, evento, null, false);
		}

		players = evento.getEspectadores().iterator();
		while (players.hasNext()) {
			String p = players.next();
			players.remove();
			eventosManager.removePlayerFromEvent(p, evento, null, false);
		}

		for (BukkitTask task : evento.getTasks())
			task.cancel();
		evento.getTasks().clear();

		ConfigurationSection bausSection = config.getConfigurationSection(EventoProperty.BAUS.keyInConfig);
		if (bausSection != null)
			bausSection.getKeys(false).forEach(bau -> {

				Location loc = locationUtils.deserializeLocation(
					config.getString(EventoProperty.BAUS.keyInConfig + "." + bau + ".Location"));

				if (loc.getBlock().getType() == Material.CHEST) {
					((Chest) loc.getBlock().getState()).getInventory().clear();
					loc.getBlock().setType(Material.AIR);
				}

			});

	}

	public void stopEventoWithWinner(Evento evento, Player winner)
		throws IOException, InvalidConfigurationException, EventoException {

		FileConfiguration config = eventosManager.getEventoConfig(evento);

		String msg = config.getStringList(EventoProperty.MSGVENCEDOR.keyInConfig).stream()
			.map(s -> s.replace("&", "§").replace("{evento}", evento.getNome())
				.replace("{player}", winner.getName())
				.replace("{players}", evento.getPlayers().size() + "")
				.replace("{premio}", evento.getPremioFormatado()))
			.collect(Collectors.joining("\n"));

		for (Player on : Bukkit.getOnlinePlayers())
			on.sendMessage(msg);

		evento.setLastWinner(winner.getName());

		DBManager dbManager = instance.getDBManager();

		dbManager.changePlayerStatus(winner.getName(), 1, 0, 0); // Add vitoria

		String winnerName = winner.getName();
		for (String p : evento.getPlayers()) {
			if (!winnerName.equalsIgnoreCase(p)) {
				Player player = Bukkit.getServer().getPlayer(p);
				dbManager.changePlayerStatus(p, 0, 0, 1); // Add derrota

				DEPlayerLoseEvent bukkitEvent = new DEPlayerLoseEvent(player, evento);
				Bukkit.getServer().getPluginManager().callEvent(bukkitEvent);
			}
		}

		instance.getEconomy().depositPlayer(winner.getName(), evento.getPremio());

		config.getStringList(EventoProperty.COMANDOSVENCEDOR.keyInConfig)
			.forEach(cmd -> instance.getServer().dispatchCommand(
				instance.getServer().getConsoleSender(),
				cmd.replace("{player}", winner.getName()).replace("/", "")));

		DEPlayerWinEvent bukkitEvent = new DEPlayerWinEvent(winner, evento);
		instance.getServer().getPluginManager().callEvent(bukkitEvent);

		stopEventoWithoutWinner(evento, null);

	}

}
