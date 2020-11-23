package me.dery.deventos.commands;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.commands.subcommands.impl.SubCmdJoin;
import me.dery.deventos.enums.subcommands.SubCommands;

public class CommandEvent implements CommandExecutor {

	private final DEventos instance;

	private final SimpleDateFormat sdf;

	public CommandEvent(DEventos instance) {

		this.instance = instance;

		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

		instance.getCommand("evento").setExecutor(this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		SubCommand subCommand;

		if (args.length == 0) {
			subCommand = new SubCmdJoin(SubCommands.ENTRAR);
		} else {
			SubCommands subCommandType;
			try {
				subCommandType = SubCommands.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				if (sender instanceof Player) {
					for (String msg : instance.getConfig().getStringList("Mensagem.Comandos_Player"))
						sender.sendMessage(msg.replace("&", "§"));

					if (sender.hasPermission("deventos.admin"))
						for (String msg : instance.getConfig().getStringList("Mensagem.Comandos_Staff"))
							sender.sendMessage(msg.replace("&", "§"));
				} else {
					for (String msg : instance.getConfig().getStringList("Mensagem.Comandos_Console"))
						sender.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}

			Class<?> subCommandClass = subCommandType.subCmdClass;
			try {
				subCommand = (SubCommand) subCommandClass.getConstructor(SubCommands.class)
					.newInstance(subCommandType);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return true;
			}
		}

		if (subCommand instanceof PlayerSubCommand && !(sender instanceof Player)) {
			instance.getConfig().getStringList("Mensagem.Comandos_Console")
				.forEach(msg -> sender.sendMessage(msg.replace("&", "§")));
			return true;
		} else {
			return subCommand.exec(instance, sender, args);
		}

	}

}
