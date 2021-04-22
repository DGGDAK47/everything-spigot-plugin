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
		
		if(data.length >= 5) {
			yaw = Float.parseFloat(data[4]);
			if(data.length == 6) {
				pitch = Float.parseFloat(data[5]);
			}
		}
		
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	/**
	 * Extracts BoundingBox from string. Format:
	 * From:<x1> <y1> <z1>|To:<x2> <y2> <z2>
	 * @param sBox
	 * @return BoundingBox
	 */
	public static BoundingBox parseBoundingBox(String sBox) {
		String[] arr = sBox.split("\\|");
		String[] from = arr[0].split(" ");
		String[] to = arr[1].split(" ");
		
		return new BoundingBox(Double.parseDouble(from[0].split(":")[1]), Double.parseDouble(from[1]), Double.parseDouble(from[2]), Double.parseDouble(to[0].split(":")[1]), Double.parseDouble(to[1]), Double.parseDouble(to[2]));
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
	
	public static BoundingBox boxOverLocationCenter(Location loc, double width, double height, double depth) {
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
