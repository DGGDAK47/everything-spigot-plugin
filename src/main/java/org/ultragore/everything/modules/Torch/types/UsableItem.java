package org.ultragore.everything.modules.Torch.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ultragore.everything.utils.DottedMap;

import net.md_5.bungee.api.ChatColor;

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
	private ArrayList<PotionEffect> potionEffects = null;
	private Map<PotionEffect, Integer> randomPotionEffects = null;
	private Integer foodLevelUp = null;
	private Color potionColor = null;
	private boolean addPotionEffects = true;
	
	
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
		
		ob = map.getObject("potion_effects");
		if(ob != null) {
			potionEffects = new ArrayList<PotionEffect>();
			randomPotionEffects = new Hashtable<PotionEffect, Integer>();
			
			List<String> effects = (List<String>) ob;
			String[] effectData;
			int amplifier;
			PotionEffect potionEffect;
			for(String effect: effects) {
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
		}
		
		
		ob = map.getList("use_particles");
		if(ob != null) {
			List<String> useParticlesData = (List<String>) ob;
			useParticles = new ArrayList<ParticleData>();
			for(String data: useParticlesData) {
				useParticles.add(new ParticleData(data));
			}
		}
		
		
		foodLevelUp = map.getInteger("food_level_up");
		
		String potionColor = map.getString("potion_color");
		if(potionColor != null) {
			// potionColor -> "R G B"
			String[] rgb = potionColor.split(" ");
			this.potionColor = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		}
		
		ob = map.getBoolean("add_potion_effects");
		if(ob != null) {
			addPotionEffects = (boolean) ob;
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

	public boolean addPotionEffects() {
		return this.addPotionEffects;
	}
	
	
	public ItemStack formItemStack(int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(name);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		if(lore != null) {
			meta.setLore(lore);
		}
		
		if(Material.POTION == material) {
			PotionMeta pmeta = (PotionMeta) meta;
			
			if(potionEffects != null && addPotionEffects) {
				for(PotionEffect pe: potionEffects) {
					pmeta.addCustomEffect(pe, true);
				}
			}
			
			if(potionColor != null) {
				pmeta.setColor(potionColor);				
			} else {
				pmeta.setColor(Color.PURPLE);
			}
		}
		
		item.setItemMeta(meta);
		
		if(enchanted) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		return item;
	}
	
	
	public void addFoodLevel(Player p) {
		if(foodLevelUp != null) {
			if(p.getFoodLevel() + foodLevelUp < 0) {
				p.setFoodLevel(0);
			} else {
				p.setFoodLevel(p.getFoodLevel() + foodLevelUp);				
			}
		}
	}
	
	public void spawnParticles(Location loc) {
		if(useParticles != null) {
			World world = loc.getWorld();
			for(ParticleData data: useParticles) {
				world.spawnParticle(data.particle, loc, data.count, data.offsetX, data.offsetY, data.offsetZ);
			}
		}
	}
	
	public void applyPotionEffects(Player p) {
		if(potionEffects != null) {
			for(PotionEffect effect: potionEffects) {
				effect.apply(p);
			}
		}
	}
	
	public void applyRandomPotionEffects(Player p) {
		if(randomPotionEffects == null) {
			return;
		}
		
		List<PotionEffect> toApply = new ArrayList<PotionEffect>();
		
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