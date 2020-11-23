package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.events.EventProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventsManager;

public class SubCmdSetChest extends PlayerSubCommand {

	public SubCmdSetChest(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos." + type.permissao) || sender.hasPermission("deventos.admin")) {

			if (args.length == 1) {
				sendArgsError(instance, sender);
				return true;
			}

			EventsManager eventsManager = instance.getEventosManager();

			Event event = eventsManager.getEventoByName(args[1]);

			if (event == null) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido")
					.replace("&", "§").replace("{evento}", args[1]));
				return true;
			}

			FileConfiguration config = eventsManager.getEventoConfig(event);
			ConfigurationSection bausSection = config.getConfigurationSection(EventProperty.CHESTS.keyInConfig);
			int bau;
			if (bausSection == null)
				bau = 1;
			else
				bau = Integer.parseInt(
					bausSection.getKeys(false).toArray(new String[] {})[bausSection.getKeys(false).size() - 1]
						.replace("Bau_", ""))
					+ 1;

			config.set(EventProperty.CHESTS.keyInConfig + ".Bau_" + bau + ".Location",
				instance.getLocationUtils().serializeLocation(((Player) sender).getLocation(), false));
			config.set(EventProperty.CHESTS.keyInConfig + ".Bau_" + bau + ".Items",
				eventsManager.getDefaultChestItems());

			try {
				config.save(event.getFileEvento());

				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Bau_Setado")
					.replace("{bau}", bau + "").replace("&", "§").replace("{evento}", event.getNome()));
			} catch (IOException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.SetChest")
					.replace("&", "§").replace("{evento}", event.getNome()));
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
