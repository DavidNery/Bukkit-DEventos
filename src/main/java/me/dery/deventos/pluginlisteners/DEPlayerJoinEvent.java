package me.dery.deventos.pluginlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.dery.deventos.objects.Evento;

public class DEPlayerJoinEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Evento evento;

	private Player player;

	public DEPlayerJoinEvent(Player player, Evento evento) {
		this.player = player;
		this.evento = evento;
	}

	public Evento getEvento() { return evento; }

	public Player getPlayer() { return player; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
