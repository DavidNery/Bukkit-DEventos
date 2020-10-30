package me.dery.deventos.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.dery.deventos.DEventos;
import me.dery.deventos.exceptions.EventoException;
import me.dery.deventos.objects.Evento;

public class RunnableManager {

	private final Map<Evento, BukkitTask> blockTasks;

	public RunnableManager() { blockTasks = new HashMap<>(); }

	public void registerBlockTask(DEventos instance, EventosStateManager eventosStateManager, Evento evento) {
		blockTasks.put(evento, new BukkitRunnable() {
			@Override
			public void run() {

				for (String player : evento.getPlayers()) {
					Player bukkitPlayer = instance.getServer().getPlayer(player);
					if (bukkitPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == evento
						.getBlock()) {
						try {
							eventosStateManager.stopEventoWithWinner(evento, bukkitPlayer);
						} catch (IOException | InvalidConfigurationException | EventoException e) {
							e.printStackTrace();
						}

						unregisterBlockTask(evento);
						break;
					}
				}

			}
		}.runTaskTimerAsynchronously(instance, 10, 10));
	}

	public void unregisterBlockTask(Evento evento) {
		blockTasks.get(evento).cancel();
		blockTasks.remove(evento);
	}

}
