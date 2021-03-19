package org.ultragore.everything.types;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.ultragore.everything.Everything;
import org.ultragore.everything.utils.Configs;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;
import org.yaml.snakeyaml.Yaml;

public abstract class Module {
	
	// Variables
	protected Everything plugin;
	protected String moduleName;
	protected String moduleCommand = null;
	protected boolean active = false;
	
	// Getters
	public Everything getPlugin() {
		return plugin;
	}
	public String getModuleName() {
		return moduleName;
	}
	public String getModuleCommand() {
		return moduleCommand;
	}
	public boolean isActive() {
		return this.active;
	}
	
	
	public void printLog(String text) {
		Logger.moduleLog(this, text);
	}
	
	
	/**
	 * Checks for config file; if not exists - creates it. Then retrieves config and
	 * parses it, returning DottedMap instance, and logging each step.
	 * 
	 * @param classPath - path to config file in plugin jar
	 * @param configFile - config file, with ext, that will be created in configs directory
	 * @return DottedMap
	 */
	public DottedMap implementConfig(String classPath, String configFile, boolean stopServer) {
		
		if(Configs.configExists(configFile)) {
			printLog(String.format("Config %s found", configFile));
		} else {
			printLog(String.format("Config %s not found. Creating it...", configFile));
			try {
				int code = Configs.extractConfigFile(classPath, configFile);
				if(code == 0) {
					printLog(String.format("Config %s has been created.", configFile));
				} else {
					printLog(String.format("Couldn't create config %s. Check logs or config file.", configFile));
				}
			} catch(Exception e) {
				printLog("Error occured while creating config file" + configFile + ". Check stacktrace down for more information.");
				e.printStackTrace();
				if(stopServer) {
					Logger.serverStop(this.moduleName);
					this.plugin.getServer().shutdown();
					return null;
				}
				
				return null;
			}
		}
		
		// Retrieving config
		printLog(String.format("Retrieving config file %s and trying to parse it", configFile));
		
		Map<String, Object> parsedMap = null;
		try {
			File file = new File("plugins/Everything/" + configFile);
			FileInputStream stream = new FileInputStream(file);
			
			parsedMap = new Yaml().load(stream);
			
			stream.close();
		} catch(Exception e) {
			printLog("Error occured while retrieving/parsing config file " + configFile + ". Check stacktrace down for more information.");
			e.printStackTrace();
			if(stopServer) {
				Logger.serverStop(this.moduleName);
				this.plugin.getServer().shutdown();
				return null;
			}
			
			return null;
		}
		
		if(parsedMap != null) {
			return new DottedMap(parsedMap);
		} else {
			return null;
		}
	}
	
	
	// State
	/**
	 * Enables module in the first time. Function must initialize all types,
	 * configs, register listeners and etc. Must be called once.
	 */
	public abstract void enableModule();
	
	
	
	/**
	 * Disables module. Function must remove module listeners and other
	 * registered things and record data in db's or configs if it needs.
	 */
	public abstract void disableModule();
	
	
	
	/**
	 * Tells module to reload confiurations if it implements its.
	 */
	public boolean reloadConfiguration() {
		printLog("Reload configuration is not defined for this module.");
		return true;
	}
}
