package me.dery.deventos.runnables;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import me.dery.deventos.DEventos;
import me.dery.deventos.databases.DBManager;

public class UpdateData extends BukkitRunnable {

	private final DEventos instance;

	private final DBManager dbManager;

	public UpdateData(DEventos instance) {

		this.instance = instance;

		dbManager = instance.getDBManager();

	}

	@Override
	public void run() {

		try {
			if (instance.getConfig().getBoolean("Config.MySQL.DEBUG_Console"))
				instance.getServer().getConsoleSender().sendMessage(" §3[DEventos] §7Atualizando database...");

			dbManager.openConnection();

			if (dbManager.getDPlayers().size() == 0)
				dbManager.downloadDPlayers();
			else
				dbManager.sendToDataBase();

			dbManager.downloadFromDataBase();
			dbManager.closeConnection();

			dbManager.setLastUpdate(System.currentTimeMillis());

			if (instance.getConfig().getBoolean("Config.MySQL.DEBUG_Console"))
				instance.getServer().getConsoleSender().sendMessage(" §3[DEventos] §7Database atualizada!");

		} catch (ClassNotFoundException | SQLException e) {
			instance.getServer().getConsoleSender().sendMessage(
				" §4[DEventos] §7Nao foi possivel trocar dados com o banco");
			e.printStackTrace();
		}

	}

}
