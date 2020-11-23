package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;

public class FoodListener implements Listener {

	private final EventsManager eventsManager;

	public FoodListener(DEventos instance) { eventsManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void foodControl(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			for (Event event : eventsManager.getEmAndamento()) {
				if (event.desativarFome() &&
					(event.getPlayers().contains(p.getName()) || event.getEspectadores().contains(p.getName()))) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
