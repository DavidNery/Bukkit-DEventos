package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.eventos.EventoStopReason;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent.QuitReason;

public class SubCmdSair extends PlayerSubCommand {

	public SubCmdSair(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos.player") || sender.hasPermission("deventos." + type.permissao)
			|| sender.hasPermission("deventos.admin")) {

			EventosManager eventosManager = instance.getEventosManager();

			Evento evento = eventosManager.getEventoByPlayer(sender);
			List<String> removeFrom;

			if (evento != null) {
				removeFrom = evento.getPlayers();
			} else {

				evento = eventosManager.getEventoByEspectador(sender);

				if (evento == null) {
					sender.sendMessage(
						instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
					return true;
				} else {
					removeFrom = evento.getEspectadores();
				}

			}

			try {
				eventosManager.removePlayerFromEvent(sender.getName(), evento, removeFrom, true);

				if (evento.getEventoState().equals(EventoState.EMANDAMENTO)) {
					if (evento.getPlayers().size() == 1 && evento.ultimoEventoGanha())
						try {
							instance.getEventosStateManager().stopEventoWithWinner(evento,
								instance.getServer().getPlayer(evento.getPlayers().get(0)));
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
						}
					else if (evento.getPlayers().size() == 0)
						instance.getEventosStateManager().stopEventoWithoutWinner(evento, EventoStopReason.SEMVENCEDOR);
				}
			} catch (EventoException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mundo_Invalido")
					.replace("&", "§"));
				return true;
			}

			DEPlayerQuitEvent quit = new DEPlayerQuitEvent((Player) sender, evento, QuitReason.NATURAL);
			instance.getServer().getPluginManager().callEvent(quit);

			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§")
				.replace("{evento}", evento.getNome()));
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Sair").replace("&", "§"));
			return true;
		}

		return false;
	}

}
