package org.ultragore.everything.modules.ChestCommandsExtender.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeIcon {
	
	public Float price = null;
	public Integer amount = null;
	public List<String> actions = null;
	public Boolean keepOpen = null;
	public String name = null;
	public List<String> lore = null;
	public List<String> enchantments = null;
	public Integer positionX = null;
	public Integer positionY = null;
	public String material = null;
	
	public NativeIcon(Map<String, Object> icon) {
		if(icon == null) {
			return;
		}
		
		if(icon.containsKey("PRICE")) {
			Object ob = icon.get("PRICE");
			if (ob instanceof Float) {
				this.price = (Float) icon.get("PRICE");
			} else {
				this.price = Float.valueOf((Integer) icon.get("PRICE"));
			}
		}
		
		if(icon.containsKey("AMOUNT")) {
			this.amount = (Integer) icon.get("AMOUNT");
		}
		
		if(icon.containsKey("ACTIONS")) {
			this.actions = (List<String>) icon.get("ACTIONS");
		}
		
		if(icon.containsKey("KEEP-OPEN")) {
			this.keepOpen = (boolean) icon.get("KEEP-OPEN");
		}
		
		if(icon.containsKey("NAME")) {
			this.name = (String) icon.get("NAME");
		}
		
		if(icon.containsKey("LORE")) {
			this.lore = (List<String>) icon.get("LORE");
		}
		
		if(icon.containsKey("ENCHANTMENTS")) {
			this.enchantments = (List<String>) icon.get("ENCHANTMENTS");
		}
		
		if(icon.containsKey("POSITION-X")) {
			this.positionX = (Integer) icon.get("POSITION-X");
		}
		
		if(icon.containsKey("POSITION-Y")) {
			this.positionY = (Integer) icon.get("POSITION-Y");
		}
		
		if(icon.containsKey("MATERIAL")) {
			this.material = (String) icon.get("MATERIAL");
		}
	}
	
	public Map<String, Object> toNativeIconMap() {
		Map<String, Object> toReturn = new HashMap();
		
		if(price != null) {
			toReturn.put("PRICE", price);
		}
		
		if(amount != null) {
			toReturn.put("AMOUNT", amount);
		}
		
		if(actions != null) {
			toReturn.put("ACTIONS", actions);
		}
		
		if(keepOpen != null) {
			toReturn.put("KEEP-OPEN", keepOpen);
		}
		
		if(name != null) {
			toReturn.put("NAME", name);
		}
		
		if(lore != null) {
			toReturn.put("LORE", lore);
		}
		
		if(enchantments != null) {
			toReturn.put("ENCHANTMENTS", enchantments);
		}
		
		if(positionX != null) {
			toReturn.put("POSITION-X", positionX);
		}
		
		if(positionY != null) {
			toReturn.put("POSITION-Y", positionY);
		}
		
		if(material != null) {
			toReturn.put("MATERIAL", material);
		}
		
		return toReturn;
	}
	
	public NativeIcon clone() {
		NativeIcon ob = new NativeIcon(null);
		
		ob.actions = actions;
		ob.amount = amount;
		ob.enchantments = enchantments;
		ob.keepOpen = keepOpen;
		ob.lore = lore;
		ob.material = material;
		ob.name = name;
		ob.positionX = positionX;
		ob.positionY = positionY;
		ob.price = price;
		
		return ob;
	}
}