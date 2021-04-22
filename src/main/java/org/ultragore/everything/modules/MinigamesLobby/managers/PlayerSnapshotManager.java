package org.ultragore.everything.modules.MinigamesLobby.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ultragore.everything.modules.MinigamesLobby.events.LobbyJoinEvent;
import org.ultragore.everything.modules.MinigamesLobby.events.LobbyLeaveEvent;
import org.ultragore.everything.modules.MinigamesLobby.types.PlayerSnapshot;

public class PlayerSnapshotManager implements Listener {
	private List<PlayerSnapshot> snapshots = new ArrayList<PlayerSnapshot>();
	
	
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
		snapshots.add(new PlayerSnapshot(e.getPlayer(), Arrays.asList(e.getLobby().lobbyGroup)));
	}
	
	@EventHandler
	public void onLobbyLeave(LobbyLeaveEvent e) {
		PlayerSnapshot snapshot = getSnapshot(e.getPlayer());
		if(snapshot != null) {
			snapshot.applySnapshot();
			snapshots.remove(snapshot);
		}
	}
}
