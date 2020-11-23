package me.dery.deventos.listeners.eventslisteners.required;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;

public class EspectadorDamageListener implements Listener {

	private final EventsManager eventsManager;

	public EspectadorDamageListener(DEventos instance) { eventsManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void espectadorDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().hasPermission("deventos.admin"))
				return;

			for (Event event : eventsManager.getEmAndamento()) {
				if (event.getEspectadores().contains(e.getEntity().getName())) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
