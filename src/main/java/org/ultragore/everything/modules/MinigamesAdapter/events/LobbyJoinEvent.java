package org.ultragore.everything.modules.MinigamesAdapter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;

public class LobbyJoinEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private Lobby lobby;
	private boolean teleportFromMinigame;
	
	public LobbyJoinEvent(Player who, Lobby lobby, boolean teleportFromMinigame) {
		super(who);
		this.lobby = lobby;
		this.teleportFromMinigame = teleportFromMinigame;
	}

	public Lobby getLobby() {
		return lobby;
	}
	
	public boolean teleportFromMinigame() {
		return teleportFromMinigame;
	}
	
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
