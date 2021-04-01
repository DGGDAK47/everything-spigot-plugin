package org.ultragore.everything.modules.WorldEditExtender.types;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class AffectedArea {
	private BoundingBox areaBox;
	private World world;
	
	
	public AffectedArea(BoundingBox areaBox, World world) {
		this.areaBox = areaBox;
		this.world = world;
	}
	
	
	public int getAreaVolume() {
		return (int)(areaBox.getWidthX() * areaBox.getHeight() * areaBox.getWidthZ());
	}
	
	public BoundingBox getAreaBox() {
		return areaBox;
	}
	
	public World getWorld() {
		return world;
	}
}
