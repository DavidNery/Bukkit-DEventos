package me.dery.deventos.commands.subcommands.abstracts;

import me.dery.deventos.enums.subcommands.SubCommands;

/**
 * Necessário implementar a verificação se o CommandSender</br>
 * realmente é um player </br>
 * 
 * <pre>
 * if (!(sender instanceof Player)) {
 * 	instance.getConfig().getStringList("Mensagem.Comandos_Console")
 * 		.forEach(msg -> sender.sendMessage(msg.replace("&", "§")));
 * 	return true;
 * }
 * </pre>
 * 
 * @author david
 */
public abstract class PlayerSubCommand extends SubCommand {

	public PlayerSubCommand(SubCommands type) { super(type); }

}
