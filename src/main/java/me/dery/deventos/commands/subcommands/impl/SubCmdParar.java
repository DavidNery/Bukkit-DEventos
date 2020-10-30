package me.dery.deventos.commands.subcommands.impl;

import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.eventos.EventoStopReason;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.objects.Evento;

public class SubCmdParar extends SubCommand {

	public SubCmdParar(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			Evento evento = instance.getEventosManager().getEventoByName(args[1]);

			if (evento == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido").replace("&", "§")
					.replace("{evento}", args[1]));
				return true;
			} else if (evento.getEventoState().equals(EventoState.FECHADO)) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§")
					.replace("{evento}", evento.getNome()));
				return true;
			}

			try {
				instance.getEventosStateManager().stopEventoWithoutWinner(evento, EventoStopReason.STAFFCANCELOU);

				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Parou")
					.replace("&", "§").replace("{evento}", evento.getNome()));
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
