package me.dery.deventos.pluginlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.dery.deventos.objects.Evento;

public class DEPlayerLoseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Evento evento;

	private Player p;

	public DEPlayerLoseEvent(Player p, Evento evento) {
		this.p = p;
		this.evento = evento;
	}

	public Player getPlayer() { return p; }

	public Evento getEvento() { return evento; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
