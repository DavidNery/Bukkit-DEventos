package me.dery.deventos.pluginlisteners;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class DEPlayerJoinEvent extends org.bukkit.event.Event {

	private static final HandlerList handlers = new HandlerList();

	private Event event;

	private Player player;

	public DEPlayerJoinEvent(Player player, Event event) {
		this.player = player;
		this.event = event;
	}

	public Event getEvento() { return event; }

	public Player getPlayer() { return player; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
