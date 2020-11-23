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
import me.dery.deventos.objects.Event;

public class RunnableManager {

	private final Map<Event, BukkitTask> blockTasks;

	public RunnableManager() { blockTasks = new HashMap<>(); }

	public void registerBlockTask(DEventos instance, EventsStateManager eventsStateManager, Event event) {
		blockTasks.put(event, new BukkitRunnable() {
			@Override
			public void run() {

				for (String player : event.getPlayers()) {
					Player bukkitPlayer = instance.getServer().getPlayer(player);
					if (bukkitPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == event
						.getBlock()) {
						try {
							eventsStateManager.stopEventoWithWinner(event, bukkitPlayer);
						} catch (IOException | InvalidConfigurationException | EventoException e) {
							e.printStackTrace();
						}

						unregisterBlockTask(event);
						break;
					}
				}

			}
		}.runTaskTimerAsynchronously(instance, 10, 10));
	}

	public void unregisterBlockTask(Event event) {
		blockTasks.get(event).cancel();
		blockTasks.remove(event);
	}

}
