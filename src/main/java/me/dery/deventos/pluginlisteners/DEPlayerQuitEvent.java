package me.dery.deventos.pluginlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.dery.deventos.objects.Evento;

public class DEPlayerQuitEvent extends Event {

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

	private Evento evento;

	private Player player;

	private QuitReason quitreason;

	public DEPlayerQuitEvent(Player player, Evento evento, QuitReason quitreason) {
		this.player = player;
		this.evento = evento;
		this.quitreason = quitreason;
	}

	public Evento getEvento() { return evento; }

	public Player getPlayer() { return player; }

	public QuitReason getQuitReason() { return quitreason; }

	public HandlerList getHandlers() { return handlers; }

	public static HandlerList getHandlerList() { return handlers; }

}
