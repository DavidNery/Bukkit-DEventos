package me.dery.deventos.commands.subcommands.impl;

import me.dery.deventos.objects.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.events.EventProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventsManager;

public class SubCmdInfo extends SubCommand {

	public SubCmdInfo(SubCommands type) { super(type); }

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

			String msg = String.join("\n", instance.getConfig().getStringList("Mensagem.Sucesso.Info"))
				.replace("&", "§")
				.replace("{evento}", event.getNome())
				.replace("{tag}", event.getTag())
				.replace("{permissao}", event.getPermissao())
				.replace("{permissaobypass}", event.getPermissaoByPass())
				.replace("{permissaoespectar}", event.getPermissaoEspectar())
				.replace("{premio}", event.getPremioFormatado())
				.replace("{players}", event.getPlayers().size() + "")
				.replace("{espectadores}", event.getEspectadores().size() + "")
				.replace("{desativarpvp}", parseBoolean(event.desativarPvp()))
				.replace("{desativardamage}", parseBoolean(event.desativarDamage()))
				.replace("{desativarff}", parseBoolean(event.desativarFF()))
				.replace("{desativarfome}", parseBoolean(event.desativarFome()))
				.replace("{invvazio}", parseBoolean(event.requireInvVazio()))
				.replace("{bypassmax}", parseBoolean(event.byPassMax()))
				.replace("{ultimovivoganha}", parseBoolean(event.ultimoVivoGanha()))
				.replace("{ultimoeventoganha}", parseBoolean(event.ultimoEventoGanha()))
				.replace("{salvarinv}", parseBoolean(event.salvarInv()))
				.replace("{ativarlobby}", parseBoolean(event.ativarLobby()))
				.replace("{ativarespectador}", parseBoolean(event.ativarEspectador()))
				.replace("{tempoanuncios}", event.getTAnuncios() + "")
				.replace("{anuncios}", config.getInt(EventProperty.ANNOUNCEMENTS.keyInConfig) + "")
				.replace("{tempoacabar}", event.getTAcabar() + "")
				.replace("{minplayers}", event.getMinPlayers() + "")
				.replace("{maxplayers}", event.getMaxPlayers() + "")
				.replace("{ultimovencedor}",
					event.getLastWinner().equals("null") ? "ninguém" : event.getLastWinner());
			
			sender.sendMessage(msg);

			return false;
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§")
				.replace("{1}", args[0]));
			return true;
		}
	}

	private String parseBoolean(boolean bool) { return bool ? "sim" : "não"; }

}
