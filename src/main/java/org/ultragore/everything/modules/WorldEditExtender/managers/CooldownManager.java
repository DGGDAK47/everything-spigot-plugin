package org.ultragore.everything.modules.WorldEditExtender.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.PlayerUnderCooldownException;
import org.ultragore.everything.modules.WorldEditExtender.types.Cooldown;

public class CooldownManager {
	private List<Cooldown> cooldowns = new ArrayList<Cooldown>();
	private Plugin plugin;
	
	
	public CooldownManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	
	public Cooldown getCooldown(Player p) {
		for(Cooldown c: cooldowns) {
			if(c.playerName.equals(p.getName())) {
				return c;
			}
		}
		return null;
	}
	
	public boolean isUnderCooldown(Player p) {
		for(Cooldown c: cooldowns) {
			if(c.playerName.equals(p.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public void createCooldown(Player p, int delaySec) {
		Cooldown c = getCooldown(p);
		if(c != null) {
			long currentTimestamp = System.currentTimeMillis();
			throw new PlayerUnderCooldownException( (int) (delaySec - ((currentTimestamp - c.createTimestamp) / 1000)) );
		}
		
		class RemoveCooldownTask implements Runnable {
			private String playerName;
			
			public RemoveCooldownTask(String playerName) {
				this.playerName = playerName;
			}
			
			@Override
			public void run() {
				removeCooldown(playerName, false);
			}
		}
		
		BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new RemoveCooldownTask(p.getName()), delaySec * 20);
		cooldowns.add(new Cooldown(p.getName(), task, System.currentTimeMillis()));
	}
	
	private void removeCooldown(String playerName, boolean cancelTask) {
		Cooldown cooldown = null;
		for(Cooldown c: cooldowns) {
			if(c.playerName.equals(playerName)) {
				cooldown = c;
				break;
			}
		}
		
		if(cooldown != null) {
			if(cancelTask) {
				cooldown.task.cancel();				
			}
			cooldowns.remove(cooldown);
		}
	}
	
	public void removeCooldowns() {
		for(Cooldown c: cooldowns) {
			c.task.cancel();
		}
		cooldowns = new ArrayList<Cooldown>();
	}
}
