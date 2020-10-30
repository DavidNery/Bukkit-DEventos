package me.dery.deventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class LegendChat implements Listener {

	private final EventosManager eventosManager;

	public LegendChat(EventosManager eventosManager) { this.eventosManager = eventosManager; }

	@EventHandler
	public void Chat(ChatMessageEvent e) {
		Player p = e.getSender();

		for (Evento eventos : eventosManager.getPlayerLastWinnerEvents(p.getName())) {
			String tag = eventos.getNome().toLowerCase();

			if (e.getTags().contains(tag))
				e.setTagValue(tag, eventos.getTag());
		}
	}

}
