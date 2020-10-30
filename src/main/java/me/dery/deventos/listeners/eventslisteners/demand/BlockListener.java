package me.dery.deventos.listeners.eventslisteners.demand;

import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.managers.EventosStateManager;
import me.dery.deventos.objects.Evento;

public class BlockListener implements Listener {

	private final EventosManager eventosManager;

	private final EventosStateManager eventosStateManager;

	public BlockListener(DEventos instance) {
		eventosManager = instance.getEventosManager();
		eventosStateManager = instance.getEventosStateManager();
	}

	@EventHandler
	public void Move(PlayerMoveEvent e) {

		Player player = e.getPlayer();

		if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
			|| e.getFrom().getZ() != e.getTo().getZ()) {

			for (Evento evento : eventosManager.getEmAndamento()) {
				if (evento.getBlock() != null
					&& player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == evento.getBlock()
					&& evento.getEventoState() == EventoState.EMANDAMENTO
					&& evento.getPlayers().contains(player.getName())) {
					try {
						eventosStateManager.stopEventoWithWinner(evento, player);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

}
