package org.ultragore.everything.modules.Torch.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.ultragore.everything.modules.Torch.types.Cooldown;
import org.ultragore.everything.modules.Torch.types.UsableItem;
import org.ultragore.everything.utils.DottedMap;

public class ItemsManager implements Listener {
	public static final String ITEMS_USE_PERM = "everything.torch.consume";
	
	private Plugin plugin;
	private DottedMap messages;
	private CooldownManager cooldownManager;
	private List<UsableItem> items = new ArrayList<UsableItem>();
	
	public ItemsManager(Plugin plugin, CooldownManager cooldownManager, Map<String, Map> usableItems, DottedMap messages) {
		this.plugin = plugin;
		this.cooldownManager = cooldownManager;
		this.messages = messages;
		
		Set<String> keys = usableItems.keySet();
		for(String key: keys) {
			items.add(new UsableItem(key, new DottedMap(usableItems.get(key))));
		}
	}
	
	public List<String> getLabels() {
		List<String> toReturn = new ArrayList<String>();
		
		for(UsableItem item: items) {
			toReturn.add(item.getLabel());
		}
		
		return toReturn;
	}
	
	public UsableItem getItemByItemStack(ItemStack itemStack) {
		for(UsableItem item: items) {
			if(item.equals(itemStack)) {
				return item;
			}
		}
		
		return null;
	}
	
	public UsableItem getItemByLabel(String label) {
		for(UsableItem item: items) {
			if(label.equals(item.getLabel())) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Tries to give item to player with specific label
	 * @param player
	 * @param label
	 * @param amount
	 * @return code: 
	 *  1 - OK
	 *  0 - no such label
	 */
	public int giveItem(Player player, String label, int amount) {
		UsableItem uItem = getItemByLabel(label);
		if(uItem == null) {
			return 0;
		}
		
		PlayerInventory inv = player.getInventory();
		int stackSize = uItem.getMaterial().getMaxStackSize();
		
		ItemStack[] itemsToAdd = new ItemStack[(int) Math.ceil((double)amount / (double)stackSize)];
		ItemStack item = uItem.formItemStack(1);
		for(int i = 0; i < itemsToAdd.length; i++) {
			if(i == itemsToAdd.length - 1) {
				Double stacked = (double)amount / (double)stackSize;
				if ((stacked == Math.floor(stacked)) && !Double.isInfinite(stacked)) {
				    item.setAmount(stackSize);
				} else {
					item.setAmount((int) (stackSize * Double.parseDouble("0." + stacked.toString().split("\\.")[1])));
				}
			} else {
				item.setAmount(stackSize);
			}
			
			itemsToAdd[i] = item.clone();
		}
		
		Map<Integer, ItemStack> itemsToDrop = inv.addItem(itemsToAdd);
		if(itemsToDrop.size() > 0) {
			Location loc = player.getLocation().add(0, 0, 0);
			for(int key: itemsToDrop.keySet()) {
				loc.getWorld().dropItemNaturally(loc, itemsToDrop.get(key));
			}
		}
		
		return 1;
	}
	public static boolean hasEmptySlots(PlayerInventory inv, int amount) {
		int count = 0;
		
		ItemStack[] items = inv.getContents();
		for(int i = 0; i < 36; i++) {
			if(items[i] == null) {
				if(count == amount) {
					return true;
				}
				count++;
			}
		}
		
		return false;
	}
	
	
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		PlayerInventory inventory = e.getPlayer().getInventory();
		UsableItem uItem = getItemByItemStack(inventory.getItemInMainHand());
		if(Material.MILK_BUCKET.equals(e.getItem().getType())) {
			onConsumeMilk(e);
			return;
		} else if(uItem == null) {
			return;
		} else if(!e.getPlayer().hasPermission(ITEMS_USE_PERM)) {
			e.getPlayer().sendMessage(messages.getString("permission_denied"));
			return;
		}
		
		Player p = e.getPlayer();
		
		if(uItem.getCooldown() != null) {
			Cooldown c = cooldownManager.getCooldown(p.getName(), uItem.getLabel());
			if(c != null) {
				long currentTimestamp = System.currentTimeMillis();
				Integer secLeft = (int) (c.duration - ((currentTimestamp - c.createTimestamp) / 1000));
				
				p.sendMessage(messages.getString("cooldown").replaceAll("\\{left\\}", secLeft.toString()));
				return;
			} else {
				cooldownManager.createCooldown(p.getName(), uItem.getCooldown(), uItem.getLabel());
			}
		}
		
		String useMessage = uItem.getUseMessage();
		if(useMessage == null) {
			useMessage = messages.getString("default_use_message").replaceAll("\\{name\\}", uItem.getName());
		}
		p.sendMessage(useMessage);
		
		uItem.applyPotionEffects(p);
		uItem.spawnParticles(p.getLocation());
		uItem.playUseSound(p);
	}
	private void onConsumeMilk(PlayerItemConsumeEvent e) {
		List<Cooldown> playerCooldowns = cooldownManager.getCooldowns(e.getPlayer().getName());
		
		UsableItem uItem;
		for(Cooldown c: playerCooldowns) {
			uItem = getItemByLabel(c.labelInitiator);
			if(uItem.isMilkCancelable()) {
				cooldownManager.removeCooldown(c);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!e.hasItem() || !e.getAction().toString().startsWith("RIGHT_CLICK_")) {
			return;
		}
		
		PlayerInventory inventory = e.getPlayer().getInventory();
		UsableItem uItem = getItemByItemStack(inventory.getItemInMainHand());
		if(uItem == null) {
			return;
		} else if(!e.getPlayer().hasPermission(ITEMS_USE_PERM)) {
			e.getPlayer().sendMessage(messages.getString("permission_denied"));
			return;
		} else if(uItem.getMaterial().isEdible()) {
			return;
		}
		
		e.setCancelled(true);
		
		Player p = e.getPlayer();
		if(uItem.getCooldown() != null) {
			Cooldown c = cooldownManager.getCooldown(p.getName(), uItem.getLabel());
			if(c != null) {
				long currentTimestamp = System.currentTimeMillis();
				Integer secLeft = (int) (c.duration - ((currentTimestamp - c.createTimestamp) / 1000));
				
				p.sendMessage(messages.getString("cooldown").replaceAll("\\{left\\}", secLeft.toString()));
				return;
			} else {
				cooldownManager.createCooldown(p.getName(), uItem.getCooldown(), uItem.getLabel());
			}
		}
		
		ItemStack itemInHand = inventory.getItemInMainHand();
		if(itemInHand.getAmount() == 1) {
			class RemoveItemTask implements Runnable {
				int itemSlot;
				PlayerInventory inventory;
				
				public RemoveItemTask(int itemSlot, PlayerInventory inventory) {
					this.itemSlot = itemSlot;
					this.inventory = inventory;
				}
				
				@Override
				public void run() {
					ItemStack item = inventory.getItem(itemSlot);
					if(item != null && !Material.AIR.equals(item.getType())) {
						inventory.setItem(itemSlot, null);
					}
				}
			}
			
			Bukkit.getScheduler().runTaskLater(plugin, new RemoveItemTask(inventory.getHeldItemSlot(), inventory), 1);
		} else {
			itemInHand.setAmount(itemInHand.getAmount() - 1);
		}
		
		String useMessage = uItem.getUseMessage();
		if(useMessage == null) {
			useMessage = messages.getString("default_use_message").replaceAll("\\{name\\}", uItem.getName());
		}
		p.sendMessage(useMessage);
		
		uItem.applyPotionEffects(e.getPlayer());
		uItem.spawnParticles(e.getPlayer().getLocation());
		uItem.playUseSound(p);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		cooldownManager.removeCooldowns(e.getEntity().getName());
	}
}