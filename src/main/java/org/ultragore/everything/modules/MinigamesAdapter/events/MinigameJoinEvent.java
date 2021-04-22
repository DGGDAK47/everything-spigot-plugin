package org.ultragore.everything.modules.MinigamesAdapter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Minigame;

public class MinigameJoinEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private Minigame minigame;
	private boolean teleportFromLobby;
	
	public MinigameJoinEvent(Player who, Minigame minigame, boolean teleportFromLobby) {
		super(who);
		this.minigame = minigame;
		this.teleportFromLobby = teleportFromLobby;
	}
	
	
	
	public Minigame getMinigame() {
		return minigame;
	}

	public boolean teleportFromLobby() {
		return teleportFromLobby;
	}
	
	

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}