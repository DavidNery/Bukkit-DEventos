package me.dery.deventos.listeners.eventslisteners.required;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class DropListener implements Listener {

	private final EventosManager eventosManager;

	public DropListener(DEventos instance) { eventosManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void dropControl(PlayerDropItemEvent e) {
		if (e.getPlayer().hasPermission("deventos.admin"))
			return;
		
		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getEspectadores().contains(e.getPlayer().getName())) {
				e.setCancelled(true);
				break;
			}
		}
	}

}
