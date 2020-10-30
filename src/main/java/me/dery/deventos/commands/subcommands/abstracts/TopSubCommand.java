package me.dery.deventos.commands.subcommands.abstracts;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.dery.deventos.enums.subcommands.SubCommands;

public abstract class TopSubCommand extends SubCommand {

	protected final SimpleDateFormat sdf;

	public TopSubCommand(SubCommands type) {
		super(type);

		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

}
