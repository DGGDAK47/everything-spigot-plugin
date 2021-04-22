package org.ultragore.everything.modules.MinigamesLobby.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ultragore.everything.utils.PermsUtils;

public class PlayerSnapshot {
	private Player player;
	private float exp;
	private int level;
	private double health;
	private int foodLevel;
	private ItemStack[] inventoryContents;
	private List<String> groups;
	
	
	public PlayerSnapshot(Player player, List<String> newGroups) {
		this.player = player;
		
		this.exp = player.getExp();
		this.level = player.getLevel();
		player.setExp(0);
		player.setLevel(0);
		
		health = player.getHealth();
		foodLevel = player.getFoodLevel();
		player.setHealth(20.0);
		player.setFoodLevel(20);
		
		groups = PermsUtils.getPlayerGroups(player);
		PermsUtils.setPlayerGroups(player, newGroups);
		
		ItemStack[] items = player.getInventory().getContents();
		inventoryContents = new ItemStack[items.length];
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null) {
				inventoryContents[i] = null;
			} else {
				inventoryContents[i] = items[i].clone();
			}
		}
		player.getInventory().clear();
	}
	
	
	public void applySnapshot() {
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setContents(inventoryContents);
		
		player.setExp(exp);
		player.setLevel(level);
		
		player.setHealth(health);
		player.setFoodLevel(foodLevel);
		
		PermsUtils.setPlayerGroups(player, groups);
	}
	
	
	public Player getPlayer() {
		return player;
	}
}
