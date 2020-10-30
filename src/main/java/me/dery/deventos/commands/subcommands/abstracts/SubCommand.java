package me.dery.deventos.commands.subcommands.abstracts;

import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.subcommands.SubCommands;

public abstract class SubCommand {

	protected SubCommands type;

	public SubCommand(SubCommands type) { this.type = type; }

	public abstract boolean exec(DEventos instance, CommandSender sender, String[] args);

	public void sendArgsError(DEventos instance, CommandSender sender) {
		sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + type.argsError)
			.replace("&", "§"));
	}

}
