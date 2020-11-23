package me.dery.deventos.pluginlisteners;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class DEPlayerLoseEvent extends org.bukkit.event.Event {

	private static final HandlerList handlers = new HandlerList();

	private Event event;

	private Player p;

	public DEPlayerLoseEvent(Player p, Event event) {
		this.p = p;
		this.event = event;
	}

	public Player getPlayer() { return p; }

	public Event getEvento() { return event; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
