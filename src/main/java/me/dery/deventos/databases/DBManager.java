package me.dery.deventos.databases;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import me.dery.deventos.DEventos;
import me.dery.deventos.databases.impl.MySQL;
import me.dery.deventos.databases.impl.SQLite;
import me.dery.deventos.objects.DPlayer;

public class DBManager {

	private final DEventos instance;

	private IDataBase database;

	private long lastupdate;

	private LinkedHashSet<DPlayer> dplayers;
	private LinkedHashMap<String, Integer> topwins, toploses, topparticipations;

	public DBManager(DEventos instance) throws IOException {

		this.instance = instance;

		topwins = new LinkedHashMap<>();
		toploses = new LinkedHashMap<>();
		topparticipations = new LinkedHashMap<>();
		dplayers = new LinkedHashSet<>();

		if (instance.getConfig().getBoolean("Config.MySQL.Use"))
			try {
				setupMySQL();
			} catch (ClassNotFoundException | SQLException e) {
				setupSQLite();
			}
		else
			setupSQLite();

	}

	private void setupMySQL() throws ClassNotFoundException, SQLException {

		this.database = new MySQL(
						instance.getConfig().getString("Config.MySQL.Usuario"),
						instance.getConfig().getString("Config.MySQL.Senha"),
						instance.getConfig().getString("Config.MySQL.DB"),
						instance.getConfig().getString("Config.MySQL.Host")
		);

	}

	private void setupSQLite() throws IOException {

		File f = new File(instance.getDataFolder(), "informacoes.db");
		if (!f.exists()) f.createNewFile();

		try {
			this.database = new SQLite(instance);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	public void downloadDPlayers() throws ClassNotFoundException, SQLException {

		dplayers = database.getPlayers();

	}

	public void sendToDataBase() throws ClassNotFoundException, SQLException {

		database.restartDataBase();
		database.prepareAddNew();
		for (DPlayer dplayer : dplayers)
			database.addNew(
							dplayer.getPlayer(), dplayer.getWins(), dplayer.getLoses(),
							dplayer.getParticipations()
			);
		database.execute();

	}

	public void downloadFromDataBase() throws ClassNotFoundException, SQLException {

		topwins = database.getTOPWins(
						instance.getConfig().getInt("Config.Rank.Vitorias_Quantidade")
		);
		toploses = database.getTOPLoses(
						instance.getConfig().getInt("Config.Rank.Derrotas_Quantidade")
		);
		topparticipations = database.geTOPParticipations(
						instance.getConfig().getInt("Config.Rank.Participacoes_Quantidade")
		);

	}

	public void openConnection() throws ClassNotFoundException, SQLException {

		database.openConnection();

	}

	public void closeConnection() throws ClassNotFoundException, SQLException {

		database.closeConnection();

	}

	public void changePlayerStatus(String player, int vitorias, int participacoes, int derrotas) {

		for (DPlayer dplayer : dplayers) {

			if (dplayer.getPlayer().equalsIgnoreCase(player)) {
				dplayer.setWins(dplayer.getWins() + vitorias);
				dplayer.setParticipations(dplayer.getParticipations() + participacoes);
				dplayer.setLoses(dplayer.getLoses() + derrotas);
				return;
			}

		}

		DPlayer fplayer = new DPlayer(player, vitorias, derrotas, participacoes);
		dplayers.add(fplayer);

	}

	public LinkedHashMap<String, Integer> getTopWins() { return topwins; }

	public LinkedHashMap<String, Integer> getTopLoses() { return toploses; }

	public LinkedHashMap<String, Integer> getTopParticipations() { return topparticipations; }

	public void setLastUpdate(long lastupdate) { this.lastupdate = lastupdate; }

	public long getLastUpdate() { return lastupdate; }

	public LinkedHashSet<DPlayer> getDPlayers() { return dplayers; }

}
