package org.ultragore.everything.modules.WorldEditExtender.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.ultragore.everything.modules.WorldEditExtender.types.Restriction;
import org.ultragore.everything.utils.PermsUtils;

public class RestrictionManager {
	
	private List<Restriction> groupRestrictions = new ArrayList<Restriction>();
	private Restriction defaultRestrictions;
	
	
	public RestrictionManager(Map<String, Map> groupRestrictions, Map<String, Object> defaultRestrictions) {
		Set<String> keys = groupRestrictions.keySet();
		for(String key: keys) {
			this.groupRestrictions.add(new Restriction(key, groupRestrictions.get(key)));
		}
		
		this.defaultRestrictions = new Restriction(null, defaultRestrictions);
	}
	
	
	public Restriction getFirstRestrictionByGroup(Player p) {
		for(Restriction r: groupRestrictions) {
			if(PermsUtils.hasPermissionGroup(p, r.groupName)) {
				return r;
			}
		}
		return null;
	}
	
	
	public int getBlocksChangeLimit(Player p) {
		Restriction r = getFirstRestrictionByGroup(p);
		if(r == null) {
			r = defaultRestrictions;
		}
		
		return r.blockChangeLimit;
	}
	
	public int getIssueCooldown(Player p) {
		Restriction r = getFirstRestrictionByGroup(p);
		if(r == null) {
			r = defaultRestrictions;
		}
		
		return r.issueCooldown;
	}
	
	public boolean canEditOthersRegions(Player p) {
		Restriction r = getFirstRestrictionByGroup(p);
		if(r == null) {
			r = defaultRestrictions;
		}
		
		return r.editOthersRegions;
	}
	
	public boolean canEditWorld(Player p) {
		Restriction r = getFirstRestrictionByGroup(p);
		if(r == null) {
			r = defaultRestrictions;
		}
		
		return r.editWorld;
	}
}
