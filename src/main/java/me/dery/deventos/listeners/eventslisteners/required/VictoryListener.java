package me.dery.deventos.listeners.eventslisteners.required;

import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventState;
import me.dery.deventos.enums.eventos.EventStopReason;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent.QuitReason;

public class VictoryListener implements Listener {

	private final DEventos instance;

	private final EventsManager eventsManager;

	public VictoryListener(DEventos instance) {
		this.instance = instance;

		eventsManager = instance.getEventosManager();
	}

	@EventHandler
	public void Quit(PlayerQuitEvent e) {

		Player player = e.getPlayer();

		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, event, QuitReason.QUIT);
					instance.getServer().getPluginManager().callEvent(quit);

					eventsManager.removePlayerFromEvent(player.getName(), event, event.getPlayers(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (event.getEventoState().equals(EventState.EMANDAMENTO)) {
					int size = event.getPlayers().size();

					try {
						if (size == 1 && event.ultimoEventoGanha())
							instance.getEventosStateManager().stopEventoWithWinner(event,
								instance.getServer().getPlayer(event.getPlayers().get(0)));
						else if (size == 0)
							instance.getEventosStateManager().stopEventoWithoutWinner(event,
								EventStopReason.SEMVENCEDOR);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : event.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Desconectou")
							.replace("&", "§").replace("{player}", player.getName()));

				break;
			} else if (event.getEspectadores().contains(player.getName())) {

				try {
					eventsManager.removePlayerFromEvent(player.getName(), event, event.getEspectadores(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				break;
			}
		}

	}

	@EventHandler
	public void Kick(PlayerKickEvent e) {

		Player player = e.getPlayer();

		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, event, QuitReason.KICK);
					instance.getServer().getPluginManager().callEvent(quit);

					eventsManager.removePlayerFromEvent(player.getName(), event, event.getPlayers(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (event.getEventoState().equals(EventState.EMANDAMENTO)) {
					int size = event.getPlayers().size();

					try {
						if (size == 1 && event.ultimoEventoGanha())
							instance.getEventosStateManager().stopEventoWithWinner(event,
								instance.getServer().getPlayer(event.getPlayers().get(0)));
						else if (size == 0)
							instance.getEventosStateManager().stopEventoWithoutWinner(event,
								EventStopReason.SEMVENCEDOR);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : event.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Desconectou")
							.replace("&", "§").replace("{player}", player.getName()));

				break;
			} else if (event.getEspectadores().contains(player.getName())) {

				try {
					eventsManager.removePlayerFromEvent(player.getName(), event, event.getEspectadores(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				break;
			}
		}

	}

	@EventHandler
	public void Death(PlayerDeathEvent e) {

		Player player = e.getEntity();

		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, event, QuitReason.DEATH);
					instance.getServer().getPluginManager().callEvent(quit);

					eventsManager.removePlayerFromEvent(player.getName(), event, event.getPlayers(), false);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (event.getEventoState().equals(EventState.EMANDAMENTO)) {
					int size = event.getPlayers().size();

					try {
						if (size == 1 && event.ultimoVivoGanha()) {
							instance.getEventosStateManager().stopEventoWithWinner(event,
								instance.getServer().getPlayer(event.getPlayers().get(0)));
							event.getRespawn().add(player.getName());
						} else if (size == 0) {
							instance.getEventosStateManager().stopEventoWithoutWinner(event,
								EventStopReason.SEMVENCEDOR);
							event.getRespawn().add(player.getName());
						} else if (size >= 2 && event.ativarEspectador()) {
							event.getEspectadores().add(player.getName());
						} else {
							event.getRespawn().add(player.getName());
						}
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : event.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Morreu")
							.replace("&", "§").replace("{player}", player.getName()));
				break;
			} else if (event.getEspectadores().contains(player.getName())) {

				e.getDrops().clear();
				try {
					eventsManager.removePlayerFromEvent(player.getName(), event, event.getEspectadores(), false);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				event.getRespawn().add(player.getName());
				break;
			}
		}
	}

}
