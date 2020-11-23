package me.dery.deventos.listeners;

import java.io.IOException;

import me.dery.deventos.objects.Event;
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
import me.dery.deventos.enums.eventos.EventState;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.managers.EventsManager;
import me.dery.deventos.managers.EventsStateManager;

public class MainListeners implements Listener {

	private final DEventos instance;

	private final EventsManager eventsManager;

	private final EventsStateManager eventsStateManager;

	public MainListeners(DEventos instance) {
		this.instance = instance;

		eventsManager = instance.getEventosManager();
		eventsStateManager = instance.getEventosStateManager();

		instance.getServer().getPluginManager().registerEvents(this, instance);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();

		for (Event eventos : eventsManager.getEmAndamento()) {
			if (eventos.ativarEspectador()) {
				if (eventos.getEspectadores().contains(p.getName())) {
					new BukkitRunnable() {
						public void run() {
							try {
								eventsManager.addPlayerInEspectadorEvent(p, eventos, false);
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

		for (Event eventos : eventsManager.getLoadedEventos()) {
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
				Event event = eventsManager.getEventoByName(e.getLine(1));
				if (event == null) {
					e.getBlock().breakNaturally();
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Invalido").replace("&", "§")
						.replace("{evento}", e.getLine(1)));
					return;
				}
				e.setLine(0, "§3§l[Evento]");
				e.setLine(1, "§f§l" + event.getNome());
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

				Event event = eventsManager.getEventoByEspectador(p);

				if (event == null) {
					p.setItemInHand(null);
					if (p.getInventory().getHelmet() != null
						&& p.getInventory().getHelmet().getType() == Material.SKULL_ITEM)
						p.getInventory().setHelmet(null);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					return;
				}

				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§")
					.replace("{evento}", event.getNome()));

				try {
					eventsManager.removePlayerFromEvent(p.getName(), event, event.getEspectadores(), true);
				} catch (EventoException e1) {
					e1.printStackTrace();
				}

			} else if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.WALL_SIGN
				|| e.getClickedBlock().getType() == Material.SIGN_POST)) {

				Sign sign = (Sign) e.getClickedBlock().getState();

				if (sign.getLine(0).equalsIgnoreCase("§3§l[Evento]") && !sign.getLine(1).isEmpty()
					&& !sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {

					e.setCancelled(true);

					Event event = eventsManager.getEventoByPlayer(p);

					if (event != null) {
						if (!event.getNome().equalsIgnoreCase(sign.getLine(1).replace("§f§l", ""))) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Evento")
								.replace("&", "§"));

						} else if (event.getEventoState() != EventState.EMANDAMENTO) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Ocorrendo")
								.replace("&", "§"));

						} else if (event.getBlock() != null) {

							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Passar_Bloco")
								.replace("{bloco}", event.getBlock().name()).replace("&", "§"));

						} else {

							sign.setLine(2, "§0§l" + p.getName());
							sign.update();

							try {
								eventsStateManager.stopEventoWithWinner(event, p);
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
