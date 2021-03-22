package org.ultragore.everything.modules.Casino.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Roll {
	
	public Machine machine;
	public int scrollsCount = 0;
	public Player participant;
	public List<List<Block>> lines;
	public World world;
	
	
	public Roll(Machine machine, Player participant) {
		// lines element represents one column(aka line)
		// 
		// ############
		// # || || || #
		// # || || || #
		// # || || || #
		// ############
		//
		
		this.machine = machine;
		world = machine.leverLocation.getWorld();
		lines = new <List<Block>>ArrayList();
		for(Location lineTopBlock: machine.lineTopBlocksLocations) {
			lines.add(getLineBlocks(lineTopBlock));
		}
		
		this.participant = participant;
	}
	
	
	public void deposeLineDown(List<Block> line) {
		
		for(int i = line.size()-1; i >= 0; i--) {
			if(i == 0) {
				line.get(i).setType(machine.getRandomLineBlock());
			} else {
				line.get(i).setType(line.get(i-1).getType());
			}
		}
	}
	
	public List<Block> getLineBlocks(Location lineTopBlock) {
		List<Block> toReturn = new <Block>ArrayList();
		
		for(int i = 0; i < machine.linesHeight; i++) {
			toReturn.add(world.getBlockAt(lineTopBlock.getBlockX(), lineTopBlock.getBlockY() - i, lineTopBlock.getBlockZ()));
		}
		
		return toReturn;
	}
	
	public List<Block> getBlocksInRow(int rowNumber) {
		List<Block> toReturn = new <Block>ArrayList();
		
		for(List<Block> line: lines) {
			toReturn.add(line.get(rowNumber-1));
		}
		
		return toReturn;
	}
}