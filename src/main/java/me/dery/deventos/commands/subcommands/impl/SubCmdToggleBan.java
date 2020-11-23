package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.others.BanAction;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventsManager;

public class SubCmdToggleBan extends SubCommand {

	public SubCmdToggleBan(SubCommands type) { super(type); }

	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length <= 2) {
				sendArgsError(instance, sender);
				return true;
			}

			Player player = instance.getServer().getPlayer(args[1]);

			if (player == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline")
					.replace("&", "§").replace("{player}", args[1]));
				return true;
			}

			EventsManager eventsManager = instance.getEventosManager();

			Event event = eventsManager.getEventoByName(args[2]);

			if (event == null) {
				sender.sendMessage(
					instance.getConfig().getString(
						"Mensagem.Erro.Evento_Invalido").replace("&", "§").replace("{evento}", args[2]));
				return true;
			}

			String[] mensagens;
			if (type == SubCommands.BAN)
				mensagens = new String[] {"Baniu", "Ja_Esta_Banido", "Erro_Banir"};
			else
				mensagens = new String[] {"Desbaniu", "Nao_Esta_Banido", "Erro_Desbanir"};

			try {

				if (eventsManager.togglePlayerBanStatus(BanAction.valueOf(type.name()), player.getName(), event))
					sender.sendMessage(
						instance.getConfig().getString("Mensagem.Sucesso." + mensagens[0])
							.replace("&", "§").replace("{evento}", event.getNome())
							.replace("{player}", player.getName()));
				else
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + mensagens[1])
						.replace("&", "§").replace("{evento}", event.getNome())
						.replace("{player}", player.getName()));

			} catch (IOException e) {
				sender.sendMessage(
					instance.getConfig().getString("Mensagem.Erro." + mensagens[2]).replace("&", "§")
						.replace("{evento}", event.getNome())
						.replace("{player}", player.getName()));
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
