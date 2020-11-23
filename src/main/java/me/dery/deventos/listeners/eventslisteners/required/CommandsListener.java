package me.dery.deventos.listeners.eventslisteners.required;

import java.util.List;

import me.dery.deventos.objects.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventProperty;
import me.dery.deventos.managers.EventsManager;

public class CommandsListener implements Listener {

	private final DEventos instance;

	private final EventsManager eventsManager;

	public CommandsListener(DEventos instance) {
		this.instance = instance;

		eventsManager = instance.getEventosManager();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void Comando(PlayerCommandPreprocessEvent e) {

		Player p = e.getPlayer();

		if (p.hasPermission("deventos.admin"))
			return;

		cmdCheck:
		for (Event event : eventsManager.getEmAndamento()) {
			if (event.getPlayers().contains(p.getName()) || event.getEspectadores().contains(p.getName())) {
				List<String> comandosLiberados = eventsManager.getEventoConfig(event)
					.getStringList(EventProperty.ALLOWEDCOMMANDS.keyInConfig);

				String cmd = e.getMessage().toLowerCase();
				for (String s : comandosLiberados)
					if (("/" + s.toLowerCase()).startsWith(cmd))
						break cmdCheck;

				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Bloqueado")
					.replace("&", "§").replace("{cmd}", e.getMessage()));
				break;
			}
		}

	}

}
