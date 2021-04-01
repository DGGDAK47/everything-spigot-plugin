package org.ultragore.everything.modules.WorldEditExtender.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.PlayerUnderCooldownException;

public class CooldownManager {
	private Plugin plugin;
	private List<String> underCooldown = new ArrayList<String>();
	
	
	public CooldownManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	
	public boolean isUnderCooldown(Player p) {
		return underCooldown.contains(p.getName());
	}
	
	public void createCooldown(Player p, int seconds) {
		if(underCooldown.contains(p.getName())) {
			throw new PlayerUnderCooldownException();
		}
		
		class RemoveCooldownTask implements Runnable {
			String name;
			
			public RemoveCooldownTask(String name) {
				this.name = name;
			}
			
			@Override
			public void run() {
				underCooldown.remove(name);
			}
		}
		Bukkit.getScheduler().runTaskLater(plugin, new RemoveCooldownTask(p.getName()), seconds * 20);
		
		underCooldown.add(p.getName());
	}
	
	private void removeCooldown(String name) {
		underCooldown.remove(name);
	}
}
