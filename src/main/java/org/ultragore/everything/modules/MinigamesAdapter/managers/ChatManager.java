package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;

public class ChatManager implements Listener{
	private LobbyManager lobbyManager;
	
	
	public ChatManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if(e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM)) {
			return;
		}
		
		Lobby lobby = lobbyManager.getLobby(e.getPlayer());
		if(lobby == null) {
			return;
		}
		
		e.setCancelled(true);
		
		String msg = lobby.messageFormat.replaceAll("\\{LOBBY\\}", lobby.name).replaceAll("\\{PLAYER\\}", e.getPlayer().getName()).replaceAll("\\{MESSAGE\\}", e.getMessage());
		
		List<Player> participants = lobby.getParticipants();
		for(Player p: participants) {
			p.sendMessage(msg);
		}
	}
}
