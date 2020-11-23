package me.dery.deventos.commands.subcommands.impl;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import me.dery.deventos.DEventos;
import me.dery.deventos.commands.subcommands.abstracts.PlayerSubCommand;
import me.dery.deventos.enums.events.EventState;
import me.dery.deventos.enums.events.EventStopReason;
import me.dery.deventos.enums.subcommands.SubCommands;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent.QuitReason;

public class SubCmdQuit extends PlayerSubCommand {

	public SubCmdQuit(SubCommands type) { super(type); }

	@Override
	public boolean exec(DEventos instance, CommandSender sender, String[] args) {

		if (sender.hasPermission("deventos.player") || sender.hasPermission("deventos." + type.permissao)
			|| sender.hasPermission("deventos.admin")) {

			EventsManager eventsManager = instance.getEventosManager();

			Event event = eventsManager.getEventoByPlayer(sender);
			List<String> removeFrom;

			if (event != null) {
				removeFrom = event.getPlayers();
			} else {

				event = eventsManager.getEventoByEspectador(sender);

				if (event == null) {
					sender.sendMessage(
						instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
					return true;
				} else {
					removeFrom = event.getEspectadores();
				}

			}

			try {
				eventsManager.removePlayerFromEvent(sender.getName(), event, removeFrom, true);

				if (event.getEventoState().equals(EventState.EMANDAMENTO)) {
					if (event.getPlayers().size() == 1 && event.ultimoEventoGanha())
						try {
							instance.getEventosStateManager().stopEventoWithWinner(event,
								instance.getServer().getPlayer(event.getPlayers().get(0)));
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
						}
					else if (event.getPlayers().size() == 0)
						instance.getEventosStateManager().stopEventoWithoutWinner(event, EventStopReason.SEMVENCEDOR);
				}
			} catch (EventoException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mundo_Invalido")
					.replace("&", "§"));
				return true;
			}

			DEPlayerQuitEvent quit = new DEPlayerQuitEvent((Player) sender, event, QuitReason.NATURAL);
			instance.getServer().getPluginManager().callEvent(quit);

			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§")
				.replace("{evento}", event.getNome()));
		} else {
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Sair").replace("&", "§"));
			return true;
		}

		return false;
	}

}
