package me.dery.deventos.commands.subcommands.impl;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.events.EventState;
import me.dery.deventos.enums.events.EventStopReason;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;

public class SubCmdStop extends SubCommand {

	public SubCmdStop(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			Event event = instance.getEventosManager().getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido").replace("&", "§")
					.replace("{evento}", args[1]));
				return true;
			} else if (event.getEventoState().equals(EventState.FECHADO)) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§")
					.replace("{evento}", event.getNome()));
				return true;
			}

			try {
				instance.getEventosStateManager().stopEventoWithoutWinner(event, EventStopReason.STAFFCANCELOU);

				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Parou")
					.replace("&", "§").replace("{evento}", event.getNome()));
			} catch (EventoException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + e.getError()).replace("&", "§"));
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao")
				.replace("&", "§").replace("{1}", args[0]));
			return true;
		}

		return false;
	}

}
