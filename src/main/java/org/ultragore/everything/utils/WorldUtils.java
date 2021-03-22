package org.ultragore.everything.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldUtils {
	
	/**
	 * Extracts location data from string. Format:
	 * <world> <x> <y> <z> [yaw[pitch]]
	 * @param sLoc
	 * @return Location
	 */
	public static Location parseLocation(String sLoc) {
		String[] data = sLoc.split(" ");
		
		World world = Bukkit.getWorld(data[0]);
		float x = Float.parseFloat(data[1]);
		float y = Float.parseFloat(data[2]);
		float z = Float.parseFloat(data[3]);
		float yaw = 0;
		float pitch = 0;
		
		if(data.length == 5) {
			yaw = Float.parseFloat(data[4]);
		}
		if(data.length == 6) {
			yaw = Float.parseFloat(data[5]);
		}
		
		return new Location(world, x, y, z, yaw, pitch);
	}
}
