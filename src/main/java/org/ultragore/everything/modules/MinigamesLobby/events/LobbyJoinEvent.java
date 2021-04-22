package org.ultragore.everything.modules.MinigamesLobby.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.ultragore.everything.modules.MinigamesLobby.types.Lobby;

public class LobbyJoinEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private Lobby lobby;
	
	public LobbyJoinEvent(Player who, Lobby lobby) {
		super(who);
		this.lobby = lobby;
	}

	public Lobby getLobby() {
		return lobby;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
