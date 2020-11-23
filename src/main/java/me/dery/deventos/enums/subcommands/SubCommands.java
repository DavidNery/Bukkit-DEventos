package me.dery.deventos.enums.subcommands;

import me.dery.deventos.commands.subcommands.impl.SubCmdDelChest;
import me.dery.deventos.commands.subcommands.impl.SubCmdJoin;
import me.dery.deventos.commands.subcommands.impl.SubCmdEspectate;
import me.dery.deventos.commands.subcommands.impl.SubCmdInfo;
import me.dery.deventos.commands.subcommands.impl.SubCmdStart;
import me.dery.deventos.commands.subcommands.impl.SubCmdStop;
import me.dery.deventos.commands.subcommands.impl.SubCmdRestore;
import me.dery.deventos.commands.subcommands.impl.SubCmdReload;
import me.dery.deventos.commands.subcommands.impl.SubCmdQuit;
import me.dery.deventos.commands.subcommands.impl.SubCmdSet;
import me.dery.deventos.commands.subcommands.impl.SubCmdSetChest;
import me.dery.deventos.commands.subcommands.impl.SubCmdToggleBan;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopLoses;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopParticipations;
import me.dery.deventos.commands.subcommands.impl.SubCmdTopWins;

public enum SubCommands {

	ENTRAR("Entrar", null, SubCmdJoin.class),
	SAIR(null, "sair", SubCmdQuit.class),
	ESPECTAR("Espectar", null, SubCmdEspectate.class),
	RECUPERAR(null, null, SubCmdRestore.class),

	TOPVITORIAS(null, "topvitorias", SubCmdTopWins.class),
	TOPDERROTAS(null, "topderrotas", SubCmdTopLoses.class),
	TOPPARTICIPACOES(null, "topparticipacoes", SubCmdTopParticipations.class),

	RELOAD(null, "reload", SubCmdReload.class),

	INICIAR("Iniciar", "iniciar", SubCmdStart.class),
	PARAR("Parar", "parar", SubCmdStop.class),
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
