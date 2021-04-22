package org.ultragore.everything.modules.MinigamesAdapter.types;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class TimeHolder implements Runnable {
	public World world;
	public long time;
	public BukkitTask task;
	
	public TimeHolder(World world, long time) {
		this.world = world;
		this.time = time;
	}
	
	@Override
	public void run() {
		world.setTime(time);
	}
}
