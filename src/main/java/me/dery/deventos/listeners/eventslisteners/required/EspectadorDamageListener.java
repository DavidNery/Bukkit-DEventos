package me.dery.deventos.listeners.eventslisteners.required;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class EspectadorDamageListener implements Listener {

	private final EventosManager eventosManager;

	public EspectadorDamageListener(DEventos instance) { eventosManager = instance.getEventosManager(); }

	@EventHandler(ignoreCancelled = true)
	public void espectadorDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().hasPermission("deventos.admin"))
				return;

			for (Evento evento : eventosManager.getEmAndamento()) {
				if (evento.getEspectadores().contains(e.getEntity().getName())) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

}
