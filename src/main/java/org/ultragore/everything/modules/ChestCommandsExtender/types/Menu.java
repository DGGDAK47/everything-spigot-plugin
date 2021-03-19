package org.ultragore.everything.modules.ChestCommandsExtender.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scala.collection.immutable.HashMap.KeySet;

public class Menu {
	
	private Map<String, Object> menuSettings;
	private List<NativeIcon> nativeIcons = new ArrayList();
	private List<IconPatterns> patterns = new ArrayList();
	private List<IconRepeater> repeaters = new ArrayList();
	private String menuFileName;
	
	public Menu(Map<String, Object> map, String menuFileName) {
		
		this.menuFileName = menuFileName;
		
		Set<String> keys = map.keySet();
		Map<String, Object> item;
		for(String key: keys) {
			if(key.equals("menu-settings")) {
				this.menuSettings = (Map<String, Object>) map.get("menu-settings");
				continue;
			}
			
			item = (Map<String, Object>) map.get(key);
			if(item.containsKey("RANGE-X")) {
				repeaters.add(new IconRepeater(item));
			} else if(item.containsKey("PATTERNS")) {
				patterns.add(new IconPatterns(item));
			} else {
				nativeIcons.add(new NativeIcon(item));
			}
		}
	}
	
	public String getMenuFileName() {
		return this.menuFileName;
	}
	
	public Map<String, Object> createNativeMenuMap() {
		Map<String, Object> toReturn = new HashMap();
		
		toReturn.put("menu-settings", menuSettings);
		
		int counter = 1;
		for(NativeIcon ni: nativeIcons) {
			toReturn.put("item" + counter, ni.toNativeIconMap());
			counter++;
		}
		for(IconPatterns p: patterns) {
			for(NativeIcon ni: p.createIcons()) {
				toReturn.put("item" + counter, ni.toNativeIconMap());
				counter++;
			}
		}
		
		for(IconRepeater r: repeaters) {
			for(NativeIcon ni: r.createIcons()) {
				toReturn.put("item" + counter, ni.toNativeIconMap());
				counter++;
			}
		}
		
		
		return toReturn;
	}
}
