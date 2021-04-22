package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.events.LobbyJoinEvent;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;
import org.ultragore.everything.modules.MinigamesAdapter.types.LobbyItem;

public class LobbyItemsManager implements Listener {
	private LobbyManager lobbyManager;
	
	
	public LobbyItemsManager(LobbyManager lobbyManager) {
		this.lobbyManager = lobbyManager;
	}
	
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM)) {
			return;
		}
		
		Lobby lobby = lobbyManager.getLobby(e.getPlayer());
		if(lobby != null) {
			List<LobbyItem> items = lobby.getLobbyItems();
			
			PlayerInventory inv = e.getPlayer().getInventory();
			for(LobbyItem item: items) {
				inv.setItem(item.getSlotNumber() - 1, item.createItemStack());
			}
		}
	}
	
	@EventHandler
	public void onLobbyJoin(LobbyJoinEvent e) {
		List<LobbyItem> items = e.getLobby().getLobbyItems();
		
		PlayerInventory inv = e.getPlayer().getInventory();
		for(LobbyItem item: items) {
			inv.setItem(item.getSlotNumber() - 1, item.createItemStack());
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!e.getAction().toString().startsWith("RIGHT_CLICK_")) {
			return;
		}
		
		Lobby lobby = lobbyManager.getLobby(e.getPlayer());
		if(lobby == null) {
			return;
		}
		
		ItemStack item = e.getItem();
		if(item == null) {
			return;
		}
		
		LobbyItem lobbyItem = lobby.getLobbyItem(item);
		if(lobbyItem != null) {
			e.getPlayer().performCommand(lobbyItem.getRightClickCommand());
		}
	}
}
