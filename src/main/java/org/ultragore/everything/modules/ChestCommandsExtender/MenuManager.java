package org.ultragore.everything.modules.ChestCommandsExtender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.ultragore.everything.modules.ChestCommandsExtender.types.Menu;
import org.yaml.snakeyaml.Yaml;

public class MenuManager {
	
	private Yaml yaml;
	private ChestCommandsExtender module;
	private List<Menu> loadedMenus;
	
	public MenuManager(ChestCommandsExtender module) {
		this.module = module;
		this.yaml = new Yaml();
	}
	
	
	public Menu getMenu(String menuFileName) {
		for(Menu m: loadedMenus) {
			if(m.getMenuFileName().equals(menuFileName)) {
				return m;
			}
		}
		
		return null;
	}
	
	public void loadMenus() {
		module.printLog("Loading menus..");
		File menusDir = new File(module.MENUS_DIR);
		
		Map<String, Map> menusMaps = new Hashtable<String, Map>();
		File[] files = menusDir.listFiles();
		for(File file: files) {
			if(file.getName().equals("menu_example.yml")) {
				continue;
			}
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				menusMaps.put(file.getName(), (Map) yaml.load(fileInputStream));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		module.printLog("Menus loades: " + menusMaps.size());
		module.printLog("Parsing menus..");
		loadedMenus = new ArrayList();
		Set<String> keys = menusMaps.keySet();
		for(String key: keys) {
			loadedMenus.add(new Menu(menusMaps.get(key), key));
		}
		module.printLog("All menus have been loaded and parsed!");
	}
	
	public void dumpMenus() throws IOException {
		module.printLog("Dumpting menus..");
		
		File file;
		FileWriter writer;
		for(Menu menu: loadedMenus) {
			file = new File(ChestCommandsExtender.DUMP_DIR + "/" + menu.getMenuFileName());
			if(!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			writer = new FileWriter(file);
			yaml.dump(menu.createNativeMenuMap(), writer);
		}
		
		module.printLog("All menus has been dumped!");
	}
	
	public void dumpMenu(String menuFileName) throws IOException {
		module.printLog("Dumpting menu: " + menuFileName);
		
		Menu menu = getMenu(menuFileName);
		if(menu != null) {
			File file = new File(ChestCommandsExtender.DUMP_DIR + "/" + menu.getMenuFileName());
			if(!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			yaml.dump(menu.createNativeMenuMap(), writer);
		} else {
			module.printLog("Menu " + menuFileName + " not found!");
			return;
		}
		
		
		module.printLog("Menu dumped!");
	}
	
	public void reloadChestCommandsPlugin() {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chestcommands reload");
	}
}
