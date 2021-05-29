package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameLeaveEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Minigame;

import plugily.projects.murdermystery.api.events.game.MMGameJoinAttemptEvent;
import plugily.projects.murdermystery.api.events.game.MMGameLeaveAttemptEvent;
import plugily.projects.murdermystery.api.events.game.MMGameStopEvent;

public class MinigamesManager implements Listener {
	private List<Minigame> minigames = new ArrayList<Minigame>();
	private LobbyManager lobbyManager;
	private Plugin plugin;
	
	public MinigamesManager(Plugin plugin, Map<String, Object> map) {
		this.plugin = plugin;
		
		Set<String> keys = map.keySet();
		for(String key: keys) {
			minigames.add(new Minigame((Map<String, Object>) map.get(key)));
		}
	}
	public void setLobbyManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}
	
	
	
	public boolean hasMinigame(Player participant) {
		for(Minigame m: minigames) {
			if(m.hasParticipant(participant)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasMinigame(String worldName) {
		for(Minigame m: minigames) {
			if(m.worldName.equals(worldName)) {
				return true;
			}
		}
		return false;
	}
	
	public Minigame getMinigame(Player participant) {
		for(Minigame m: minigames) {
			if(m.hasParticipant(participant)) {
				return m;
			}
		}
		return null;
	}
	
	public Minigame getMinigame(String arenaId) {
		for(Minigame m: minigames) {
			if(m.arenaId.equals(arenaId)) {
				return m;
			}
		}
		return null;
	}
	
	public List<Minigame> getMinigames(String worldName) {
		List<Minigame> toReturn = new ArrayList<Minigame>();
		
		for(Minigame mg: minigames) {
			if(mg.worldName.equals(worldName)) {
				toReturn.add(mg);
			}
		}
		
		return toReturn;
	}
	
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Minigame minigame = getMinigame(e.getPlayer());
		if(minigame != null) {
			e.setRespawnLocation(minigame.deathRespawnLocation);
		}
	}
	
	@EventHandler
	public void onMurderGameJoin(MMGameJoinAttemptEvent e) {
		Minigame mg = getMinigame(e.getArena().getId());
		mg.addParticipant(e.getPlayer());
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new MinigameJoinEvent(e.getPlayer(), mg, lobbyManager.hasLobby(e.getPlayer())));				
			}
		}, 1);
	}
	@EventHandler
	public void onMurderGameLeave(MMGameLeaveAttemptEvent e) {
		Minigame mg = getMinigame(e.getPlayer());
		mg.removeParticipant(e.getPlayer());
		
		Bukkit.getPluginManager().callEvent(new MinigameLeaveEvent(e.getPlayer(), mg, true));
	}
	@EventHandler
	public void onMurderGameEnd(MMGameStopEvent e) {
		Minigame mg = getMinigame(e.getArena().getId());
		List<Player> oldParticipants = mg.clearParticipants();
		
		for(Player p: oldParticipants) {
			Bukkit.getPluginManager().callEvent(new MinigameLeaveEvent(p, mg, true));
		}
	}
}
