package me.dery.deventos.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import me.dery.deventos.objects.Event;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventProperty;
import me.dery.deventos.enums.others.BanAction;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.pluginlisteners.DEPlayerJoinEvent;
import me.dery.deventos.utils.InventoryUtils;
import me.dery.deventos.utils.LocationUtils;

public class EventsManager {

	private final DEventos instance;

	private final List<Event> loadedEvents, emAndamento;

	private final List<String> defaultChestItems;

	private final LocationUtils locationUtils;

	private final InventoryUtils inventoryUtils;

	public EventsManager(DEventos instance) {

		this.instance = instance;

		loadedEvents = new ArrayList<>();
		emAndamento = new ArrayList<>();

		defaultChestItems = new ArrayList<>();
		defaultChestItems.addAll(instance.getConfig().getStringList("Config.Baus.Itens_Padroes"));

		locationUtils = instance.getLocationUtils();
		inventoryUtils = instance.getInventoryUtils();

		loadEventos();

	}

	public List<Event> getLoadedEventos() { return loadedEvents; }

	public List<Event> getEmAndamento() { return emAndamento; }

	public List<String> getDefaultChestItems() { return defaultChestItems; }

	public List<Event> loadEventos() {

		for (File f : new File(instance.getDataFolder(), "eventos").listFiles())
			if (f.getName().endsWith(".yml"))
				loadedEvents.add(new Event(f));

		return loadedEvents;

	}

	public LinkedHashSet<Event> getPlayerLastWinnerEvents(String winner) {

		LinkedHashSet<Event> events = new LinkedHashSet<>();

		for (Event event : loadedEvents)
			if (event.getLastWinner().equalsIgnoreCase(winner))
				events.add(event);

		return events;

	}

	public Event getEventoByName(String evento) {

		for (Event ev : loadedEvents)
			if (ev.getNome().equalsIgnoreCase(evento))
				return ev;

		return null;

	}

	public Event getEventoByEspectador(CommandSender espectador) {

		String espectadorName = espectador.getName();

		for (Event event : emAndamento)
			if (event.getEspectadores().contains(espectadorName))
				return event;

		return null;

	}

	public Event getEventoByPlayer(CommandSender player) {

		String playerName = player.getName();

		for (Event event : emAndamento)
			if (event.getPlayers().contains(playerName))
				return event;

		return null;

	}

	public void addPlayerInEvent(Player p, Event event) throws EventoException {

		if (event.ativarLobby()) {
			Location lobbyLocation = locationUtils.deserializeLocation(event.getLobby());

			if (lobbyLocation.getWorld() == null)
				throw new EventoException("Mundo_Invalido");

			p.teleport(lobbyLocation);
		} else {
			p.teleport(locationUtils.deserializeLocation(event.getSpawn()));
		}

		String msg = instance.getConfig().getString("Mensagem.Sucesso.Player_Entrou")
			.replace("&", "§").replace("{player}", p.getName());

		event.getPlayers().forEach(player -> instance.getServer().getPlayer(player).sendMessage(msg));

		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou")
			.replace("&", "§").replace("{evento}", event.getNome()));

		event.getPlayers().add(p.getName());

		if (event.salvarInv())
			try {
				inventoryUtils.saveInventory(p);
			} catch (IOException e) {
				e.printStackTrace();
			}

		DEPlayerJoinEvent join = new DEPlayerJoinEvent(p, event);
		instance.getServer().getPluginManager().callEvent(join);

	}

	/**
	 * Necessário remover o player da lista de players do evento, caso ele esteja
	 * 
	 * @param p
	 * @param event
	 * @param addInEspectadoresList
	 * @throws EventoException
	 */
	public void addPlayerInEspectadorEvent(Player p, Event event, boolean addInEspectadoresList)
		throws EventoException {

		Location espectadorLocation = locationUtils.deserializeLocation(event.getEspectador());

		if (espectadorLocation.getWorld() == null)
			throw new EventoException("Mundo_Invalido");

		if (addInEspectadoresList)
			event.getEspectadores().add(p.getName());

		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Espectando")
			.replace("&", "§").replace("{evento}", event.getNome()));

		p.teleport(espectadorLocation);

		ItemStack tocha = new ItemStack(Material.REDSTONE_TORCH_ON);
		ItemMeta tochameta = tocha.getItemMeta();
		tochameta.setDisplayName("§cClique para sair");
		tocha.setItemMeta(tochameta);

		p.getInventory().setItem(8, tocha);
		p.getInventory().setHelmet(new ItemStack(Material.SKULL_ITEM));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2));

	}

	/**
	 * @param p O player
	 * @param event O evento
	 * @param removeFrom A lista que irá remover o player. <strong>Evento#getPlayers()</strong> ou
	 *            <strong>Evento#getEspectadores()</strong>
	 * @param restoreInventory restaurar o inventário do player
	 * @throws EventoException
	 * @return <strong>true</strong> se houver vencedor
	 */
	public void removePlayerFromEvent(String p, Event event, List<String> removeFrom, boolean restoreInventory)
		throws EventoException {
		Location exitLocation = locationUtils.deserializeLocation(event.getExit());

		if (exitLocation.getWorld() == null)
			throw new EventoException("Mundo_Invalido");

		Player player = instance.getServer().getPlayer(p);

		player.teleport(exitLocation);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);

		Iterator<PotionEffect> effects = player.getActivePotionEffects().iterator();
		while (effects.hasNext())
			player.removePotionEffect(effects.next().getType());

		if (restoreInventory)
			try {
				inventoryUtils.restoreInventory(player);
			} catch (IOException e) {
				e.printStackTrace();
			}

		if (removeFrom != null) {
			removeFrom.remove(p);
			if (removeFrom.equals(event.getEspectadores())) {
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
			}
		}

	}

	public boolean isBan(String player, Event event) {
		List<String> bannedPlayers = getEventoConfig(event).getStringList(EventProperty.BANS.keyInConfig);

		if (bannedPlayers == null)
			return false;

		for (String bannedPlayer : bannedPlayers)
			if (bannedPlayer.equalsIgnoreCase(player))
				return true;

		return false;
	}

	public boolean togglePlayerBanStatus(BanAction action, String player, Event event) throws IOException {
		FileConfiguration config = getEventoConfig(event);

		List<String> bannedPlayers = config.getStringList(EventProperty.BANS.keyInConfig);

		if (bannedPlayers == null) {
			if (action == BanAction.UNBAN)
				return false;
			else
				bannedPlayers = new ArrayList<>();
		}

		Iterator<String> it = bannedPlayers.iterator();

		while (it.hasNext()) {
			String bannedPlayer = it.next();

			if (bannedPlayer.equalsIgnoreCase(player)) {
				if (action == BanAction.UNBAN) {
					it.remove();
					setAndSaveEventoConfig(event, config, EventProperty.BANS, bannedPlayers);

					return true;
				} else {
					// Se achou e a ação é BAN, então o player já está banido
					return false;
				}
			}
		}

		if (action == BanAction.UNBAN)
			return false;

		bannedPlayers.add(player);
		setAndSaveEventoConfig(event, config, EventProperty.BANS, bannedPlayers);

		return true;
	}

	public FileConfiguration getEventoConfig(Event event) {
		return YamlConfiguration.loadConfiguration(event.getFileEvento());
	}

	private void setAndSaveEventoConfig(Event event, FileConfiguration config, EventProperty property, Object value)
		throws IOException {
		config.set(property.keyInConfig, value);
		config.save(event.getFileEvento());
	}

}
