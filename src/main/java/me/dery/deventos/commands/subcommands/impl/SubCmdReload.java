package me.dery.deventos.commands.subcommands.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class SubCmdReload extends SubCommand {

	public SubCmdReload(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			EventosManager eventosManager = instance.getEventosManager();

			if (eventosManager.getEmAndamento().size() > 0) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Existe_Ocorrendo").replace("&", "§"));
				return true;
			}

			instance.reloadConfig();

			eventosManager.getLoadedEventos().clear();

			List<Evento> loadedEventos = eventosManager.loadEventos();

			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Eventos_Recarregados")
				.replace("&", "§").replace("{qnt}", loadedEventos.size() + "")
				.replace("{eventos}",
					loadedEventos.stream().map(evento -> evento.getNome()).collect(Collectors.joining(", "))));

			return false;
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§")
				.replace("{1}", args[0]));
			return true;
		}
	}

}
