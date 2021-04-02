package org.ultragore.everything.modules.WorldEditExtender.types;

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
		int width = (int) areaBox.getWidthX();
		width = width == 0 ? 1 : width + 1;
		
		int height = (int) areaBox.getHeight();
		height = height == 0 ? 1 : height + 1;
		
		int depth = (int) areaBox.getWidthZ();
		depth = depth == 0 ? 1 : depth + 1;
		
		return width * height * depth;
	}
	
	public BoundingBox getAreaBox() {
		return areaBox;
	}
	
	public World getWorld() {
		return world;
	}
}
