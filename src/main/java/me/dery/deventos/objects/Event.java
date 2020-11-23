package me.dery.deventos.objects;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import me.dery.deventos.enums.eventos.EventProperty;
import me.dery.deventos.enums.eventos.EventState;

public class Event {

	private final File fileEvento;

	private String nome, tag, permissao, permissaoByPass, permissaoEspectar,
		spawn, lobby, exit, espectador;

	private double premio;

	private final List<String> players, espectadores, respawn;

	private boolean desativarPvp, desativarDamage, desativarFF, desativarFome, requireInvVazio, byPassMax,
		ultimoVivoGanha,
		ultimoEventoGanha, salvarInv, ativarLobby, ativarEspectador;

	private int tAnuncios, tAcabar, minPlayers, maxPlayers;

	private Material block;

	private List<BukkitTask> tasks;

	private EventState eventState;

	private String lastWinner;

	public Event(File fileEvento) {

		this.fileEvento = fileEvento;

		FileConfiguration fc = YamlConfiguration.loadConfiguration(fileEvento);

		// Strings
		nome = fc.getString(EventProperty.NOME.keyInConfig);
		if (fc.getBoolean(EventProperty.GIVETAG.keyInConfig))
			tag = fc.getString(EventProperty.TAG.keyInConfig).replace("&", "§");
		else
			tag = null;
		permissao = fc.getString(EventProperty.PERMISSION.keyInConfig);
		permissaoByPass = fc.getString(EventProperty.PERMISSIONBYPASS.keyInConfig);
		permissaoEspectar = fc.getString(EventProperty.PERMISSIONESPECTAR.keyInConfig);

		// Strings (locations)
		spawn = fc.getString(EventProperty.SPAWN.keyInConfig);
		lobby = fc.getString(EventProperty.LOBBY.keyInConfig);
		exit = fc.getString(EventProperty.EXIT.keyInConfig);
		espectador = fc.getString(EventProperty.ESPECTATOR.keyInConfig);

		// Double
		premio = fc.getDouble(EventProperty.PRIZE.keyInConfig);

		// Booleans
		desativarPvp = fc.getBoolean(EventProperty.DISABLEPVP.keyInConfig);
		desativarDamage = fc.getBoolean(EventProperty.DISABLEDAMAGE.keyInConfig);
		desativarFF = fc.getBoolean(EventProperty.DISABLEFF.keyInConfig);
		requireInvVazio = fc.getBoolean(EventProperty.EMPTYINV.keyInConfig);
		byPassMax = fc.getBoolean(EventProperty.ALLOWBYPASSMAXPLAYERS.keyInConfig);
		ultimoVivoGanha = fc.getBoolean(EventProperty.LASTALIVEWIN.keyInConfig);
		ultimoEventoGanha = fc.getBoolean(EventProperty.LASTEVENTWIN.keyInConfig);
		salvarInv = fc.getBoolean(EventProperty.SAVEINV.keyInConfig);
		ativarLobby = fc.getBoolean(EventProperty.ENABLELOBBY.keyInConfig);
		ativarEspectador = fc.getBoolean(EventProperty.ENABLEESPECTATOR.keyInConfig);
		desativarFome = fc.getBoolean(EventProperty.DISABLEFOME.keyInConfig);

		// Inteiros
		tAnuncios = fc.getInt(EventProperty.ANNOUNCEMENTTIME.keyInConfig);
		minPlayers = fc.getInt(EventProperty.MINPLAYERS.keyInConfig);
		maxPlayers = fc.getInt(EventProperty.MAXPLAYERS.keyInConfig);
		tAcabar = fc.getInt(EventProperty.EVENTSTOPTIME.keyInConfig);

		// Material
		if (fc.getBoolean(EventProperty.WALKBLOCK.keyInConfig))
			block = Material.getMaterial(EventProperty.BLOCKWALK.keyInConfig);
		else
			block = null;

		tasks = new ArrayList<BukkitTask>();

		players = new ArrayList<String>();
		espectadores = new ArrayList<String>();
		respawn = new ArrayList<String>();

		eventState = EventState.FECHADO;

		lastWinner = fc.getString(EventProperty.LASTWINNER.keyInConfig);

	}

	public File getFileEvento() { return fileEvento; }

	public String getNome() { return nome; }

	public String getTag() { return tag; }

	public String getPermissao() { return permissao; }

	public String getPermissaoByPass() { return permissaoByPass; }

	public String getPermissaoEspectar() { return permissaoEspectar; }

	public String getSpawn() { return spawn; }

	public String getLobby() { return lobby; }

	public String getExit() { return exit; }

	public String getEspectador() { return espectador; }

	public double getPremio() { return premio; }

	public String getPremioFormatado() {
		return NumberFormat.getCurrencyInstance().format(premio).replaceAll("[R$]", "");
	}

	public List<String> getPlayers() { return players; }

	public List<String> getEspectadores() { return espectadores; }

	public List<String> getRespawn() { return respawn; }

	public boolean desativarPvp() { return desativarPvp; }

	public boolean desativarDamage() { return desativarDamage; }

	public boolean desativarFF() { return desativarFF; }

	public boolean requireInvVazio() { return requireInvVazio; }

	public boolean byPassMax() { return byPassMax; }

	public boolean ultimoVivoGanha() { return ultimoVivoGanha; }

	public boolean ultimoEventoGanha() { return ultimoEventoGanha; }

	public boolean salvarInv() { return salvarInv; }

	public boolean ativarLobby() { return ativarLobby; }

	public boolean ativarEspectador() { return ativarEspectador; }

	public boolean desativarFome() { return desativarFome; }

	public int getTAnuncios() { return tAnuncios; }

	public int getTAcabar() { return tAcabar; }

	public int getMinPlayers() { return minPlayers; }

	public int getMaxPlayers() { return maxPlayers; }

	public Material getBlock() { return block; }

	public List<BukkitTask> getTasks() { return tasks; }

	public void setEventoState(EventState eventState) { this.eventState = eventState; }

	public EventState getEventoState() { return eventState; }

	public void setLastWinner(String lastWinner) { this.lastWinner = lastWinner; }

	public String getLastWinner() { return lastWinner; }

}
