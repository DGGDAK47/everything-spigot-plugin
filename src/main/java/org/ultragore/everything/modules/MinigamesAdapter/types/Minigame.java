package org.ultragore.everything.modules.MinigamesAdapter.types;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Minigame {
	public String gameGroup;
	public String worldName;
	
	public Minigame(Map<String, Object> map) {
		gameGroup = (String) map.get("game_group");
		worldName = (String) map.get("world_name");
	}
	
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
}
