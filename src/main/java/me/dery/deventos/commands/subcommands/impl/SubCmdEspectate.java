package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

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

public class SubCmdEspectate extends PlayerSubCommand {

	public SubCmdEspectate(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		InventoryUtils inventoryUtils = instance.getInventoryUtils();

		if (inventoryUtils.hasInventory((Player) sender)) {
			sender.sendMessage(
				instance.getConfig().getString("Mensagem.Erro.Recuperar_Itens").replace("&", "§"));
			return true;
		}

		EventsManager eventsManager = instance.getEventosManager();
		Event event;

		if (args.length == 1) {
			if (eventsManager.getEmAndamento().size() > 1) {
				sendArgsError(instance, sender);
				return true;
			} else {
				event = eventsManager.getEmAndamento().get(0);
			}
		} else {
			event = eventsManager.getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}
		}

		if (eventsManager.getEventoByPlayer(sender) != null || eventsManager.getEventoByEspectador(sender) != null) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_Um_Evento").replace("&", "§"));
			return true;

		} else if (event.getEventoState() == EventState.FECHADO) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§")
				.replace("{evento}", event.getNome()));
			return true;

		} else if (!sender.hasPermission(event.getPermissaoEspectar()) && !sender.hasPermission("deventos.admin")) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Espectar")
				.replace("&", "§").replace("{evento}", event.getNome()));
			return true;

		} else if (!event.ativarEspectador()) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Espectador_Desativado")
				.replace("&", "§").replace("{evento}", event.getNome()));
			return true;

		} else {
			try {
				inventoryUtils.saveInventory((Player) sender);
			} catch (IOException e1) {
				e1.printStackTrace();
				return true;
			}

			try {
				eventsManager.addPlayerInEspectadorEvent((Player) sender, event, true);
			} catch (EventoException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + e.getError()).replace("&", "§"));
				return true;
			}
		}

		return false;
	}

}
