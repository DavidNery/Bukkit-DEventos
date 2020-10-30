package me.dery.deventos;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.dery.deventos.commands.ComandoEvento;
import me.dery.deventos.commands.ComandoEventos;
import me.dery.deventos.databases.DBManager;
import me.dery.deventos.integrations.SimpleClansAPI;
import me.dery.deventos.listeners.LegendChat;
import me.dery.deventos.listeners.MainListeners;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.managers.EventosStateManager;
import me.dery.deventos.managers.ListenersManager;
import me.dery.deventos.managers.RunnableManager;
import me.dery.deventos.runnables.CheckStart;
import me.dery.deventos.runnables.UpdateData;
import me.dery.deventos.utils.ConfigUtils;
import me.dery.deventos.utils.InventoryUtils;
import me.dery.deventos.utils.ItemStackUtils;
import me.dery.deventos.utils.LocationUtils;
import net.milkbowl.vault.economy.Economy;

public class DEventos extends JavaPlugin {

	private Economy econ;

	private BukkitTask checkStart, updateData;

	private SimpleClansAPI scAPI;

	private DBManager dbManager;

	private EventosManager eventosManager;

	private RunnableManager runnableManager;

	private ListenersManager listenersManager;

	private EventosStateManager eventosStateManager;

	private InventoryUtils inventoryUtils;

	private LocationUtils locationUtils;

	private ItemStackUtils itemStackUtils;

	public void onEnable() {

		getServer().getConsoleSender().sendMessage("§3==========[§bDEventos§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());

		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");

			if (!new File(getDataFolder(), "config.yml").exists()) {
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");

				if (!new File(getDataFolder(), "eventos/parkour.yml").exists())
					saveResource("eventos/parkour.yml", false);
			} else {
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}

			new ConfigUtils().updateConfigAndEventos(this);
			setupEconomy();

			inventoryUtils = new InventoryUtils(this);
			locationUtils = new LocationUtils();
			itemStackUtils = new ItemStackUtils();

			try {
				this.dbManager = new DBManager(this);
			} catch (IOException e) {
				e.printStackTrace();
			}

			eventosManager = new EventosManager(this);
			runnableManager = new RunnableManager();
			listenersManager = new ListenersManager(this);
			eventosStateManager = new EventosStateManager(this);

			new MainListeners(this);

			new ComandoEvento(this);
			new ComandoEventos(this);

			if (getConfig().getBoolean("Config.Auto_Start.Ativar"))
				checkStart = new CheckStart(this).runTaskTimerAsynchronously(this, 0, 60 * 20);

			if (getConfig().getBoolean("Config.Use_SimpleClans")) {
				if (getServer().getPluginManager().getPlugin("SimpleClans") == null) {
					getServer().getConsoleSender().sendMessage(" §3SimpleClans: §bNao Encontrado");
				} else {
					getServer().getConsoleSender().sendMessage(" §3SimpleClans: §bHooked (Clans)");
					this.scAPI = new SimpleClansAPI(this);
				}
			} else {
				getServer().getConsoleSender().sendMessage(" §3SimpleClans: §bNao usar");
			}

			if (getConfig().getBoolean("Config.Use_LegendChat")) {
				if (getServer().getPluginManager().getPlugin("Legendchat") == null) {
					getServer().getConsoleSender().sendMessage(" §3Legendchat: §bNao Encontrado");
				} else {
					getServer().getConsoleSender().sendMessage(" §3Legendchat: §bHooked (Tag)");
					Bukkit.getServer().getPluginManager().registerEvents(new LegendChat(eventosManager), this);
				}
			} else {
				getServer().getConsoleSender().sendMessage(" §3Legendchat: §bNao usar");
			}

			int tempo = getConfig().getInt("Config.Rank.Tempo_Atualizar_Dados") * 20 * 60;
			updateData = new UpdateData(this).runTaskTimerAsynchronously(this, tempo, tempo);

		}
		getServer().getConsoleSender().sendMessage("§3==========[§bDEventos§3]==========");

	}

	public void onDisable() {

		try {
			dbManager.openConnection();
			dbManager.sendToDataBase();
			dbManager.closeConnection();
		} catch (ClassNotFoundException | SQLException e) {
			getServer().getConsoleSender().sendMessage(
				" §4[DEventos] §7Nao foi possivel trocar dados com o banco");
			e.printStackTrace();
		}

		if (checkStart != null)
			checkStart.cancel();
		updateData.cancel();

		HandlerList.unregisterAll(this);

		getServer().getConsoleSender().sendMessage("§4==========[§cDEventos§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDEventos§4]==========");

	}

	private void setupEconomy() {

		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager()
			.getRegistration(Economy.class);

		if (ec != null)
			econ = ec.getProvider();

	}

	public SimpleClansAPI getSimpleClansAPI() { return scAPI; }

	public Economy getEconomy() { return econ; }

	public DBManager getDBManager() { return dbManager; }

	public EventosManager getEventosManager() { return eventosManager; }

	public ListenersManager getListenersManager() { return listenersManager; }
	
	public RunnableManager getRunnableManager() { return runnableManager; }

	public EventosStateManager getEventosStateManager() { return eventosStateManager; }

	public InventoryUtils getInventoryUtils() { return inventoryUtils; }

	public LocationUtils getLocationUtils() { return locationUtils; }

	public ItemStackUtils getItemStackUtils() { return itemStackUtils; }

}
