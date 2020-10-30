package me.dery.deventos.listeners.eventslisteners.required;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class BuildListener implements Listener {

	private final DEventos instance;

	private final EventosManager eventosManager;

	public BuildListener(DEventos instance) {
		this.instance = instance;

		eventosManager = instance.getEventosManager();
	}

	@EventHandler(ignoreCancelled = true)
	public void Build(BlockPlaceEvent e) { checkAndCancel(e, e.getPlayer()); }

	@EventHandler(ignoreCancelled = true)
	public void Build(BlockBreakEvent e) { checkAndCancel(e, e.getPlayer()); }

	@EventHandler(ignoreCancelled = true)
	public void Build(PlayerBucketFillEvent e) { checkAndCancel(e, e.getPlayer()); }

	@EventHandler(ignoreCancelled = true)
	public void Build(PlayerBucketEmptyEvent e) { checkAndCancel(e, e.getPlayer()); }

	private void checkAndCancel(Cancellable event, Player player) {
		if (player.hasPermission("deventos.admin"))
			return;

		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(player.getName()) || evento.getEspectadores().contains(player.getName())) {
				event.setCancelled(true);
				player.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Build").replace("&", "§"));
			}
		}
	}

}
