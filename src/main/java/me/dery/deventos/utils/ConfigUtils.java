package me.dery.deventos.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.dery.deventos.DEventos;

public class ConfigUtils {

	@SuppressWarnings("deprecation")
	public void updateConfigAndEventos(DEventos instance) {

		YamlConfiguration finalyml = new YamlConfiguration();
		File config = new File(instance.getDataFolder(), "config.yml");

		try {
			finalyml.load(config);
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean configUpdated = false;

		FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(instance.getResource("config.yml"));

		for (String key : tempConfig.getKeys(true)) {
			Object obj = tempConfig.get(key);

			if (finalyml.get(key) != null)
				obj = finalyml.get(key);
			else
				configUpdated = true;

			finalyml.set(key, obj);
		}

		for (String key : finalyml.getKeys(true)) {
			if (tempConfig.get(key) == null) {
				finalyml.set(key, null);
				configUpdated = true;
			}
		}

		if (configUpdated)
			try {
				finalyml.save(config);
				finalyml.load(config);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

		FileConfiguration tempEvento = YamlConfiguration.loadConfiguration(instance.getResource("eventos/parkour.yml"));

		for (File evento : new File(instance.getDataFolder(), "eventos").listFiles()) {
			FileConfiguration fcevento = YamlConfiguration.loadConfiguration(evento);

			boolean eventoUpdated = false;

			for (String key : tempEvento.getKeys(true)) {
				if (!key.startsWith("Avisos_Evento") && !key.startsWith("Baus")) {
					Object obj = tempEvento.get(key);

					if (fcevento.get(key) != null)
						obj = fcevento.get(key);
					else
						eventoUpdated = true;

					fcevento.set(key, obj);
				}
			}

			for (String key : fcevento.getKeys(true)) {
				if (!key.startsWith("Avisos_Evento") && !key.startsWith("Baus") && tempEvento.get(key) == null) {
					fcevento.set(key, null);
					eventoUpdated = true;
				}
			}

			if (eventoUpdated)
				try {
					fcevento.save(evento);
					fcevento.load(evento);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
		}

	}

}
