package org.ultragore.everything.modules.MinigamesAdapter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;

public class LobbyLeaveEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private Lobby lobby;
	private boolean teleportToMinigame;
	
	public LobbyLeaveEvent(Player who, Lobby lobby, boolean teleportToMinigame) {
		super(who);
		this.lobby = lobby;
		this.teleportToMinigame = teleportToMinigame;
	}

	public Lobby getLobby() {
		return lobby;
	}
	
	public boolean teleportToMinigame() {
		return teleportToMinigame;
	}
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }
}
