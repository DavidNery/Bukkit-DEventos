package me.dery.deventos.listeners.eventslisteners.demand;

import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventState;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.managers.EventsStateManager;
import me.dery.deventos.objects.Event;

public class BlockListener implements Listener {

	private final EventsManager eventsManager;

	private final EventsStateManager eventsStateManager;

	public BlockListener(DEventos instance) {
		eventsManager = instance.getEventosManager();
		eventsStateManager = instance.getEventosStateManager();
	}

	@EventHandler
	public void Move(PlayerMoveEvent e) {

		Player player = e.getPlayer();

		if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
			|| e.getFrom().getZ() != e.getTo().getZ()) {

			for (Event event : eventsManager.getEmAndamento()) {
				if (event.getBlock() != null
					&& player.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == event.getBlock()
					&& event.getEventoState() == EventState.EMANDAMENTO
					&& event.getPlayers().contains(player.getName())) {
					try {
						eventsStateManager.stopEventoWithWinner(event, player);
					} catch (IOException | InvalidConfigurationException | EventoException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

}
