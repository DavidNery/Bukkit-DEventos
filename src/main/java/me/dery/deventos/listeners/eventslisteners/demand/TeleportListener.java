package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;
import me.dery.deventos.utils.LocationUtils;

public class TeleportListener implements Listener {

	private final DEventos instance;

	private final EventsManager eventsManager;

	private final LocationUtils locationUtils;

	public TeleportListener(DEventos instance) {
		this.instance = instance;

		eventsManager = instance.getEventosManager();

		locationUtils = instance.getLocationUtils();
	}

	@EventHandler
	public void Teleport(PlayerTeleportEvent e) {

		final Player p = e.getPlayer();
		if (p.hasPermission("deventos.bypasstp") || p.hasPermission("deventos.admin"))
			return;

		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getPlayers().contains(p.getName()) || event.getEspectadores().contains(p.getName())) {

				if (e.getTo().equals(locationUtils.deserializeLocation(event.getSpawn()))
					|| e.getTo().equals(locationUtils.deserializeLocation(event.getExit()))
					|| (event.ativarLobby()
						&& e.getTo().equals(locationUtils.deserializeLocation(event.getLobby())))
					|| (event.ativarEspectador()
						&& e.getTo().equals(locationUtils.deserializeLocation(event.getEspectador())))
					|| e.getTo().equals(e.getFrom()))
					return;

				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Teleport").replace("&", "§"));
			}
		}

	}

}
