package org.ultragore.everything.modules.MinigamesAdapter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Minigame;

public class MinigameLeaveEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private Minigame minigame;
	private boolean teleportToLobby;
	
	
	public MinigameLeaveEvent(Player who, Minigame minigame, boolean teleportToLobby) {
		super(who);
		this.minigame = minigame;
		this.teleportToLobby = teleportToLobby;
	}

	
	public Minigame getMinigame() {
		return minigame;
	}
	
	public boolean teleportToLobby() {
		return teleportToLobby;
	}
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
