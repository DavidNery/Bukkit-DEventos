package me.dery.deventos.pluginlisteners;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class DEPlayerQuitEvent extends org.bukkit.event.Event {

	public enum QuitReason {
		/**
		 * Chamado quando o player sai via /evento sair
		 */
		NATURAL,
		/**
		 * Chamado quando o player sai por ter deslogado
		 */
		QUIT,
		/**
		 * Chamado quando o player sai porque morreu
		 */
		DEATH,
		/**
		 * Chamado quando o player sai por ser kickado do servidor
		 */
		KICK
	}

	private static final HandlerList handlers = new HandlerList();

	private Event event;

	private Player player;

	private QuitReason quitreason;

	public DEPlayerQuitEvent(Player player, Event event, QuitReason quitreason) {
		this.player = player;
		this.event = event;
		this.quitreason = quitreason;
	}

	public Event getEvento() { return event; }

	public Player getPlayer() { return player; }

	public QuitReason getQuitReason() { return quitreason; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
