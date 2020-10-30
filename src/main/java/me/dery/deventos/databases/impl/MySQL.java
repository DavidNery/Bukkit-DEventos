package me.dery.deventos.databases.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import me.dery.deventos.databases.IDataBase;
import me.dery.deventos.objects.DPlayer;

public class MySQL implements IDataBase {

	private final String user, password, database, host;

	private Connection connection;
	private PreparedStatement ps;

	public MySQL(final String user, final String password, final String database, final String host)
					throws SQLException, ClassNotFoundException {

		this.user = user;
		this.password = password;
		this.database = database;
		this.host = host;

		openConnection();
		restartDataBase();
		closeConnection();

	}

	public void openConnection() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		if (connection == null)
			connection = DriverManager.getConnection(
							"jdbc:mysql://" + host + "/" + database + "", user, password
			);

	}

	public void closeConnection() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

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

	public void addNew(String player, int vitorias, int derrotas, int participacoes)
					throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps.setString(1, player);
		ps.setInt(2, vitorias);
		ps.setInt(3, derrotas);
		ps.setInt(4, participacoes);

		ps.addBatch();

	}

	public void execute() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps.executeBatch();

	}

	public LinkedHashMap<String, Integer> getTOPVitorias(int quantidade)
					throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY vitorias DESC LIMIT " + quantidade
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> topvitorias = new LinkedHashMap<>();

		while (rs.next())
			topvitorias.put(rs.getString("player"), rs.getInt("vitorias"));

		return topvitorias;

	}

	public LinkedHashMap<String, Integer> getTOPParticipacoes(int quantidade)
					throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY participacoes DESC LIMIT " + quantidade
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> topparticipacoes = new LinkedHashMap<>();

		while (rs.next())
			topparticipacoes.put(rs.getString("player"), rs.getInt("participacoes"));

		return topparticipacoes;

	}

	public LinkedHashMap<String, Integer> getTOPDerrotas(int quantidade)
					throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps = connection.prepareStatement(
						"SELECT * FROM eventos ORDER BY derrotas DESC LIMIT " + quantidade
		);

		final ResultSet rs = ps.executeQuery();

		final LinkedHashMap<String, Integer> topderrotas = new LinkedHashMap<>();

		while (rs.next())
			topderrotas.put(rs.getString("player"), rs.getInt("derrotas"));

		return topderrotas;

	}

	public LinkedHashSet<DPlayer> getPlayers() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		ps = connection.prepareStatement("SELECT * FROM eventos");

		final ResultSet rs = ps.executeQuery();

		final LinkedHashSet<DPlayer> fplayers = new LinkedHashSet<>();

		while (rs.next())
			fplayers.add(new DPlayer(rs.getString("player"), rs.getInt("vitorias"), rs.getInt("derrotas"), rs.getInt("participacoes")));

		return fplayers;

	}

}
