package me.dery.deventos.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;

public class ComandoEventos implements CommandExecutor {

	private final DEventos instance;

	public ComandoEventos(DEventos instance) {

		this.instance = instance;

		instance.getCommand("eventos").setExecutor(this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		instance.getConfig().getStringList("Mensagem.Eventos")
						.forEach(s -> sender.sendMessage(s.replace("&", "§")));

		return false;

	}

}
