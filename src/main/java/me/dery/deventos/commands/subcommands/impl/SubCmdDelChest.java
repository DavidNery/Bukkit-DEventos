package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventsManager;

public class SubCmdDelChest extends SubCommand {

	public SubCmdDelChest(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			EventsManager eventsManager = instance.getEventosManager();

			Event event = eventsManager.getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}

			FileConfiguration config = eventsManager.getEventoConfig(event);

			if (config.contains("Baus.Bau_" + args[2])) {
				config.set("Baus.Bau_" + args[2], null);

				try {
					config.save(event.getFileEvento());

					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Bau_Deletado")
						.replace("{bau}", args[2]).replace("&", "§").replace("{evento}", event.getNome()));
				} catch (IOException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.SetChest")
						.replace("&", "§").replace("{evento}", event.getNome()));
					return true;
				}
			} else {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Bau_Invalido")
					.replace("&", "§"));
				return true;
			}
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao")
				.replace("&", "§").replace("{1}", args[0]));
			return true;
		}

		return false;
	}

}
