package me.dery.deventos.commands.subcommands.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.TopSubCommand;
import me.dery.deventos.enums.subcommands.SubCommands;

public class SubCmdTopDerrotas extends TopSubCommand {

	public SubCmdTopDerrotas(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos.player") || sender.hasPermission("deventos." + type.permissao)
			|| sender.hasPermission("deventos.admin")) {

			LinkedHashMap<String, Integer> topderrotas = instance.getDBManager().getTopDerrotas();

			if (topderrotas == null || topderrotas.size() == 0) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.TOP_Derrotas_Nao_Definido")
					.replace("&", "§"));
				return true;
			}

			sender.sendMessage(instance.getConfig().getString("Config.Rank.TOP_Derrotas_Titulo").replace("&", "§"));

			int i = 1;

			for (Entry<String, Integer> entry : topderrotas.entrySet())
				sender.sendMessage(instance.getConfig().getString("Config.Rank.TOP_Derrotas_Format")
					.replace("&", "§").replace("{posicao}", (i++) + "")
					.replace("{player}", entry.getKey()).replace("{derrotas}", entry.getValue() + ""));

			sender.sendMessage(instance.getConfig().getString("Config.Rank.TOP_Derrotas_Fechamento")
				.replace("&", "§")
				.replace("{ultimaatualizacao}", sdf.format(new Date(instance.getDBManager().getLastUpdate()))));
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§")
				.replace("{1}", args[0]));
			return true;
		}

		return false;
	}

}
