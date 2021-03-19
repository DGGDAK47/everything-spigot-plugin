package org.ultragore.everything.modules.OPRegions;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

public class OPRegions extends Module {
	
	public static final String CONFIG_FILE = "opregions.yml";
	public static final String CONFIG_FILE_CLASSPATH = "org/ultragore/everything/modules/OPRegions/config.yml";
	
	private DottedMap config;
	private EventsManager eventsManager;
	private CommandsManager comnmandsManager;
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		config = implementConfig(CONFIG_FILE_CLASSPATH, CONFIG_FILE, false);
		
		printLog("Initializing managers and registering event listeners");
		eventsManager = new EventsManager(config);
		comnmandsManager = new CommandsManager(this);
		
		Bukkit.getPluginManager().registerEvents(eventsManager, plugin);
		this.plugin.getCommand(moduleCommand).setExecutor(comnmandsManager);
		
		this.active = true;
		Logger.moduleEnabled(this);
	}

	@Override
	public void disableModule() {
		Logger.disablingModule(this);
		
		printLog("Unregistering event listeners");
		HandlerList.unregisterAll(eventsManager);
		
		this.active = false;
		Logger.moduleDisabled(this);
	}
	
	@Override
	public boolean reloadConfiguration() {
		printLog("Reloading config. Stopping events manager..");
		HandlerList.unregisterAll(eventsManager);
		
		DottedMap newMap = implementConfig(CONFIG_FILE_CLASSPATH, CONFIG_FILE, false);
		if(newMap == null) {
			printLog("Something wrong happened while implementing config. Check stacktrace up.");
			printLog("Running on previous config");
		} else {
			config.setMap(newMap.getMap());
			printLog("Config has been updated! Running events manager.");
		}
		
		Bukkit.getPluginManager().registerEvents(eventsManager, plugin);
		
		return newMap != null;
	}
}
