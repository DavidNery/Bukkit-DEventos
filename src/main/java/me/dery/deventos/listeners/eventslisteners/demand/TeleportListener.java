package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;
import me.dery.deventos.utils.LocationUtils;

public class TeleportListener implements Listener {

	private final DEventos instance;

	private final EventosManager eventosManager;

	private final LocationUtils locationUtils;

	public TeleportListener(DEventos instance) {
		this.instance = instance;

		eventosManager = instance.getEventosManager();

		locationUtils = instance.getLocationUtils();
	}

	@EventHandler
	public void Teleport(PlayerTeleportEvent e) {

		final Player p = e.getPlayer();
		if (p.hasPermission("deventos.bypasstp") || p.hasPermission("deventos.admin"))
			return;

		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(p.getName()) || evento.getEspectadores().contains(p.getName())) {

				if (e.getTo().equals(locationUtils.deserializeLocation(evento.getSpawn()))
					|| e.getTo().equals(locationUtils.deserializeLocation(evento.getExit()))
					|| (evento.ativarLobby()
						&& e.getTo().equals(locationUtils.deserializeLocation(evento.getLobby())))
					|| (evento.ativarEspectador()
						&& e.getTo().equals(locationUtils.deserializeLocation(evento.getEspectador())))
					|| e.getTo().equals(e.getFrom()))
					return;

				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Teleport").replace("&", "§"));
			}
		}

	}

}
