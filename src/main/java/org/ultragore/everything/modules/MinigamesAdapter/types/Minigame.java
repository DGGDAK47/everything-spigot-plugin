package org.ultragore.everything.modules.MinigamesAdapter.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.ultragore.everything.utils.WorldUtils;

public class Minigame {
	public String gameGroup;
	public String worldName;
	public Location deathRespawnLocation;
	private List<Player> participants = new ArrayList<Player>();
	
	public Minigame(Map<String, Object> map) {
		gameGroup = (String) map.get("game_group");
		worldName = (String) map.get("world_name");
		deathRespawnLocation = WorldUtils.parseLocation((String) map.get("death_respawn_location"));
	}
	
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	
	public boolean hasParticipant(Player participant) {
		return participants.contains(participant);
	}
	
	public void addParticipant(Player participant) {
		participants.add(participant);
	}
	
	public void removeParticipant(Player participant) {
		participants.remove(participant);
	}
}
