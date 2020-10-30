package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class DamageListener implements Listener {

	private final EventosManager eventosManager;

	public DamageListener(DEventos instance) { eventosManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void playerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().hasPermission("deventos.admin"))
				return;
			
			for (Evento evento : eventosManager.getEmAndamento()) {
				if (evento.desativarDamage() && evento.getPlayers().contains(e.getEntity().getName())) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
