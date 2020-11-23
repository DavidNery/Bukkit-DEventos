package me.dery.deventos.listeners.eventslisteners.demand;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventsManager;

public class DamageListener implements Listener {

	private final EventsManager eventsManager;

	public DamageListener(DEventos instance) { eventsManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void playerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().hasPermission("deventos.admin"))
				return;
			
			for (Event event : eventsManager.getEmAndamento()) {
				if (event.desativarDamage() && event.getPlayers().contains(e.getEntity().getName())) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
