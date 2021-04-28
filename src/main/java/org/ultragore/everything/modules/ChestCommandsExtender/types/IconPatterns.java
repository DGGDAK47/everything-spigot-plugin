package org.ultragore.everything.modules.ChestCommandsExtender.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IconPatterns {
	private NativeIcon iconSample;
	private List<String> patterns;
	private String argument;
	private Integer menuTopLineY = 1;
	private Integer menuSize = 6;
	private Direction direction = Direction.HORIZONTAL;
	
	public IconPatterns(Map<String, Object> map) {
		this.iconSample = new NativeIcon(map);
		this.patterns = (List) map.get("PATTERNS");
		this.argument = (String) map.get("ARGUMENT");
		
		if(map.containsKey("MENU-TOP-LINE-Y")) {
			this.menuTopLineY = (Integer) map.get("MENU-TOP-LINE-Y");
		}
		
		if(map.containsKey("MENU-SIZE")) {
			this.menuSize = (Integer) map.get("MENU-SIZE");
		}
		
		if(map.containsKey("DIRECTION")) {
			this.direction = Direction.valueOf((String) map.get("DIRECTION"));
		}
	}
	
	public List<NativeIcon> createIcons() {
		List<NativeIcon> toReturn = new ArrayList();
		
		NativeIcon icon;
		List<String> newLore;
		List<String> newActions;
		int positionX = iconSample.positionX;
		int positionY = iconSample.positionY;
		for(String pattern: patterns) {
			icon = iconSample.clone();
			
			if(pattern.contains("$")) {
				String[] modifiers = pattern.split("\\$");
				
				if(modifiers.length >= 2) {
					icon.price = round(icon.price * Double.parseDouble(modifiers[1]), 2);
				}
				
				if(modifiers.length == 3) {
					icon.amount = Integer.parseInt(modifiers[2]);
				}
				
				icon.material = modifiers[0].replaceAll("\\{arg\\}", argument);
			} else {
				icon.material = pattern.replaceAll("\\{arg\\}", argument);
			}
			
			
			
			if(icon.lore != null) {
				newLore = new ArrayList<String>();
				for(String line: icon.lore) {
					if(icon.amount != null) {
						line = line.replaceAll("\\{AMOUNT\\}", icon.amount.toString());
					}
					if(icon.price != null) {
						line = line.replaceAll("\\{PRICE\\}", icon.price.toString());
					}
					newLore.add(line);
				}
				icon.lore = newLore;
			}
			
			if(icon.actions != null) {
				newActions = new ArrayList<String>();
				
				String enchantments = "";
				if(icon.enchantments != null) {
					for(String ench: icon.enchantments) {
						enchantments += ench + ' ';
					}
					
					enchantments = enchantments.substring(0, enchantments.length()-1);
				}
				
				for(String command: icon.actions) {
					if(icon.amount != null) {
						command = command.replaceAll("\\{AMOUNT\\}", icon.amount.toString());
					}
					command = command.replaceAll("\\{MATERIAL\\}", icon.material);
					command = command.replaceAll("\\{ENCHANTMENTS\\}", enchantments);
					newActions.add(command);
				}
				icon.actions = newActions;
			}
			
			icon.positionX = positionX;
			icon.positionY = positionY;
			
			toReturn.add(icon);
			
			if(direction == Direction.HORIZONTAL) {
				positionX++;
				if(positionX > 9) {
					positionX = 1;
					positionY++;
				}
			} else if(direction == Direction.VERTICAL) {
				positionY++;
				if(positionY > menuSize) {
					positionY = menuTopLineY;
					positionX++;
				}
			}
		}
		
		return toReturn;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
