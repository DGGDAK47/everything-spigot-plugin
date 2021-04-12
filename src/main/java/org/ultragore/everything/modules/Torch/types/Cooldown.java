package org.ultragore.everything.modules.Torch.types;

import java.util.List;

import org.bukkit.scheduler.BukkitTask;

public class Cooldown {
	public BukkitTask task;
	public long createTimestamp;
	public String playerName;
	public List<String> labels;
	public String labelInitiator;
	
	public Cooldown(BukkitTask task, long createTimestamp, String playerName, List<String> labels, String labelInitiator) {
		this.task = task;
		this.createTimestamp = createTimestamp;
		this.playerName = playerName;
		this.labels = labels;
		this.labelInitiator = labelInitiator;
	}
}
