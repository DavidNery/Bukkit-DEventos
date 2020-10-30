package me.dery.deventos.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

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
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.others.BanAction;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.pluginlisteners.DEPlayerJoinEvent;
import me.dery.deventos.utils.InventoryUtils;
import me.dery.deventos.utils.LocationUtils;

public class EventosManager {

	private final DEventos instance;

	private final List<Evento> loadedEventos, emAndamento;

	private final List<String> defaultChestItems;

	private final LocationUtils locationUtils;

	private final InventoryUtils inventoryUtils;

	public EventosManager(DEventos instance) {

		this.instance = instance;

		loadedEventos = new ArrayList<>();
		emAndamento = new ArrayList<>();

		defaultChestItems = new ArrayList<>();
		defaultChestItems.addAll(instance.getConfig().getStringList("Config.Baus.Itens_Padroes"));

		locationUtils = instance.getLocationUtils();
		inventoryUtils = instance.getInventoryUtils();

		loadEventos();

	}

	public List<Evento> getLoadedEventos() { return loadedEventos; }

	public List<Evento> getEmAndamento() { return emAndamento; }

	public List<String> getDefaultChestItems() { return defaultChestItems; }

	public List<Evento> loadEventos() {

		for (File f : new File(instance.getDataFolder(), "eventos").listFiles())
			if (f.getName().endsWith(".yml"))
				loadedEventos.add(new Evento(f));

		return loadedEventos;

	}

	public LinkedHashSet<Evento> getPlayerLastWinnerEvents(String winner) {

		LinkedHashSet<Evento> eventos = new LinkedHashSet<>();

		for (Evento evento : loadedEventos)
			if (evento.getLastWinner().equalsIgnoreCase(winner))
				eventos.add(evento);

		return eventos;

	}

	public Evento getEventoByName(String evento) {

		for (Evento ev : loadedEventos)
			if (ev.getNome().equalsIgnoreCase(evento))
				return ev;

		return null;

	}

	public Evento getEventoByEspectador(CommandSender espectador) {

		String espectadorName = espectador.getName();

		for (Evento evento : emAndamento)
			if (evento.getEspectadores().contains(espectadorName))
				return evento;

		return null;

	}

	public Evento getEventoByPlayer(CommandSender player) {

		String playerName = player.getName();

		for (Evento evento : emAndamento)
			if (evento.getPlayers().contains(playerName))
				return evento;

		return null;

	}

	public void addPlayerInEvent(Player p, Evento evento) throws EventoException {

		if (evento.ativarLobby()) {
			Location lobbyLocation = locationUtils.deserializeLocation(evento.getLobby());

			if (lobbyLocation.getWorld() == null)
				throw new EventoException("Mundo_Invalido");

			p.teleport(lobbyLocation);
		} else {
			p.teleport(locationUtils.deserializeLocation(evento.getSpawn()));
		}

		String msg = instance.getConfig().getString("Mensagem.Sucesso.Player_Entrou")
			.replace("&", "§").replace("{player}", p.getName());

		evento.getPlayers().forEach(player -> instance.getServer().getPlayer(player).sendMessage(msg));

		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou")
			.replace("&", "§").replace("{evento}", evento.getNome()));

		evento.getPlayers().add(p.getName());

		if (evento.salvarInv())
			try {
				inventoryUtils.saveInventory(p);
			} catch (IOException e) {
				e.printStackTrace();
			}

		DEPlayerJoinEvent join = new DEPlayerJoinEvent(p, evento);
		instance.getServer().getPluginManager().callEvent(join);

	}

	/**
	 * Necessário remover o player da lista de players do evento, caso ele esteja
	 * 
	 * @param p
	 * @param evento
	 * @param addInEspectadoresList
	 * @throws EventoException
	 */
	public void addPlayerInEspectadorEvent(Player p, Evento evento, boolean addInEspectadoresList)
		throws EventoException {

		Location espectadorLocation = locationUtils.deserializeLocation(evento.getEspectador());

		if (espectadorLocation.getWorld() == null)
			throw new EventoException("Mundo_Invalido");

		if (addInEspectadoresList)
			evento.getEspectadores().add(p.getName());

		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Espectando")
			.replace("&", "§").replace("{evento}", evento.getNome()));

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
	 * @param evento O evento
	 * @param removeFrom A lista que irá remover o player. <strong>Evento#getPlayers()</strong> ou
	 *            <strong>Evento#getEspectadores()</strong>
	 * @param restoreInventory restaurar o inventário do player
	 * @throws EventoException
	 * @return <strong>true</strong> se houver vencedor
	 */
	public void removePlayerFromEvent(String p, Evento evento, List<String> removeFrom, boolean restoreInventory)
		throws EventoException {
		Location exitLocation = locationUtils.deserializeLocation(evento.getExit());

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
			if (removeFrom.equals(evento.getEspectadores())) {
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
			}
		}

	}

	public boolean isBan(String player, Evento evento) {
		List<String> bannedPlayers = getEventoConfig(evento).getStringList(EventoProperty.BANS.keyInConfig);

		if (bannedPlayers == null)
			return false;

		for (String bannedPlayer : bannedPlayers)
			if (bannedPlayer.equalsIgnoreCase(player))
				return true;

		return false;
	}

	public boolean togglePlayerBanStatus(BanAction action, String player, Evento evento) throws IOException {
		FileConfiguration config = getEventoConfig(evento);

		List<String> bannedPlayers = config.getStringList(EventoProperty.BANS.keyInConfig);

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
					setAndSaveEventoConfig(evento, config, EventoProperty.BANS, bannedPlayers);

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
		setAndSaveEventoConfig(evento, config, EventoProperty.BANS, bannedPlayers);

		return true;
	}

	public FileConfiguration getEventoConfig(Evento evento) {
		return YamlConfiguration.loadConfiguration(evento.getFileEvento());
	}

	private void setAndSaveEventoConfig(Evento evento, FileConfiguration config, EventoProperty property, Object value)
		throws IOException {
		config.set(property.keyInConfig, value);
		config.save(evento.getFileEvento());
	}

}
