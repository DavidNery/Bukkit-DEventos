package me.dery.deventos.commands.subcommands.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.SubCommand;
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class SubCmdInfo extends SubCommand {

	public SubCmdInfo(SubCommands type) { super(type); }

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

			String msg = String.join("\n", instance.getConfig().getStringList("Mensagem.Sucesso.Info"))
				.replace("&", "§")
				.replace("{evento}", evento.getNome())
				.replace("{tag}", evento.getTag())
				.replace("{permissao}", evento.getPermissao())
				.replace("{permissaobypass}", evento.getPermissaoByPass())
				.replace("{permissaoespectar}", evento.getPermissaoEspectar())
				.replace("{premio}", evento.getPremioFormatado())
				.replace("{players}", evento.getPlayers().size() + "")
				.replace("{espectadores}", evento.getEspectadores().size() + "")
				.replace("{desativarpvp}", parseBoolean(evento.desativarPvp()))
				.replace("{desativardamage}", parseBoolean(evento.desativarDamage()))
				.replace("{desativarff}", parseBoolean(evento.desativarFF()))
				.replace("{desativarfome}", parseBoolean(evento.desativarFome()))
				.replace("{invvazio}", parseBoolean(evento.requireInvVazio()))
				.replace("{bypassmax}", parseBoolean(evento.byPassMax()))
				.replace("{ultimovivoganha}", parseBoolean(evento.ultimoVivoGanha()))
				.replace("{ultimoeventoganha}", parseBoolean(evento.ultimoEventoGanha()))
				.replace("{salvarinv}", parseBoolean(evento.salvarInv()))
				.replace("{ativarlobby}", parseBoolean(evento.ativarLobby()))
				.replace("{ativarespectador}", parseBoolean(evento.ativarEspectador()))
				.replace("{tempoanuncios}", evento.getTAnuncios() + "")
				.replace("{anuncios}", config.getInt(EventoProperty.ANUNCIOS.keyInConfig) + "")
				.replace("{tempoacabar}", evento.getTAcabar() + "")
				.replace("{minplayers}", evento.getMinPlayers() + "")
				.replace("{maxplayers}", evento.getMaxPlayers() + "")
				.replace("{ultimovencedor}",
					evento.getLastWinner().equals("null") ? "ninguém" : evento.getLastWinner());
			
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
