package me.dery.deventos.listeners.eventslisteners.required;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;

public class DropListener implements Listener {

	private final EventsManager eventsManager;

	public DropListener(DEventos instance) { eventsManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void dropControl(PlayerDropItemEvent e) {
		if (e.getPlayer().hasPermission("deventos.admin"))
			return;
		
		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getEspectadores().contains(e.getPlayer().getName())) {
				e.setCancelled(true);
				break;
			}
		}
	}

}
