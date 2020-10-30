package me.dery.deventos.listeners.eventslisteners.required;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventoProperty;
import me.dery.deventos.managers.EventosManager;
import me.dery.deventos.objects.Evento;

public class CommandsListener implements Listener {

	private final DEventos instance;

	private final EventosManager eventosManager;

	public CommandsListener(DEventos instance) {
		this.instance = instance;

		eventosManager = instance.getEventosManager();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void Comando(PlayerCommandPreprocessEvent e) {

		Player p = e.getPlayer();

		if (p.hasPermission("deventos.admin"))
			return;

		cmdCheck:
		for (Evento evento : eventosManager.getEmAndamento()) {
			if (evento.getPlayers().contains(p.getName()) || evento.getEspectadores().contains(p.getName())) {
				List<String> comandosLiberados = eventosManager.getEventoConfig(evento)
					.getStringList(EventoProperty.COMANDOSLIBERADOS.keyInConfig);

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
