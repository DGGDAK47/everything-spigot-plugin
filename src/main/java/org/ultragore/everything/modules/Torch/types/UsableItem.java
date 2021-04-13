package org.ultragore.everything.modules.Torch.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ultragore.everything.utils.DottedMap;

public class UsableItem {
	private String label;
	private String name;
	private List<String> lore = null;
	private Material material;
	private String useMessage = null;
	private Integer cooldown = null;
	private Sound useSound = null;
	private boolean enchanted;
	private boolean milkCancel;
	private List<ParticleData> useParticles = null;
	private ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	private Map<PotionEffect, Integer> randomPotionEffects = new Hashtable<PotionEffect, Integer>();
	
	
	public UsableItem(String label, DottedMap map) {
		this.label = label;
		name = map.getString("name");
		
		Object ob = map.getList("lore");
		if(ob != null) {
			lore = (List<String>) ob;
		}
		
		material = Material.valueOf(map.getString("material"));
		
		ob = map.getString("use_message");
		if(ob != null) {
			useMessage = (String) ob;
		}
		
		ob = map.getInteger("cooldown");
		if(ob != null) {
			cooldown = (Integer) ob;
		}
		
		ob = map.getString("use_sound");
		if(ob != null) {
			useSound = Sound.valueOf((String) ob);
		}
		
		enchanted = map.getBoolean("enchanted");
		
		milkCancel = map.getBoolean("milk_cancel");
		
		List<String> potionEffects = (List) map.getList("potion_effects");
		String[] effectData;
		int amplifier;
		PotionEffect potionEffect;
		for(String effect: potionEffects) {
			// effect -> "<effect>,<duration>[,amplifier[,chance]]"
			effectData = effect.split(",");
			
			if(effectData.length >= 3) {
				amplifier = Integer.parseInt(effectData[2]);
			} else {
				amplifier = 1;
			}
			
			potionEffect = new PotionEffect(PotionEffectType.getByName(effectData[0]), Integer.parseInt(effectData[1]) * 20, amplifier);
			
			if(effectData.length == 4) {
				randomPotionEffects.put(potionEffect, Integer.parseInt(effectData[3]));
			} else {
				this.potionEffects.add(potionEffect);
			}
		}
		
		ob = map.getList("use_particles");
		if(ob != null) {
			List<String> useParticlesData = (List<String>) ob;
			useParticles = new ArrayList<ParticleData>();
			for(String data: useParticlesData) {
				useParticles.add(new ParticleData(data));
			}
		}
		
	}
	
	

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public Material getMaterial() {
		return material;
	}

	public String getUseMessage() {
		return useMessage;
	}

	public Integer getCooldown() {
		return cooldown;
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public boolean isMilkCancelable() {
		return milkCancel;
	}

	
	public ItemStack formItemStack(int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(name);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if(lore != null) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		
		if(enchanted) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		return item;
	}
	
	public void spawnParticles(Location loc) {
		World world = loc.getWorld();
		for(ParticleData data: useParticles) {
			world.spawnParticle(data.particle, loc, data.count, data.offsetX, data.offsetY, data.offsetZ);
		}
	}
	
	public void applyPotionEffects(Player p) {
		List<PotionEffect> toApply = (List<PotionEffect>) potionEffects.clone();
		
		Set<PotionEffect> keys = randomPotionEffects.keySet();
		int chance;
		for(PotionEffect effect: keys) {
			chance = randomPotionEffects.get(effect);
			if(getRandomNumber(0, 100) + 1 <= chance) {
				toApply.add(effect);
			}
		}
		
		for(PotionEffect effect: toApply) {
			effect.apply(p);
		}
	}
	private static int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	public void playUseSound(Player p) {
		if(useSound != null) {
			p.playSound(p.getLocation(), useSound, 2.0f, 0.5f);			
		}
	}
	
	@Override
	public boolean equals(Object ob) {
		if(!(ob instanceof ItemStack)) {
			return false;
		}
		
		ItemStack item = (ItemStack) ob;
		ItemMeta meta = item.getItemMeta();
		
		if(meta == null) {
			return false;
		}
		
		if(!material.equals(item.getType())) {
			return false;
		} else if(!name.equals(meta.getDisplayName())) {
			return false;
		} else if(lore != null && (!meta.hasLore() || !meta.getLore().equals(lore))) {
			return false;
		} else if(lore == null && meta.hasLore()) {
			return false;
		} else if(enchanted && !meta.hasEnchant(Enchantment.DURABILITY)) {
			return false;
		}
		
		return true;
	}
}