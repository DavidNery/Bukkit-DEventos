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
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.enums.eventos.EventoStopReason;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent;
import me.dery.deventos.pluginlisteners.DEPlayerQuitEvent.QuitReason;

public class VictoryListener implements Listener {

	private final DEventos instance;

	private final EventosManager eventosManager;

	public VictoryListener(DEventos instance) {
		this.instance = instance;

		eventosManager = instance.getEventosManager();
	}

	@EventHandler
	public void Quit(PlayerQuitEvent e) {

		Player player = e.getPlayer();

		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, evento, QuitReason.QUIT);
					instance.getServer().getPluginManager().callEvent(quit);

					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getPlayers(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (evento.getEventoState().equals(EventoState.EMANDAMENTO)) {
					int size = evento.getPlayers().size();

					try {
						if (size == 1 && evento.ultimoEventoGanha())
							instance.getEventosStateManager().stopEventoWithWinner(evento,
								instance.getServer().getPlayer(evento.getPlayers().get(0)));
						else if (size == 0)
							instance.getEventosStateManager().stopEventoWithoutWinner(evento,
								EventoStopReason.SEMVENCEDOR);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : evento.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Desconectou")
							.replace("&", "§").replace("{player}", player.getName()));

				break;
			} else if (evento.getEspectadores().contains(player.getName())) {

				try {
					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getEspectadores(), true);
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

		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, evento, QuitReason.KICK);
					instance.getServer().getPluginManager().callEvent(quit);

					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getPlayers(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (evento.getEventoState().equals(EventoState.EMANDAMENTO)) {
					int size = evento.getPlayers().size();

					try {
						if (size == 1 && evento.ultimoEventoGanha())
							instance.getEventosStateManager().stopEventoWithWinner(evento,
								instance.getServer().getPlayer(evento.getPlayers().get(0)));
						else if (size == 0)
							instance.getEventosStateManager().stopEventoWithoutWinner(evento,
								EventoStopReason.SEMVENCEDOR);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : evento.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Desconectou")
							.replace("&", "§").replace("{player}", player.getName()));

				break;
			} else if (evento.getEspectadores().contains(player.getName())) {

				try {
					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getEspectadores(), true);
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

		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(player.getName())) {

				try {
					DEPlayerQuitEvent quit = new DEPlayerQuitEvent(player, evento, QuitReason.DEATH);
					instance.getServer().getPluginManager().callEvent(quit);

					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getPlayers(), false);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				if (evento.getEventoState().equals(EventoState.EMANDAMENTO)) {
					int size = evento.getPlayers().size();

					try {
						if (size == 1 && evento.ultimoVivoGanha()) {
							instance.getEventosStateManager().stopEventoWithWinner(evento,
								instance.getServer().getPlayer(evento.getPlayers().get(0)));
							evento.getRespawn().add(player.getName());
						} else if (size == 0) {
							instance.getEventosStateManager().stopEventoWithoutWinner(evento,
								EventoStopReason.SEMVENCEDOR);
							evento.getRespawn().add(player.getName());
						} else if (size >= 2 && evento.ativarEspectador()) {
							evento.getEspectadores().add(player.getName());
						} else {
							evento.getRespawn().add(player.getName());
						}
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
				}

				for (String players : evento.getPlayers())
					instance.getServer().getPlayer(players)
						.sendMessage(instance.getConfig().getString("Mensagem.Player_Morreu")
							.replace("&", "§").replace("{player}", player.getName()));
				break;
			} else if (evento.getEspectadores().contains(player.getName())) {

				e.getDrops().clear();
				try {
					eventosManager.removePlayerFromEvent(player.getName(), evento, evento.getEspectadores(), false);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

				evento.getRespawn().add(player.getName());
				break;
			}
		}
	}

}
