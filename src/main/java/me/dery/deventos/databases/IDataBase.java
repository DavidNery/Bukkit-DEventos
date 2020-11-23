package me.dery.deventos.databases;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import me.dery.deventos.objects.DPlayer;

public interface IDataBase {

	public void openConnection() throws ClassNotFoundException, SQLException;

	public void closeConnection() throws ClassNotFoundException, SQLException;

	public void restartDataBase() throws ClassNotFoundException, SQLException;
	
	public void prepareAddNew() throws ClassNotFoundException, SQLException;

	public void addNew(String player, int wins, int loses, int participations)
					throws ClassNotFoundException, SQLException;
	
	public void execute() throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> getTOPWins(int amount)
					throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> geTOPParticipations(int amount)
					throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> getTOPLoses(int amount)
					throws ClassNotFoundException, SQLException;

	public LinkedHashSet<DPlayer> getPlayers() throws ClassNotFoundException, SQLException;

}
