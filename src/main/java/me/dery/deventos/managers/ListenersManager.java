package me.dery.deventos.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.dery.deventos.DEventos;
import me.dery.deventos.listeners.eventslisteners.demand.DamageListener;
import me.dery.deventos.listeners.eventslisteners.demand.FoodListener;
import me.dery.deventos.listeners.eventslisteners.demand.PVPListener;
import me.dery.deventos.listeners.eventslisteners.demand.TeleportListener;
import me.dery.deventos.listeners.eventslisteners.required.BuildListener;
import me.dery.deventos.listeners.eventslisteners.required.CommandsListener;
import me.dery.deventos.listeners.eventslisteners.required.DropListener;
import me.dery.deventos.listeners.eventslisteners.required.EspectadorDamageListener;
import me.dery.deventos.listeners.eventslisteners.required.VictoryListener;
import me.dery.deventos.objects.Evento;

public class ListenersManager {

	private final DEventos instance;

	private final EventosManager eventosManager;

	private final RunnableManager runnableManager;

	private final List<Listener> registeredListeners;

	public ListenersManager(DEventos instance) {

		this.instance = instance;

		eventosManager = instance.getEventosManager();
		runnableManager = instance.getRunnableManager();

		registeredListeners = new ArrayList<>();

	}

	public void registerListeners(Evento evento) {

		PluginManager pluginManager = instance.getServer().getPluginManager();

		List<Listener> listenersToRegister = getListenerToManipulate(evento);

		// O método é chamado antes de adicionar o evento na lista
		if (eventosManager.getEmAndamento().size() == 0) {
			listenersToRegister.addAll(Arrays.asList(new BuildListener(instance), new CommandsListener(instance),
				new DropListener(instance), new EspectadorDamageListener(instance), new VictoryListener(instance)));

			if (instance.getConfig().getBoolean("Config.Desativar_Teleporte"))
				registerListener(pluginManager, Arrays.asList(new TeleportListener(instance)));
		}

		if (listenersToRegister.size() > 0)
			registerListener(pluginManager, listenersToRegister);

		if (evento.getBlock() != null)
			runnableManager.registerBlockTask(instance, instance.getEventosStateManager(), evento);
	}

	public void unregisterListeners(Evento evento) {

		// O método é chamado depois de remover o evento da lista
		if (eventosManager.getEmAndamento().size() == 0) {
			unregisterListener(instance.getServer().getPluginManager(), registeredListeners);
		} else {
			List<Listener> listenersToUnregister = getListenerToManipulate(evento);

			if (listenersToUnregister.size() > 0)
				unregisterListener(instance.getServer().getPluginManager(), listenersToUnregister);
		}

		if (evento.getBlock() != null)
			runnableManager.unregisterBlockTask(evento);

	}

	private void registerListener(PluginManager pluginManager, List<Listener> listeners) {
		for (Listener listener : listeners)
			pluginManager.registerEvents(listener, instance);
		registeredListeners.addAll(listeners);
	}

	private void unregisterListener(PluginManager pluginManager, List<Listener> listeners) {
		Iterator<Listener> it = listeners.iterator();
		while (it.hasNext()) {
			HandlerList.unregisterAll(it.next());
			it.remove();
		}
	}

	private List<Listener> getListenerToManipulate(Evento evento) {
		boolean damageListener = false, pvpListener = false, foodListener = false;

		for (Evento e : eventosManager.getEmAndamento()) {
			if (e.desativarDamage())
				damageListener = true;
			if (e.desativarFome())
				foodListener = true;
			if (e.desativarPvp())
				pvpListener = true;
		}

		List<Listener> listeners = new ArrayList<>();

		if (evento.desativarDamage() && !damageListener)
			listeners.add(new DamageListener(instance));
		if (evento.desativarPvp() && !pvpListener)
			listeners.add(new PVPListener(instance));
		if (evento.desativarFome() && !foodListener)
			listeners.add(new FoodListener(instance));

		return listeners;
	}

}
