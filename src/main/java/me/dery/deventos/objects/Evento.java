package me.dery.deventos.objects;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.eventos.EventoState;

public class Evento {

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

	private EventoState eventoState;

	private String lastWinner;

	public Evento(File fileEvento) {

		this.fileEvento = fileEvento;

		FileConfiguration fc = YamlConfiguration.loadConfiguration(fileEvento);

		// Strings
		nome = fc.getString(EventoProperty.NOME.keyInConfig);
		if (fc.getBoolean(EventoProperty.DARTAG.keyInConfig))
			tag = fc.getString(EventoProperty.TAG.keyInConfig).replace("&", "§");
		else
			tag = null;
		permissao = fc.getString(EventoProperty.PERMISSAO.keyInConfig);
		permissaoByPass = fc.getString(EventoProperty.PERMISSAOBYPASS.keyInConfig);
		permissaoEspectar = fc.getString(EventoProperty.PERMISSAOESPECTAR.keyInConfig);

		// Strings (locations)
		spawn = fc.getString(EventoProperty.SPAWN.keyInConfig);
		lobby = fc.getString(EventoProperty.LOBBY.keyInConfig);
		exit = fc.getString(EventoProperty.EXIT.keyInConfig);
		espectador = fc.getString(EventoProperty.ESPECTADOR.keyInConfig);

		// Double
		premio = fc.getDouble(EventoProperty.PREMIO.keyInConfig);

		// Booleans
		desativarPvp = fc.getBoolean(EventoProperty.DESATIVARPVP.keyInConfig);
		desativarDamage = fc.getBoolean(EventoProperty.DESATIVARDAMAGE.keyInConfig);
		desativarFF = fc.getBoolean(EventoProperty.DESATIVARFF.keyInConfig);
		requireInvVazio = fc.getBoolean(EventoProperty.INVVAZIO.keyInConfig);
		byPassMax = fc.getBoolean(EventoProperty.ALLOWBYPASSMAXPLAYERS.keyInConfig);
		ultimoVivoGanha = fc.getBoolean(EventoProperty.ULTIMOVIVOGANHA.keyInConfig);
		ultimoEventoGanha = fc.getBoolean(EventoProperty.ULTIMOEVENTOGANHA.keyInConfig);
		salvarInv = fc.getBoolean(EventoProperty.SALVARINV.keyInConfig);
		ativarLobby = fc.getBoolean(EventoProperty.ATIVARLOBBY.keyInConfig);
		ativarEspectador = fc.getBoolean(EventoProperty.ATIVARESPECTADOR.keyInConfig);
		desativarFome = fc.getBoolean(EventoProperty.DESATIVARFOME.keyInConfig);

		// Inteiros
		tAnuncios = fc.getInt(EventoProperty.TEMPOANUNCIOS.keyInConfig);
		minPlayers = fc.getInt(EventoProperty.MINPLAYERS.keyInConfig);
		maxPlayers = fc.getInt(EventoProperty.MAXPLAYERS.keyInConfig);
		tAcabar = fc.getInt(EventoProperty.TEMPOACABAREVENTO.keyInConfig);

		// Material
		if (fc.getBoolean(EventoProperty.PASSARBLOCO.keyInConfig))
			block = Material.getMaterial(EventoProperty.BLOCOPASSAR.keyInConfig);
		else
			block = null;

		tasks = new ArrayList<BukkitTask>();

		players = new ArrayList<String>();
		espectadores = new ArrayList<String>();
		respawn = new ArrayList<String>();

		eventoState = EventoState.FECHADO;

		lastWinner = fc.getString(EventoProperty.LASTWINNER.keyInConfig);

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

	public void setEventoState(EventoState eventoState) { this.eventoState = eventoState; }

	public EventoState getEventoState() { return eventoState; }

	public void setLastWinner(String lastWinner) { this.lastWinner = lastWinner; }

	public String getLastWinner() { return lastWinner; }

}
