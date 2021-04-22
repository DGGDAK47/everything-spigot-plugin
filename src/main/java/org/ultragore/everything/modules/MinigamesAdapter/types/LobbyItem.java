package org.ultragore.everything.modules.MinigamesAdapter.types;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItem {
	private String name;
	private List<String> lore;
	private int slotNumber;
	private boolean enchanted;
	private Material material;
	private String rightClickCommand;
	
	
	public LobbyItem(Map<String, Object> map) {
		name = (String) map.get("name");
		lore = (List<String>) map.get("lore");
		slotNumber = (Integer) map.get("slot_number");
		enchanted = (Boolean) map.get("enchanted");
		material = Material.valueOf((String) map.get("material"));
		rightClickCommand = (String) map.get("right_click_command");
	}
	
	
	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public Material getMaterial() {
		return material;
	}

	public String getRightClickCommand() {
		return rightClickCommand;
	}
	
	
	public ItemStack createItemStack() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		if(enchanted) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		
		return item;
	}
	
	@Override
	public boolean equals(Object ob) {
		if(!(ob instanceof ItemStack)) {
			return false;
		}
		
		ItemStack item = (ItemStack) ob;
		ItemMeta meta = item.getItemMeta();
		
		if(!material.equals(item.getType())) {
			return false;
		} else if(!name.equals(meta.getDisplayName())) {
			return false;
		} else if(lore.size() > 0 && (!meta.hasLore() || !meta.getLore().equals(lore))) {
			return false;
		} else if(lore.size() == 0 && meta.hasLore()) {
			return false;
		}
		
		return true;
	}
}
