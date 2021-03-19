package org.ultragore.everything;

import org.bukkit.plugin.java.JavaPlugin;
import org.ultragore.everything.managers.CoreCommandsManager;
import org.ultragore.everything.managers.ModulesManager;
import org.ultragore.everything.utils.Configs;
import org.ultragore.everything.utils.Logger;

public class Everything extends JavaPlugin {
	
	private static final String SERVICE_NAME = "Main";
	private static final String CORE_COMMAND = "everything";
	
	private ModulesManager modulesManager;
	private CoreCommandsManager commandsManager;
	
	public String getPluginFileName() {
		return this.getFile().getName();
	}
	
	private void printLog(String text) {
		Logger.printCoreLog(SERVICE_NAME, text);
	}
	
	public void implementConfigsDirectory() {
		printLog("Checking for configs folder..");
		if(Configs.configsDirExists()) {
			printLog("Configs folder found!");
		} else {
			printLog("Configs folder not found. Creating..");
			try {
				Configs.createConfigsDir();
				printLog("Configs folder has been created!");
			} catch(Exception e) {
				printLog("Error occured while creating configs folder. Check stacktrace down for more information.");
				e.printStackTrace();
				Logger.serverStop(SERVICE_NAME);
				this.getServer().shutdown();
			}
		}
	}
	
	@Override
	public void onEnable() {
		Logger.printLine();
		implementConfigsDirectory();
		
		printLog("Starting managers..");
		modulesManager = new ModulesManager(this);
		modulesManager.enableModules();
		commandsManager = new CoreCommandsManager();
		
		printLog("Registering Core commands");
		this.getCommand(CORE_COMMAND).setExecutor(commandsManager);
		
		Logger.printLine();
	}
	
	@Override
	public void onDisable() {
		Logger.printLine();
		modulesManager.disableModules();
		Logger.printLine();
	}
}
