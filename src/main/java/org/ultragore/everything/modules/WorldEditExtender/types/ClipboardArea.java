package org.ultragore.everything.modules.WorldEditExtender.types;

import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class ClipboardArea {
	private Player player;
	private int clipboardWidth;
	private int clipboardHeight;
	private int clipboardDepth;
	private int offsetX;
	private int offsetY;
	private int offsetZ;
	
	
	public ClipboardArea(Player player, int clipboardWidth, int clipboardHeight, int clipboardDepth, int offsetX, int offsetY, int offsetZ) {
		this.player = player;
		this.clipboardWidth = clipboardWidth;
		this.clipboardHeight = clipboardHeight;
		this.clipboardDepth = clipboardDepth;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}

	
	public Player getPlayer() {
		return player;
	}
	
	public BoundingBox getBoxFromCoords(int x, int y, int z) {
		return new BoundingBox(
			x + offsetX,
			y + offsetY,
			z + offsetZ,
			x + offsetX + clipboardWidth,
			y + offsetY + clipboardHeight,
			z + offsetZ + clipboardDepth
		);
	}
}
