package me.dery.deventos.enums.subcommands;

import me.dery.deventos.commands.subcommands.impl.SubCmdDelChest;
import me.dery.deventos.commands.subcommands.impl.SubCmdEntrar;
import me.dery.deventos.commands.subcommands.impl.SubCmdEspectar;
import me.dery.deventos.commands.subcommands.impl.SubCmdInfo;
import me.dery.deventos.commands.subcommands.impl.SubCmdIniciar;
import me.dery.deventos.commands.subcommands.impl.SubCmdParar;
import me.dery.deventos.commands.subcommands.impl.SubCmdRecuperar;
import me.dery.deventos.commands.subcommands.impl.SubCmdReload;
import me.dery.deventos.commands.subcommands.impl.SubCmdSair;
import me.dery.deventos.commands.subcommands.impl.SubCmdSet;
import me.dery.deventos.commands.subcommands.impl.SubCmdSetChest;
import me.dery.deventos.commands.subcommands.impl.SubCmdToggleBan;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopDerrotas;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopParticipacoes;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopVitorias;

public enum SubCommands {

	ENTRAR("Entrar", null, SubCmdEntrar.class),
	SAIR(null, "sair", SubCmdSair.class),
	ESPECTAR("Espectar", null, SubCmdEspectar.class),
	RECUPERAR(null, null, SubCmdRecuperar.class),

	TOPVITORIAS(null, "topvitorias", SubCmdTopVitorias.class),
	TOPDERROTAS(null, "topderrotas", SubCmdTopDerrotas.class),
	TOPPARTICIPACOES(null, "topparticipacoes", SubCmdTopParticipacoes.class),

	RELOAD(null, "reload", SubCmdReload.class),

	INICIAR("Iniciar", "iniciar", SubCmdIniciar.class),
	PARAR("Parar", "parar", SubCmdParar.class),
	INFO("Info", "info", SubCmdInfo.class),

	BAN("Ban", "ban", SubCmdToggleBan.class),
	UNBAN("UnBan", "unban", SubCmdToggleBan.class),

	SETPREMIO("SetPremio", "setpremio", SubCmdSet.class),
	SETMINPLAYERS("SetMinPlayers", "setminplayers", SubCmdSet.class),
	SETMAXPLAYERS("SetMaxPlayers", "setmaxplayers", SubCmdSet.class),
	SETANUNCIOS("SetAnuncios", "setanuncios", SubCmdSet.class),
	SETTEMPOANUNCIOS("SetTempoAnuncios", "settempoanuncios", SubCmdSet.class),
	SETTEMPOACABAREVENTO("SetTempoAcabarEvento", "settempoacabarevento", SubCmdSet.class),
	SETULTIMOVIVOGANHA("SetUltimoVivoGanha", "setuvg", SubCmdSet.class),
	SETULTIMOEVENTOGANHA("SetUltimoEventoGanha", "setueg", SubCmdSet.class),
	SETTAG("SetTag", "settag", SubCmdSet.class),
	SETBYPASSMAX("SetByPassMax", "setbypassmax", SubCmdSet.class),
	SETSALVARINV("SetSalvarInv", "setsalvarinv", SubCmdSet.class),
	SETATIVARESPECTADOR("SetAtivarEspectador", "setativarespectador", SubCmdSet.class),
	SETATIVARLOBBY("SetAtivarLobby", "setativarlobby", SubCmdSet.class),

	SETDESATIVARPVP("SetDesativarPVP", "setdesativarpvp", SubCmdSet.class),
	SETDESATIVARDAMAGE("SetDesativarDamage", "setdesativardamage", SubCmdSet.class),
	SETDESATIVARFOME("SetDesativarFome", "setdesativarfome", SubCmdSet.class),
	SETDESATIVARFF("SetDesativarFF", "setdesativarff", SubCmdSet.class),

	SETSPAWN("SetSpawn", "setspawn", SubCmdSet.class),
	SETLOBBY("SetLobby", "setlobby", SubCmdSet.class),
	SETEXIT("SetExit", "setexit", SubCmdSet.class),
	SETESPECTADOR("SetEspectador", "setespectador", SubCmdSet.class),

	SETCHEST("SetChestLocation", "setchest", SubCmdSetChest.class),
	DELCHEST("DelChestLocation", "delchest", SubCmdDelChest.class);

	public final String argsError, permissao;

	public final Class<?> subCmdClass;

	private SubCommands(String argsError, String permissao, Class<?> subCmdClass) {
		this.argsError = argsError;
		this.permissao = permissao;
		this.subCmdClass = subCmdClass;
	}

}
