package org.ultragore.everything.modules.MinigamesLobby.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.ultragore.everything.utils.WorldUtils;

public class Lobby {
	public Location spawnLocation;
	public String name;
	public String worldName;
	public String lobbyGroup;
	public String messageFormat;
	public Long worldConstantTime = null;
	public BoundingBox lobbyBox;
	private List<Material> allowedInteractMaterials = new ArrayList<Material>();
	private List<String> allowedCommands;
	private List<Player> lobbyParticipants = new ArrayList<Player>();
	private List<LobbyItem> lobbyItems = new ArrayList<LobbyItem>();
	
	
	public Lobby(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.spawnLocation = WorldUtils.parseLocation((String) map.get("spawn_location"));
		this.lobbyGroup = (String) map.get("lobby_group");
		this.worldName = (String) map.get("world_name");
		
		List<String> materials = (List<String>) map.get("allowed_interact_materials");
		for(String sMat: materials) {
			allowedInteractMaterials.add(Material.valueOf(sMat));
		}
		
		messageFormat = (String) map.get("message_format");
		
		allowedCommands = (List<String>) map.get("allowed_commands");
		
		Object ob = map.get("world_constant_time");
		if(ob != null) {
			worldConstantTime = Long.valueOf((Integer) ob);
		}
		
		this.lobbyBox = WorldUtils.parseBoundingBox((String) map.get("lobby_box"));
		
		Map<String, Object> lobby_items = (Map<String, Object>) map.get("lobby_items");
		Set<String> keys = lobby_items.keySet();
		for(String key: keys) {
			lobbyItems.add(new LobbyItem((Map<String, Object>) lobby_items.get(key)));
		}
	}
	
	
	public boolean hasParticipant(Player player) {
		return lobbyParticipants.contains(player);
	}
	
	public void addParticipant(Player player) {
		lobbyParticipants.add(player);
	}
	
	public void removeParticipant(Player player) {
		lobbyParticipants.remove(player);
	}
	
	public List<Player> getParticipants() {
		return lobbyParticipants;
	}
	
	public LobbyItem getLobbyItem(ItemStack itemStack) {
		for(LobbyItem item: lobbyItems) {
			if(item.equals(itemStack)) {
				return item;
			}
		}
		
		return null;
	}
	
	public List<LobbyItem> getLobbyItems() {
		return lobbyItems;
	}
	
	
	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	
	public boolean isInteractAllowed(Material sample) {
		return allowedInteractMaterials.contains(sample);
	}
	
	public boolean isCommandAllowed(String command) {
		for(String allowedCommand: allowedCommands) {
			if(command.startsWith(allowedCommand)) {
				return true;
			}
		}
		return false;
	}
}
