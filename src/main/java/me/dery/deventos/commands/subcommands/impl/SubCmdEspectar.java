package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.utils.InventoryUtils;

public class SubCmdEspectar extends PlayerSubCommand {

	public SubCmdEspectar(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		InventoryUtils inventoryUtils = instance.getInventoryUtils();

		if (inventoryUtils.hasInventory((Player) sender)) {
			sender.sendMessage(
				instance.getConfig().getString("Mensagem.Erro.Recuperar_Itens").replace("&", "§"));
			return true;
		}

		EventosManager eventosManager = instance.getEventosManager();
		Evento evento;

		if (args.length == 1) {
			if (eventosManager.getEmAndamento().size() > 1) {
				sendArgsError(instance, sender);
				return true;
			} else {
				evento = eventosManager.getEmAndamento().get(0);
			}
		} else {
			evento = eventosManager.getEventoByName(args[1]);

			if (evento == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}
		}

		if (eventosManager.getEventoByPlayer(sender) != null || eventosManager.getEventoByEspectador(sender) != null) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_Um_Evento").replace("&", "§"));
			return true;

		} else if (evento.getEventoState() == EventoState.FECHADO) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§")
				.replace("{evento}", evento.getNome()));
			return true;

		} else if (!sender.hasPermission(evento.getPermissaoEspectar()) && !sender.hasPermission("deventos.admin")) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Espectar")
				.replace("&", "§").replace("{evento}", evento.getNome()));
			return true;

		} else if (!evento.ativarEspectador()) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Espectador_Desativado")
				.replace("&", "§").replace("{evento}", evento.getNome()));
			return true;

		} else {
			try {
				inventoryUtils.saveInventory((Player) sender);
			} catch (IOException e1) {
				e1.printStackTrace();
				return true;
			}

			try {
				eventosManager.addPlayerInEspectadorEvent((Player) sender, evento, true);
			} catch (EventoException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + e.getError()).replace("&", "§"));
				return true;
			}
		}

		return false;
	}

}
