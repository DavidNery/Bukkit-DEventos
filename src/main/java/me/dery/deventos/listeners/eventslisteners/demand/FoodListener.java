package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class FoodListener implements Listener {

	private final EventosManager eventosManager;

	public FoodListener(DEventos instance) { eventosManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void foodControl(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			for (Evento evento : eventosManager.getEmAndamento()) {
				if (evento.desativarFome() &&
					(evento.getPlayers().contains(p.getName()) || evento.getEspectadores().contains(p.getName()))) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
