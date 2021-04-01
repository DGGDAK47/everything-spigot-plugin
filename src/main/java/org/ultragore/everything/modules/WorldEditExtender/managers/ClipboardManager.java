package org.ultragore.everything.modules.WorldEditExtender.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ultragore.everything.modules.WorldEditExtender.types.ClipboardArea;

public class ClipboardManager implements Listener {
	private List<ClipboardArea> activeClipboards = new ArrayList<ClipboardArea>();
	
	
	public void addClipboard(ClipboardArea clipboard) {
		if(hasClipboard(clipboard.getPlayer())) {
			removeClipboard(clipboard.getPlayer());
		}
		activeClipboards.add(clipboard);
	}
	
	public boolean hasClipboard(Player holder) {
		for(ClipboardArea area: activeClipboards) {
			if(area.getPlayer() == holder) {
				return true;
			}
		}
		return false;
	}
	
	public ClipboardArea getClipboard(Player holder) {
		for(ClipboardArea area: activeClipboards) {
			if(area.getPlayer() == holder) {
				return area;
			}
		}
		return null;
	}
	
	public void removeClipboard(Player holder) {
		activeClipboards.remove(getClipboard(holder));
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(hasClipboard(event.getPlayer())) {
			removeClipboard(event.getPlayer());
			event.getPlayer().performCommand("clearclipboard");
		}
	}
}