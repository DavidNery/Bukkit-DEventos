package me.dery.deventos.commands.subcommands.impl;

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

public class SubCmdEntrar extends PlayerSubCommand {

	public SubCmdEntrar(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		InventoryUtils inventoryUtils = instance.getInventoryUtils();

		if (inventoryUtils.hasInventory((Player) sender)) {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Recuperar_Itens").replace("&", "§"));
			return true;
		}

		EventosManager eventosManager = instance.getEventosManager();
		Evento evento;

		if (args.length <= 1) {
			if (eventosManager.getEmAndamento().size() == 1) {
				evento = eventosManager.getEmAndamento().get(0);
			} else {
				sendArgsError(instance, sender);
				return true;
			}
		} else {
			evento = eventosManager.getEventoByName(args[1]);

			if (evento == null) {

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;

			}
		}

		if (eventosManager.getEventoByPlayer(sender) != null) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_Um_Evento").replace("&", "§"));
			return true;

		} else if (evento.getEventoState() != EventoState.INICIANDO) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto")
				.replace("&", "§").replace("{evento}", evento.getNome()));
			return true;

		} else if (!sender.hasPermission(evento.getPermissao()) && !sender.hasPermission("deventos.admin")) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Entrar")
				.replace("&", "§").replace("{evento}", evento.getNome()));
			return true;

		} else if (eventosManager.isBan(sender.getName(), evento)) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Banido").replace("&", "§"));
			return true;

		} else if (evento.requireInvVazio() && !inventoryUtils.isVazio((Player) sender)) {

			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inv_Vazio")
				.replace("&", "§").replace("{evento}", evento.getNome()));
			return true;

		} else if (evento.getMaxPlayers() != 0 && evento.getPlayers().size() >= evento.getMaxPlayers()) {
			if (!evento.byPassMax() || !sender.hasPermission(evento.getPermissaoByPass())) {

				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Lotato")
					.replace("&", "§").replace("{evento}", evento.getNome()));
				return true;

			}
		}

		try {
			eventosManager.addPlayerInEvent((Player) sender, evento);
		} catch (EventoException e) {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro." + e.getError()).replace("&", "§"));
		}

		return false;
	}

}
