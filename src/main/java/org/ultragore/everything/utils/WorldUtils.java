package org.ultragore.everything.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

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
	
	
	
	public static BoundingBox boxAtLocationCenter(Location loc, double width, double height, double depth) {
		return new BoundingBox(
			loc.getX() - width / 2,
			loc.getY() - height / 2,
			loc.getZ() - depth / 2,
			loc.getX() + width / 2,
			loc.getY() + height / 2,
			loc.getZ() + depth / 2
		);
	}
	
	public static BoundingBox boxOverLocation(Location loc, double width, double height, double depth) {
		return new BoundingBox(
			loc.getX() - width / 2,
			loc.getY(),
			loc.getZ() - depth / 2,
			loc.getX() + width / 2,
			loc.getY() + height,
			loc.getZ() + depth / 2
		);
	}
}
