package me.dery.deventos.listeners;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.dery.deventos.managers.EventsManager;

public class LegendChat implements Listener {

	private final EventsManager eventsManager;

	public LegendChat(EventsManager eventsManager) { this.eventsManager = eventsManager; }

	@EventHandler
	public void Chat(ChatMessageEvent e) {
		Player p = e.getSender();

		for (Event eventos : eventsManager.getPlayerLastWinnerEvents(p.getName())) {
			String tag = eventos.getNome().toLowerCase();

			if (e.getTags().contains(tag))
				e.setTagValue(tag, eventos.getTag());
		}
	}

}
