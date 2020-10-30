package me.dery.deventos.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.dery.deventos.DEventos;

public class InventoryUtils {

	private final DEventos instance;

	public InventoryUtils(DEventos instance) { this.instance = instance; }

	public boolean isVazio(Player p) {
		for (ItemStack item : p.getInventory().getContents())
			if (item != null && item.getType() != Material.AIR)
				return false;

		for (ItemStack item : p.getInventory().getArmorContents())
			if (item != null && item.getType() != Material.AIR)
				return false;

		return true;
	}

	public void saveInventory(Player p) throws IOException {
		if(isVazio(p)) return;
		
		File f = getPlayerFile(p);
		FileConfiguration playerInventoryConfig = YamlConfiguration.loadConfiguration(f);
		playerInventoryConfig.set("Inventory.Armor", p.getInventory().getArmorContents());
		playerInventoryConfig.set("Inventory.Content", p.getInventory().getContents());
		playerInventoryConfig.save(f);
	}

	public boolean hasInventory(Player p) { return getPlayerFile(p).exists(); }

	@SuppressWarnings("unchecked")
	public boolean restoreInventory(Player p) throws IOException {
		File f = getPlayerFile(p);

		if (f.exists()) {
			FileConfiguration playerInventoryConfig = YamlConfiguration.loadConfiguration(f);

			ItemStack[] content =
				((List<ItemStack>) playerInventoryConfig.get("Inventory.Armor")).toArray(new ItemStack[0]);
			p.getInventory().setArmorContents(content);

			content = ((List<ItemStack>) playerInventoryConfig.get("Inventory.Content")).toArray(new ItemStack[0]);
			p.getInventory().setContents(content);

			p.updateInventory();

			f.delete();

			return true;
		}

		return false;
	}

	private File getPlayerFile(Player p) {
		return new File(instance.getDataFolder(), "inventarios" + File.separator + p.getName() + ".yml");
	}

}
