package org.ultragore.everything.modules.WorldEditExtender.types;

import java.util.Map;

public class Restriction {
	public String groupName = null;
	public int blockChangeLimit;
	public int issueCooldown;
	public boolean editOthersRegions;
	public boolean editWorld;

	public Restriction(String groupName, Map<String, Object> m) {
		this.groupName = groupName;
		
		blockChangeLimit = (int) m.get("blocks_change_limit");
		issueCooldown = (int) m.get("issue_cooldown");
		editOthersRegions = (boolean) m.get("edit_others_regions");
		editWorld = (boolean) m.get("edit_world");
	}
}