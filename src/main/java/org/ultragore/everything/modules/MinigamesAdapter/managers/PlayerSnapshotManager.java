package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ultragore.everything.modules.MinigamesAdapter.events.LobbyJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.LobbyLeaveEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.events.MinigameLeaveEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;
import org.ultragore.everything.modules.MinigamesAdapter.types.PlayerSnapshot;
import org.ultragore.everything.utils.PermsUtils;

public class PlayerSnapshotManager implements Listener {
	private LobbyManager lobbyManager;
	private List<PlayerSnapshot> snapshots = new ArrayList<PlayerSnapshot>();
	
	
	public PlayerSnapshotManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}
	
	
	public void applySnapshots() {
		for(PlayerSnapshot ps: snapshots) {
			ps.applySnapshot();
		}
	}
	
	
	public PlayerSnapshot getSnapshot(Player player) {
		for(PlayerSnapshot snapshot: snapshots) {
			if(snapshot.getPlayer() == player) {
				return snapshot;
			}
		}
		return null;
	}
	
	
	@EventHandler
	public void onLobbyJoin(LobbyJoinEvent e) {
		if(!e.teleportFromMinigame()) {
			snapshots.add(new PlayerSnapshot(e.getPlayer(), Arrays.asList(e.getLobby().lobbyGroup)));			
		}
	}
	
	@EventHandler
	public void onLobbyLeave(LobbyLeaveEvent e) {
		if(e.teleportToMinigame()) {
			return;
		}
		
		PlayerSnapshot snapshot = getSnapshot(e.getPlayer());
		if(snapshot != null) {
			snapshot.applySnapshot();
			snapshots.remove(snapshot);
		}
	}
	
	@EventHandler
	public void onMinigameJoin(MinigameJoinEvent e) {
		PermsUtils.setPlayerGroups(e.getPlayer(), Arrays.asList(e.getMinigame().gameGroup));
	}
	
	@EventHandler
	public void onMinigameLeave(MinigameLeaveEvent e) {
		if(e.teleportToLobby()) {
			Lobby lobby = lobbyManager.getLobby(e.getMinigame().lobbyWorld);
			if(lobby != null) {
				PermsUtils.setPlayerGroups(e.getPlayer(), Arrays.asList(lobby.lobbyGroup));				
			}
		} else {
			PlayerSnapshot snapshot = getSnapshot(e.getPlayer());
			if(snapshot != null) {
				snapshot.applySnapshot();
				snapshots.remove(snapshot);
			}
		}
	}
}
