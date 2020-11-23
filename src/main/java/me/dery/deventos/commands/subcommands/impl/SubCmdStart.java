package me.dery.deventos.commands.subcommands.impl;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.events.EventState;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventsManager;

public class SubCmdStart extends SubCommand {

	public SubCmdStart(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos.iniciar") || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			EventsManager eventsManager = instance.getEventosManager();

			if (!instance.getConfig().getBoolean("Config.Permitir_Mais_De_Um_Evento")) {
				if (eventsManager.getEmAndamento().size() > 0) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Em_Andamento")
						.replace("&", "§"));
					return true;
				}
			}

			Event event = eventsManager.getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (event.getEventoState() != EventState.FECHADO) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Aberto")
					.replace("&", "§").replace("{evento}", event.getNome()));
				return true;
			}

			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Iniciou").replace("&", "§")
				.replace("{evento}", event.getNome()));

			instance.getEventosStateManager().startEvento(event);
		} else {
			sender.sendMessage(
				instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§").replace("{1}",
					args[0]));
		}

		return false;
	}

}
