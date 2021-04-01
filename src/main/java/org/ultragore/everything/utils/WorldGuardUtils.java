package org.ultragore.everything.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardUtils {

	public static RegionManager getRegionManager(World world) {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
	}
	
	public static List<ProtectedRegion> getCrossedRegions(Location location, boolean ignoreGlobal) {
		List<ProtectedRegion> toReturn = new ArrayList<ProtectedRegion>();
		
		ApplicableRegionSet regions = getRegionManager(location.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(location));
		for(ProtectedRegion pr: regions) {
			if(ignoreGlobal && pr.getId().equals("__global__")) {
				continue;
			}
			toReturn.add(pr);
		}
		
		return toReturn;
	}
	
	public static List<ProtectedRegion> getCrossedRegions(BoundingBox area, World world, boolean ignoreGlobal) {
		List<ProtectedRegion> toReturn = new ArrayList<ProtectedRegion>();
		
		RegionManager rm = getRegionManager(world);
		ApplicableRegionSet regionsFromVector;
		for(double x = area.getMinX(); x <= area.getMaxX(); x++) {
			for(double y = area.getMinY(); y <= area.getMaxY(); y++) {
				for(double z = area.getMinZ(); z <= area.getMaxZ(); z++) {
					regionsFromVector = rm.getApplicableRegions(BlockVector3.at(x, y, z));
					for(ProtectedRegion pr: regionsFromVector) {
						if(!toReturn.contains(pr)) {
							if(ignoreGlobal && pr.getId().equals("__global__")) {
								continue;
							}
							toReturn.add(pr);
						}
					}
				}
			}
		}
		
		return toReturn;
	}
	
	public static boolean isRegionlessAreaCrossed(BoundingBox area, World world, boolean ignoreGlobal) {
		List<ProtectedRegion> crossedRegions = new ArrayList<ProtectedRegion>();
		
		RegionManager rm = getRegionManager(world);
		Set<ProtectedRegion> regionsFromVector;
		ProtectedRegion globalRegion;
		for(double x = area.getMinX(); x <= area.getMaxX(); x++) {
			for(double y = area.getMinY(); y <= area.getMaxY(); y++) {
				for(double z = area.getMinZ(); z <= area.getMaxZ(); z++) {
					regionsFromVector = rm.getApplicableRegions(BlockVector3.at(x, y, z)).getRegions();
					
					if(ignoreGlobal) {
						globalRegion = null;
						for(ProtectedRegion pr: regionsFromVector) {
							if(pr.getId().equals("__global__")) {
								globalRegion = pr;
								break;
							}
						}
						
						if(globalRegion != null) {
							regionsFromVector.remove(globalRegion);
						}
					}
					
					if(regionsFromVector.size() == 0) {
						return true;
					}
				}
			}
		}
		
		return crossedRegions.size() == 0;
	}
	
}