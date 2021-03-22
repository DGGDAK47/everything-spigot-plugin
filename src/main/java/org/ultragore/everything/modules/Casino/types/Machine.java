package org.ultragore.everything.modules.Casino.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.ultragore.everything.utils.WorldUtils;

public class Machine {
	
	public int winLine;
	public Double rollPrice;
	public int linesHeight;
	public Map<Material, Float> blocksRewards;
	public List<Material> blocksInLine;
	public List<Location> lineTopBlocksLocations;
	public Location leverLocation;
	
	public Machine(Map<String, Object> m) {
		winLine = (Integer) m.get("win_line");
		
		Object ob = m.get("roll_price");
		if(ob instanceof Double) {
			rollPrice = (Double) ob;
		} else if(ob instanceof Integer) {
			rollPrice = ((Integer) ob).doubleValue();
		}
		
		linesHeight = (Integer) m.get("lines_height");
		
		blocksRewards = new HashMap<Material, Float>();
		blocksInLine = new ArrayList();
		String[] data;
		for(String block: (List<String>) m.get("blocks_in_lines")) {
			// lineBlock -> MATERIAL:reward|NONE
			data = block.split(":");
			
			if(data[1].equals("NONE")) {
				blocksRewards.put(Material.getMaterial(data[0]), null);
				blocksInLine.add(Material.getMaterial(data[0]));
			} else {
				blocksRewards.put(Material.getMaterial(data[0]), Float.parseFloat(data[1]));
				blocksInLine.add(Material.getMaterial(data[0]));
			}
		}
		
		leverLocation = WorldUtils.parseLocation((String) m.get("lever_location"));
		
		lineTopBlocksLocations = new ArrayList<Location>();
		for(String loc: (List<String>) m.get("lines_top_block_locations")) {
			lineTopBlocksLocations.add(WorldUtils.parseLocation(loc));
		}
	}
	
	public static int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	public Float getBlockReward(Material mat) {
		return blocksRewards.get(mat);
	}
	
	public Material getRandomLineBlock() {
		int index = getRandomNumber(0, blocksInLine.size());
		return (Material) blocksInLine.get(index);
	}
}
