package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class SubCmdSetChest extends PlayerSubCommand {

	public SubCmdSetChest(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			EventosManager eventosManager = instance.getEventosManager();

			Evento evento = eventosManager.getEventoByName(args[1]);

			if (evento == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}

			FileConfiguration config = eventosManager.getEventoConfig(evento);
			ConfigurationSection bausSection = config.getConfigurationSection(EventoProperty.BAUS.keyInConfig);
			int bau;
			if (bausSection == null)
				bau = 1;
			else
				bau = Integer.parseInt(
					bausSection.getKeys(false).toArray(new String[] {})[bausSection.getKeys(false).size() - 1]
						.replace("Bau_", ""))
					+ 1;

			config.set(EventoProperty.BAUS.keyInConfig + ".Bau_" + bau + ".Location",
				instance.getLocationUtils().serializeLocation(((Player) sender).getLocation(), false));
			config.set(EventoProperty.BAUS.keyInConfig + ".Bau_" + bau + ".Items",
				eventosManager.getDefaultChestItems());

			try {
				config.save(evento.getFileEvento());

				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Bau_Setado")
					.replace("{bau}", bau + "").replace("&", "§").replace("{evento}", evento.getNome()));
			} catch (IOException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.SetChest")
					.replace("&", "§").replace("{evento}", evento.getNome()));
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
