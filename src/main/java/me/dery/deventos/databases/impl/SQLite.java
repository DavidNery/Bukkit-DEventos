package me.dery.deventos.databases.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import me.dery.deventos.DEventos;
import me.dery.deventos.databases.IDataBase;
import me.dery.deventos.objects.DPlayer;

public class SQLite implements IDataBase {

	private final DEventos instance;

	private Connection connection;
	private PreparedStatement ps;

	public SQLite(final DEventos instance) throws ClassNotFoundException, SQLException {

		this.instance = instance;

		openConnection();
		restartDataBase();
		closeConnection();

	}

	public void openConnection() throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");
		if (connection == null) connection = DriverManager.getConnection(
						"jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator
										+ "informacoes.db"
		);

	}

	public void closeConnection() throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");

		if (connection != null) {
			connection.close();
			connection = null;
		}

		if (ps != null) {
			ps.close();
			ps = null;
		}

	}

	public void restartDataBase() throws ClassNotFoundException, SQLException {

		ps = connection.prepareStatement("DROP TABLE IF EXISTS eventos;");
		ps.executeUpdate();

		ps = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS eventos ("
										+ "player VARCHAR(255), "
										+ "vitorias INTEGER, "
										+ "derrotas INTEGER, "
										+ "participacoes INTEGER);"
		);
		ps.executeUpdate();

	}

	public void prepareAddNew() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps = connection.prepareStatement(
						"INSERT INTO eventos (player, vitorias, derrotas, participacoes) VALUES (?, ?, ?, ?);"
		);

	}

	public void addNew(String player, int wins, int loses, int participations)
					throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps.setString(1, player);
		ps.setInt(2, wins);
		ps.setInt(3, loses);
		ps.setInt(4, participations);

		ps.addBatch();

	}

	public void execute() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps.executeBatch();

	}

	public LinkedHashMap<String, Integer> getTOPWins(final int amount)
					throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY vitorias DESC LIMIT " + amount
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> topwins = new LinkedHashMap<>();

		while (rs.next())
			topwins.put(rs.getString("player"), rs.getInt("vitorias"));

		return topwins;

	}

	public LinkedHashMap<String, Integer> geTOPParticipations(final int amount)
					throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY participacoes DESC LIMIT " + amount
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> topparticipations = new LinkedHashMap<>();

		while (rs.next())
			topparticipations.put(rs.getString("player"), rs.getInt("participacoes"));

		return topparticipations;

	}

	public LinkedHashMap<String, Integer> getTOPLoses(final int amount)
					throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY derrotas DESC LIMIT " + amount
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> toploses = new LinkedHashMap<>();

		while (rs.next())
			toploses.put(rs.getString("player"), rs.getInt("derrotas"));

		return toploses;

	}

	public LinkedHashSet<DPlayer> getPlayers() throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");

		ps = connection.prepareStatement("SELECT * FROM eventos");

		final ResultSet rs = ps.executeQuery();

		final LinkedHashSet<DPlayer> fplayers = new LinkedHashSet<>();

		while (rs.next())
			fplayers.add(new DPlayer(rs.getString("player"), rs.getInt("vitorias"), rs.getInt("derrotas"), rs.getInt("participacoes")));

		return fplayers;

	}

}
