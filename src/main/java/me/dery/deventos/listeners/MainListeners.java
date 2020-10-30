package me.dery.deventos.listeners;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.managers.EventosStateManager;
import me.dery.deventos.objects.Evento;

public class MainListeners implements Listener {

	private final DEventos instance;

	private final EventosManager eventosManager;

	private final EventosStateManager eventosStateManager;

	public MainListeners(DEventos instance) {
		this.instance = instance;

		eventosManager = instance.getEventosManager();
		eventosStateManager = instance.getEventosStateManager();

		instance.getServer().getPluginManager().registerEvents(this, instance);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();

		for (Evento eventos : eventosManager.getEmAndamento()) {
			if (eventos.ativarEspectador()) {
				if (eventos.getEspectadores().contains(p.getName())) {
					new BukkitRunnable() {
						public void run() {
							try {
								eventosManager.addPlayerInEspectadorEvent(p, eventos, false);
							} catch (EventoException e) {
								e.printStackTrace();
							}
							p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
						}
					}.runTaskLater(instance, 2L);

					return;
				}
			}
		}

		for (Evento eventos : eventosManager.getLoadedEventos()) {
			if (eventos.getRespawn().contains(p.getName())) {
				e.setRespawnLocation(instance.getLocationUtils().deserializeLocation(eventos.getExit()));
				eventos.getRespawn().remove(p.getName());

				break;
			}
		}
	}

	@EventHandler
	public void criarPlaca(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[Evento]") && !e.getLine(1).isEmpty()) {
			Player p = e.getPlayer();
			if (p.hasPermission("deventos.placa")) {
				Evento evento = eventosManager.getEventoByName(e.getLine(1));
				if (evento == null) {
					e.getBlock().breakNaturally();
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido").replace("&", "§")
						.replace("{evento}", e.getLine(1)));
					return;
				}
				e.setLine(0, "§3§l[Evento]");
				e.setLine(1, "§f§l" + evento.getNome());
				e.setLine(2, "§0§l------");
			} else {
				e.getBlock().breakNaturally();
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Placa").replace("&", "§"));
				return;
			}
		}
	}

	@EventHandler
	public void eventoItemsClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()
				&& p.getItemInHand().getItemMeta().hasDisplayName()
				&& p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cClique para sair")) {

				e.setCancelled(true);

				Evento evento = eventosManager.getEventoByEspectador(p);

				if (evento == null) {
					p.setItemInHand(null);
					if (p.getInventory().getHelmet() != null
						&& p.getInventory().getHelmet().getType() == Material.SKULL_ITEM)
						p.getInventory().setHelmet(null);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					return;
				}

				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§")
					.replace("{evento}", evento.getNome()));

				try {
					eventosManager.removePlayerFromEvent(p.getName(), evento, evento.getEspectadores(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

			} else if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.WALL_SIGN
				|| e.getClickedBlock().getType() == Material.SIGN_POST)) {

				Sign sign = (Sign) e.getClickedBlock().getState();

				if (sign.getLine(0).equalsIgnoreCase("§3§l[Evento]") && !sign.getLine(1).isEmpty()
					&& !sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {

					e.setCancelled(true);

					Evento evento = eventosManager.getEventoByPlayer(p);

					if (evento != null) {
						if (!evento.getNome().equalsIgnoreCase(sign.getLine(1).replace("§f§l", ""))) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Evento")
								.replace("&", "§"));

						} else if (evento.getEventoState() != EventoState.EMANDAMENTO) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Ocorrendo")
								.replace("&", "§"));

						} else if (evento.getBlock() != null) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Passar_Bloco")
								.replace("{bloco}", evento.getBlock().name()).replace("&", "§"));

						} else {

							sign.setLine(2, "§0§l" + p.getName());
							sign.update();

							try {
								eventosStateManager.stopEventoWithWinner(evento, p);
							} catch (IOException | InvalidConfigurationException | EventoException e1) {
								e1.printStackTrace();
							}

						}
					}
				}
			}
		}
	}

}
