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

	public void addNew(String player, int vitorias, int derrotas, int participacoes)
					throws ClassNotFoundException, SQLException;
	
	public void execute() throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> getTOPVitorias(int quantidade)
					throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> getTOPParticipacoes(int quantidade)
					throws ClassNotFoundException, SQLException;

	public LinkedHashMap<String, Integer> getTOPDerrotas(int quantidade)
					throws ClassNotFoundException, SQLException;

	public LinkedHashSet<DPlayer> getPlayers() throws ClassNotFoundException, SQLException;

}
