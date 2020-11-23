package me.dery.deventos.commands.subcommands.impl;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.events.EventState;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.utils.InventoryUtils;

public class SubCmdJoin extends PlayerSubCommand {

	public SubCmdJoin(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		InventoryUtils inventoryUtils = instance.getInventoryUtils();

		if (inventoryUtils.hasInventory((Player) sender)) {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Recuperar_Itens").replace("&", "§"));
			return true;
		}

		EventsManager eventsManager = instance.getEventosManager();
		Event event;

		if (args.length <= 1) {
			if (eventsManager.getEmAndamento().size() == 1) {
				event = eventsManager.getEmAndamento().get(0);
			} else {
				sendArgsError(instance, sender);
				return true;
			}
		} else {
			event = eventsManager.getEventoByName(args[1]);

			if (event == null) {

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;

			}
		}

		if (eventsManager.getEventoByPlayer(sender) != null) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_Um_Evento").replace("&", "§"));
			return true;

		} else if (event.getEventoState() != EventState.INICIANDO) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto")
				.replace("&", "§").replace("{evento}", event.getNome()));
			return true;

		} else if (!sender.hasPermission(event.getPermissao()) && !sender.hasPermission("deventos.admin")) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Entrar")
				.replace("&", "§").replace("{evento}", event.getNome()));
			return true;

		} else if (eventsManager.isBan(sender.getName(), event)) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Banido").replace("&", "§"));
			return true;

		} else if (event.requireInvVazio() && !inventoryUtils.isVazio((Player) sender)) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inv_Vazio")
				.replace("&", "§").replace("{evento}", event.getNome()));
			return true;

		} else if (event.getMaxPlayers() != 0 && event.getPlayers().size() >= event.getMaxPlayers()) {
			if (!event.byPassMax() || !sender.hasPermission(event.getPermissaoByPass())) {

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Lotato")
					.replace("&", "§").replace("{evento}", event.getNome()));
				return true;

			}
		}

		try {
			eventsManager.addPlayerInEvent((Player) sender, event);
		} catch (EventoException e) {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + e.getError()).replace("&", "§"));
		}

		return false;
	}

}
