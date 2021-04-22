package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameLeaveEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Minigame;

public class MinigamesManager implements Listener {
	private LobbyManager lobbyManager;
	private List<Minigame> minigames = new ArrayList<Minigame>();
	
	
	public MinigamesManager(Map<String, Object> map) {
		Set<String> keys = map.keySet();
		for(String key: keys) {
			minigames.add(new Minigame((Map<String, Object>) map.get(key)));
		}
	}
	public void setLobbyManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}
	
	
	public boolean hasMinigame(String worldName) {
		for(Minigame m: minigames) {
			if(m.worldName.equals(worldName)) {
				return true;
			}
		}
		return false;
	}
	
	public Minigame getMinigame(String worldName) {
		for(Minigame m: minigames) {
			if(m.worldName.equals(worldName)) {
				return m;
			}
		}
		return null;
	}
	
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if(e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM)) {
			return;
		}
		
		Minigame fromMinigame = getMinigame(e.getFrom().getWorld().getName());
		Minigame toMinigame = getMinigame(e.getTo().getWorld().getName());
		
		if(fromMinigame != null) {
			Bukkit.getPluginManager().callEvent(new MinigameLeaveEvent(e.getPlayer(), fromMinigame, lobbyManager.hasLobby(e.getTo().getWorld().getName())));
		}
		
		if(toMinigame != null) {
			Bukkit.getPluginManager().callEvent(new MinigameJoinEvent(e.getPlayer(), toMinigame, lobbyManager.hasLobby(e.getFrom().getWorld().getName())));
		}
	}
}
