package me.dery.deventos.listeners.eventslisteners.demand;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventState;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.objects.Event;

public class PVPListener implements Listener {

	private final DEventos instance;

	private final EventsManager eventsManager;

	public PVPListener(DEventos instance) {
		this.instance = instance;

		eventsManager = instance.getEventosManager();
	}

	@EventHandler(ignoreCancelled = true)
	public void PvP(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player) {

			Player player = (Player) e.getEntity(), damager;

			if (e.getDamager() instanceof Projectile) {
				ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
				if (shooter instanceof Player)
					damager = (Player) shooter;
				else
					damager = null;
			} else {
				Entity entity = e.getDamager();
				if (entity instanceof Player)
					damager = (Player) entity;
				else
					damager = null;
			}

			if (damager != null && damager.hasPermission("deventos.admin"))
				return;

			for (Event event : eventsManager.getEmAndamento()) {
				if (event.getPlayers().contains(player.getName())) {

					if (damager != null) {
						if (event.desativarPvp()) {

							e.setCancelled(true);
							damager.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_PvP")
								.replace("&", "§"));

						} else if (event.getEventoState() == EventState.INICIANDO) {

							e.setCancelled(true);
							damager.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Ainda_Nao_Ativo")
								.replace("&", "§"));

						}
					} else {
						// Se não foi um player que hitou o jogador, cancelar de todo jeito o ataque
						e.setCancelled(true);
					}

					break;

				} else if (damager != null && event.getEspectadores().contains(damager.getName())) {

					e.setCancelled(true);
					damager.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_PvP_Espectador")
						.replace("&", "§"));
					break;

				}
			}
		}

	}

}
