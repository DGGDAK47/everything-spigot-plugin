package org.ultragore.everything.modules.Torch.types;

import java.util.List;

import org.bukkit.scheduler.BukkitTask;

public class Cooldown {
	public BukkitTask task;
	public long createTimestamp;
	public int duration;
	public String playerName;
	public List<String> labels;
	public String labelInitiator;
	
	public Cooldown(BukkitTask task, long createTimestamp, int duration, String playerName, List<String> labels, String labelInitiator) {
		this.task = task;
		this.createTimestamp = createTimestamp;
		this.duration = duration;
		this.playerName = playerName;
		this.labels = labels;
		this.labelInitiator = labelInitiator;
	}
}
