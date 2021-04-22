package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.events.LobbyJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.LobbyLeaveEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;
import org.ultragore.everything.utils.WorldUtils;

public class LobbyManager implements Listener {
	private List<Lobby> lobbies = new ArrayList<Lobby>();
	private Location serverSpawnLocation;
	private Sound lobbyJoinSound;
	private MinigamesManager minigamesManager;
	
	
	public LobbyManager(MinigamesManager minigamesManager, Sound lobbyJoinSound, Map<String, Map> map, String serverSpawnLocation) {
		this.minigamesManager = minigamesManager;
		this.lobbyJoinSound = lobbyJoinSound;
		this.serverSpawnLocation = WorldUtils.parseLocation(serverSpawnLocation);
		Set<String> keys = map.keySet();
		
		for(String key: keys) {
			lobbies.add(new Lobby(map.get(key)));
		}
	}
	
	
	public void teleportParticipantsToSpawn() {
		for(Lobby lobby: lobbies) {
			for(Player participant: lobby.getParticipants()) {
				participant.teleport(serverSpawnLocation);
			}
		}
	}
	
	public Location getServerSpawnLocation() {
		return serverSpawnLocation;
	}
	
	
	public Lobby getLobby(Player participant) {
		for(Lobby lobby: lobbies) {
			if(lobby.hasParticipant(participant)) {
				return lobby;
			}
		}
		return null;
	}
	
	public Lobby getLobby(String worldName) {
		for(Lobby l: lobbies) {
			if(l.worldName.equals(worldName)) {
				return l;
			}
		}
		
		return null;
	}
	
	public boolean hasLobby(String worldName) {
		for(Lobby l: lobbies) {
			if(l.worldName.equals(worldName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasLobby(Player participant) {
		for(Lobby l: lobbies) {
			if(l.hasParticipant(participant)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<Lobby> getLobbies() {
		return (List<Lobby>) new ArrayList(this.lobbies).clone();
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Lobby lobby = getLobby(e.getPlayer());
		if(lobby != null) {
			e.getPlayer().teleport(serverSpawnLocation);
			Bukkit.getPluginManager().callEvent(new LobbyLeaveEvent(e.getPlayer(), lobby, false));
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if(e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM)) {
			return;
		}
		
		Lobby fromLobby = getLobby(e.getFrom().getWorld().getName());
		Lobby toLobby = getLobby(e.getTo().getWorld().getName());
		
		if(fromLobby != null && toLobby != null &&
		   fromLobby.worldName.equals(toLobby.worldName)) {
			return;
		}
		
		if(fromLobby != null) {
			if(fromLobby.hasParticipant(e.getPlayer())) {
				fromLobby.removeParticipant(e.getPlayer());
				Bukkit.getPluginManager().callEvent(new LobbyLeaveEvent(e.getPlayer(), fromLobby, minigamesManager.hasMinigame(e.getTo().getWorld().getName())));				
			}
		}
		
		if(toLobby != null) {
			toLobby.addParticipant(e.getPlayer());
			Bukkit.getPluginManager().callEvent(new LobbyJoinEvent(e.getPlayer(), toLobby, minigamesManager.hasMinigame(e.getFrom().getWorld().getName())));
			e.getPlayer().playSound(e.getPlayer().getLocation(), lobbyJoinSound, 0.2F, 1.0F);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Lobby lobby = getLobby(e.getPlayer());
		
		if(lobby != null) {
			e.setRespawnLocation(lobby.spawnLocation);
		}
	}
	
	@EventHandler
	public void onPlayerSpawn(PlayerSpawnLocationEvent e) {
		if(e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM)) {
			return;
		}
		
		Lobby lobby = getLobby(e.getPlayer().getWorld().getName());
		if(lobby != null && !lobby.hasParticipant(e.getPlayer())) {
			e.setSpawnLocation(serverSpawnLocation);
		}
	}
}
