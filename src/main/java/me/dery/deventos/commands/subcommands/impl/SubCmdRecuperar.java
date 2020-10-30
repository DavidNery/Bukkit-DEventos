package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.utils.InventoryUtils;

public class SubCmdRecuperar extends PlayerSubCommand {

	public SubCmdRecuperar(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		InventoryUtils inventoryUtils = instance.getInventoryUtils();

		if (!inventoryUtils.isVazio((Player) sender)) {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Recuperar_Inv_Vazio").replace("&", "§"));
			return true;
		}

		try {
			if (inventoryUtils.restoreInventory((Player) sender)) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Recuperou_Itens")
					.replace("&", "§"));
			} else {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Itens_Recuperar")
					.replace("&", "§"));
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

}
