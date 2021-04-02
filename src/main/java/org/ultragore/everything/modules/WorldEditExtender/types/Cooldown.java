package org.ultragore.everything.modules.WorldEditExtender.types;

import org.bukkit.scheduler.BukkitTask;

public class Cooldown {
	public String playerName;
	public BukkitTask task;
	public long createTimestamp;
	
	public Cooldown(String playerName, BukkitTask task, long createTimestamp) {
		this.playerName = playerName;
		this.task = task;
		this.createTimestamp = createTimestamp;
	}
	
}