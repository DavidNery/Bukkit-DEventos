package me.dery.deventos.runnables;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.bukkit.scheduler.BukkitRunnable;

import me.dery.deventos.DEventos;
import me.dery.deventos.enums.eventos.EventoState;
import me.dery.deventos.managers.EventosManager;

public class CheckStart extends BukkitRunnable {

	private final DEventos instance;

	private final EventosManager eventosmanager;

	private final Pattern pattern;

	private final String[] dias;

	public CheckStart(DEventos instance) {

		this.instance = instance;

		eventosmanager = instance.getEventosManager();

		pattern = Pattern
			.compile("(domingo)|(segunda)|(terca)|(quarta)|(quinta)|(sexta)|(sabado)");

		dias = new String[] {"domingo", "segunda", "terca", "quarta", "quinta", "sexta", "sabado"};

	}

	@Override
	public void run() {

		Calendar c = new GregorianCalendar();

		for (String eventos : instance.getConfig().getStringList("Config.Auto_Start.Eventos")) {
			String[] autostart = eventos.split("->");
			String evento = autostart[0];
			String tempo = autostart[1];

			if (hasDay(tempo))
				checkDays(tempo, evento, c);
			else
				checkHours(tempo, evento, c);
		}

	}

	private void checkDays(String tempo, String evento, Calendar c) {
		for (String days : tempo.split("&")) {
			String[] partes = days.split(">");

			if (!translateDay(c).equalsIgnoreCase(partes[0]))
				continue;

			checkHours(partes[1], evento, c);
		}
	}

	private void checkHours(String hora, String evento, Calendar c) {
		for (String hours : hora.split(";")) {

			String[] hoursAndMinutes = hours.split(":");

			if (isNum(hoursAndMinutes[0]) && isNum(hoursAndMinutes[1])
				&& c.get(Calendar.HOUR_OF_DAY) == Integer.valueOf(hoursAndMinutes[0])
				&& c.get(Calendar.MINUTE) == Integer.valueOf(hoursAndMinutes[1])
				&& eventosmanager.getEventoByName(evento).getEventoState() != EventoState.EMANDAMENTO)

				instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(),
					"evento iniciar " + evento);
		}
	}

	private String translateDay(Calendar c) { return dias[c.get(Calendar.DAY_OF_WEEK) - 1]; }

	private boolean hasDay(String string) { return pattern.matcher(string).find(); }

	private boolean isNum(String s) {

		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

}
