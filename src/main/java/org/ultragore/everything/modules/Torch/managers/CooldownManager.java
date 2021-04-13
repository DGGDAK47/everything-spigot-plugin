package org.ultragore.everything.modules.Torch.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ultragore.everything.modules.Torch.types.Cooldown;

public class CooldownManager {
	private Plugin plugin;
	private List<Cooldown> activeCooldowns = new ArrayList<Cooldown>();
	private Map<String, List<String>> groupCooldownLabels;
	
	
	public CooldownManager(Plugin plugin, Map<String, List<String>> groupCooldownLabels) {
		this.plugin = plugin;
		this.groupCooldownLabels = groupCooldownLabels;
	}
	
	
	public List<String> getCooldownGroup(String label) {
		Set<String> keys = groupCooldownLabels.keySet();
		for(String key: keys) {
			if(groupCooldownLabels.get(key).contains(label)) {
				return groupCooldownLabels.get(key);
			}
		}
		
		return null;
	}
	
	
	public Cooldown getCooldown(String playerName, String label) {
		for(Cooldown c: activeCooldowns) {
			if(c.playerName.equals(playerName) && c.labels.contains(label)) {
				return c;
			}
		}
		
		return null;
	}
	
	public List<Cooldown> getCooldowns(String playerName) {
		List<Cooldown> toReturn = new ArrayList<Cooldown>();
		
		for(Cooldown c: activeCooldowns) {
			if(c.playerName.equals(playerName)) {
				toReturn.add(c);
			}
		}
		
		return toReturn;
	}
	
	public void removeCooldown(String playerName, String label) {
		Cooldown toRemove = null;
		
		for(Cooldown c: activeCooldowns) {
			if(c.playerName.equals(playerName) && c.labels.contains(label)) {
				toRemove = c;
				c.task.cancel();
				break;
			}
		}
		
		activeCooldowns.remove(toRemove);
	}
	
	public void removeCooldown(Cooldown cooldown) {
		cooldown.task.cancel();
		activeCooldowns.remove(cooldown);
	}
	
	public void removeCooldowns(String playerName) {
		for(Cooldown c: getCooldowns(playerName)) {
			removeCooldown(c);
		}
	}
	
	public void removeAllCooldowns() {
		for(Cooldown c: activeCooldowns) {
			c.task.cancel();
		}
		
		activeCooldowns = new ArrayList<Cooldown>();
	}
	
	public void createCooldown(String playerName, int duration, String label) {
		List<String> labels = getCooldownGroup(label);
		
		if(labels == null) {
			labels = Arrays.asList(label);
		}
		
		class CooldownExpireTask implements Runnable {
			String playerName;
			String label;
			
			public CooldownExpireTask(String playerName, String label) {
				this.playerName = playerName;
				this.label = label;
			}
			
			@Override
			public void run() {
				removeCooldown(playerName, label);
			}
		}
		
		BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new CooldownExpireTask(playerName, label), duration * 20);
		activeCooldowns.add(new Cooldown(task, System.currentTimeMillis(), duration, playerName, labels, label));
	}
}