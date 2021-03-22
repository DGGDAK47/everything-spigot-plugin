package org.ultragore.everything.modules.Casino.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.ultragore.everything.modules.Casino.Casino;
import org.ultragore.everything.modules.Casino.types.Machine;
import org.ultragore.everything.modules.Casino.types.Roll;
import org.ultragore.everything.utils.DottedMap;

import com.sk89q.worldedit.event.platform.PlayerInputEvent;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.milkbowl.vault.economy.Economy;

public class RollManager implements Listener{
	
	private class ActiveRoll implements Runnable {
		static final int END_SCORE = 35;
		static final int STOP_LINE_STEP = 6;
		static final long DELAY = 5;
		
		Roll roll;
		BukkitTask task;
		

		public ActiveRoll(Machine machine, Player participant) {
			this.roll = new Roll(machine, participant);
			task = Bukkit.getScheduler().runTaskLater(module.getPlugin(), this, 20);
		}
		
		@Override
		public void run() {
			List<List<Block>> linesToDepose = new ArrayList();
			if(roll.scrollsCount > END_SCORE) {
				rollDone(this);
				return;
			} else if(roll.scrollsCount < END_SCORE - (STOP_LINE_STEP * roll.lines.size()) + STOP_LINE_STEP) {
				linesToDepose = roll.lines;
			} else {
				int linesAmountToScroll = (int) ((END_SCORE - roll.scrollsCount) / STOP_LINE_STEP);
				if(linesAmountToScroll == 0) {
					linesAmountToScroll = 1;
				} else {
					linesAmountToScroll++;
				}
				
				linesToDepose = roll.lines.subList(roll.lines.size() - linesAmountToScroll, roll.lines.size());
				for(int i = END_SCORE; i > 0; i -= STOP_LINE_STEP) {
					if(i == roll.scrollsCount) {
						playSound(roll.machine, lineStopSound);
						break;
					}
				}
			}
			
			for(List<Block> line: linesToDepose) {
				roll.deposeLineDown(line);
			}
			
			playSound(roll.machine, scrollSound);
			roll.scrollsCount++;
			task = Bukkit.getScheduler().runTaskLater(module.getPlugin(), this, DELAY);
		}

	}
	
	private List<ActiveRoll> activeRolls = new <ActiveRoll>ArrayList();
	private Sound scrollSound;
	private Sound loseSound;
	private Sound rewardSound;
	private Sound lineStopSound;
	private Casino module;
	private Economy eco;
	private DottedMap messages;
	
	public RollManager(Casino module, Economy eco, Sound scrollSound, Sound loseSound, Sound rewardSound, Sound lineStopSound, DottedMap messages) {
		this.module = module;
		this.eco = eco;
		this.scrollSound = scrollSound;
		this.loseSound = loseSound;
		this.rewardSound = rewardSound;
		this.lineStopSound = lineStopSound;
		this.messages = messages;
	}
	
	
	public void rollDone(ActiveRoll ar) {
		deleteRoll(ar);
		
		boolean sameBlocksTypeInRow = true;
		Material prevMat = null;
		for(Block block: ar.roll.getBlocksInRow(ar.roll.machine.winLine)) {
			if(prevMat == null) {
				prevMat = block.getType();
			} else if(prevMat != block.getType()) {
				sameBlocksTypeInRow = false;
				break;
			}
		}
		
		if(sameBlocksTypeInRow && prevMat != null) {
			Float reward = ar.roll.machine.getBlockReward(prevMat);
			
			if(reward != null) {
				ar.roll.participant.sendMessage(messages.getString("reward_message").replaceAll("\\{reward\\}", reward.toString()));
				eco.depositPlayer(ar.roll.participant, reward);
				playSound(ar.roll.machine, rewardSound);
			} else {
				ar.roll.participant.sendMessage(messages.getString("lose_message"));
				playSound(ar.roll.machine, loseSound);
			}
		} else {
			ar.roll.participant.sendMessage(messages.getString("lose_message"));
			playSound(ar.roll.machine, loseSound);
		}
	}
	
	public void playSound(Machine machine, Sound sound) {
		machine.leverLocation.getWorld().playSound(machine.leverLocation, sound, 0.2F, 1.0F);
	}
	
	
	public ActiveRoll getRoll(Player player) {
		for(ActiveRoll ar: activeRolls) {
			if(ar.roll.participant == player) {
				return ar;
			}
		}
		
		return null;
	}
	
	public boolean hasRoll(Machine machine) {
		for(ActiveRoll ar: activeRolls) {
			if(ar.roll.machine == machine) {
				return true;
			}
		}
		
		return false;
	}
	
	public void createRoll(Machine machine, Player participant) {
		activeRolls.add(new ActiveRoll(machine, participant));
	}
	
	public void deleteRoll(ActiveRoll ar) {
		activeRolls.remove(ar);
	}
		
	public void deleteActiveRolls() {
		for(ActiveRoll roll: activeRolls) {
			roll.task.cancel();
			deleteRoll(roll);
		}
	}
	
	
	public static void reverseLeverState(Block block) {
		Powerable p = (Powerable) block.getBlockData();
		p.setPowered(true);
		block.setBlockData(p);
		block.getState().update();
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ActiveRoll roll = getRoll(event.getPlayer());
		if(roll != null) {
			deleteRoll(roll);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getPlayer() != null && AuthMeApi.getInstance().isAuthenticated(event.getPlayer()) &&
		   event.getAction() == Action.RIGHT_CLICK_BLOCK && 
		   event.getClickedBlock().getType() == Material.LEVER) {
			
			Player p = event.getPlayer();
			Machine machine = module.getMachineManager().getMachineByLever(event.getClickedBlock().getLocation());
			
			if(machine == null) {
				return;
			} else if(hasRoll(machine)) {
				p.sendMessage(messages.getString("machine_busy"));
				reverseLeverState(event.getClickedBlock());
				return;
			}
			
			reverseLeverState(event.getClickedBlock());
			
			if(eco.has(p, machine.rollPrice)) {
				eco.withdrawPlayer(p, machine.rollPrice);
				p.sendMessage(messages.getString("roll_start").replaceAll("\\{fee\\}", String.valueOf(machine.rollPrice)));
				createRoll(machine, p);
			} else {
				p.sendMessage(messages.getString("not_enough_cash").replaceAll("\\{fee\\}", String.valueOf(machine.rollPrice)));
			}
			
		}
	}
}
