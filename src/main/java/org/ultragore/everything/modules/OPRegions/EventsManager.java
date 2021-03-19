package org.ultragore.everything.modules.OPRegions;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.ultragore.everything.utils.DottedMap;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class EventsManager implements Listener {
	
	private RegionContainer container;
	private DottedMap config;
	
	public EventsManager(DottedMap config) {
		this.config = config;
		this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}
	
	/**
	 * Returns if block is in listed op_regions. 
	 * @param block the block which is being checked
	 * @return  0 - block is not in op_regions region
	 * 			1 - block in one of op_regions list
	 * 			2 - block in one of op_regions list and one of regions has flag build ALLOW flag
	 */
	public int isBlockInOpRegion(Block block) {
		RegionManager regionManager = this.container.get(BukkitAdapter.adapt(block.getWorld()));
		Location loc = block.getLocation();
		ApplicableRegionSet regions = regionManager.getApplicableRegions(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		
		boolean hasOpRegionEntered = false;
		boolean hasRegionWithBuildAllowEntered = false;
		Map<Flag<?>, Object> flags;
		Set<Flag<?>> flagsKeys;
		Object state;
		
		for(ProtectedRegion region: regions) {
			
			if (config.getList("op_regions").contains(region.getId())) {
				hasOpRegionEntered = true;
			}
			
			flags = region.getFlags();
			flagsKeys = flags.keySet();
			
			for(Flag flag: flagsKeys) {
				state = flags.get(flag);
				if (state != null && flag.getName().equals("build") && state.toString().equals("ALLOW")) {
					hasRegionWithBuildAllowEntered = true;
					break;
				}
			}
			
			if (hasOpRegionEntered && hasRegionWithBuildAllowEntered) {
				return 2;
			}
		}
		
		if (hasOpRegionEntered) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getPlayer() != null && !event.getPlayer().isOp() && isBlockInOpRegion(event.getBlock()) == 1) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getPlayer() != null && !event.getPlayer().isOp() && isBlockInOpRegion(event.getBlock()) == 1) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
		if(event.getPlayer() != null && !event.getPlayer().isOp() && isBlockInOpRegion(event.getBlock()) == 1) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getPlayer() != null && !event.getPlayer().isOp() && isBlockInOpRegion(event.getBlock()) == 1) {
			event.setCancelled(true);
		}
	}
}