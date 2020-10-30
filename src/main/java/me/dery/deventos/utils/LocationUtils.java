package me.dery.deventos.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

	public String serializeLocation(Location loc, boolean yawAndPitch) {

		return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ()
			+ (yawAndPitch ? (";" + loc.getYaw() + ";" + loc.getPitch()) : "");

	}

	public Location deserializeLocation(String serializedLocation) {

		String[] partes = serializedLocation.split(";");

		Location loc = new Location(
			Bukkit.getWorld(partes[0]),
			Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));

		if (partes.length >= 5) {
			loc.setYaw(Float.parseFloat(partes[4]));
			loc.setPitch(Float.parseFloat(partes[5]));
		}

		return loc;

	}

}
