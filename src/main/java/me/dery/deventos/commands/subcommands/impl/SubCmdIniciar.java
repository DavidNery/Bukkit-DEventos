package me.dery.deventos.commands.subcommands.impl;

import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class SubCmdIniciar extends SubCommand {

	public SubCmdIniciar(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos.iniciar") || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			EventosManager eventosManager = instance.getEventosManager();

			if (!instance.getConfig().getBoolean("Config.Permitir_Mais_De_Um_Evento")) {
				if (eventosManager.getEmAndamento().size() > 0) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Em_Andamento")
						.replace("&", "§"));
					return true;
				}
			}

			Evento evento = eventosManager.getEventoByName(args[1]);

			if (evento == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			} else if (evento.getEventoState() != EventoState.FECHADO) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Aberto")
					.replace("&", "§").replace("{evento}", evento.getNome()));
				return true;
			}

			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Iniciou").replace("&", "§")
				.replace("{evento}", evento.getNome()));

			instance.getEventosStateManager().startEvento(evento);
		} else {
			sender.sendMessage(
				instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§").replace("{1}",
					args[0]));
		}

		return false;
	}

}
