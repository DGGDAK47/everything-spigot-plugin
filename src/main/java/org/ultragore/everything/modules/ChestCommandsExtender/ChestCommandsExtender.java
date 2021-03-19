package org.ultragore.everything.modules.ChestCommandsExtender;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.Configs;
import org.ultragore.everything.utils.Logger;

public class ChestCommandsExtender extends Module implements CommandExecutor{

	public static final String MENUS_DIR = "plugins/Everything/ChestCommandsExtender";
	public static final String DUMP_DIR = "plugins/ChestCommands/menu";
	public static final String REL_MENUS_DIR = "ChestCommandsExtender";
	public static final String MENU_EXAMPLE_FILE = "menu_example.yml";
	public static final String MENU_EXAMPLE_FILE_CLASSPATH = "org/ultragore/everything/modules/ChestCommandsExtender/menu_example.yml";
	
	private MenuManager menuManager;
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		File file = new File(MENUS_DIR);
		printLog("Checking for module directory");
		if(file.exists()) {
			printLog("Module directory found!");
		} else {
			file.mkdir();
			printLog("Module directory created!");
		}
		
		File exampleFile = new File(MENUS_DIR + "/" + MENU_EXAMPLE_FILE);
		if(exampleFile.exists()) {
			printLog("Menu example file found.");
		} else {
			printLog("Creating menu example file..");
			try {
				int exitCode = Configs.extractConfigFile(MENU_EXAMPLE_FILE_CLASSPATH, REL_MENUS_DIR + "/" + MENU_EXAMPLE_FILE);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		
		plugin.getCommand("cce").setExecutor(this);		
		menuManager = new MenuManager(this);
//		menuManager.loadMenus();
//		try {
//			menuManager.dumpMenus();
//		} catch (IOException e) {
//			printLog("Error occured while menus dumping. Check stacktrace below");
//			e.printStackTrace();
//			return;
//		}
//		menuManager.reloadChestCommandsPlugin();
		this.active = true;
		Logger.moduleEnabled(this);
	}

	@Override
	public void disableModule() {
		this.active = false;
		Logger.moduleDisabled(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof ConsoleCommandSender) {
			if(args.length > 0 && args[0].equals("refresh")) {
				Logger.commandMsg(this, sender, "reloading module and ChestCommands plugin..");
				
				menuManager.loadMenus();
				try {
					menuManager.dumpMenus();
				} catch (IOException e) {
					e.printStackTrace();
					return true;
				}
				menuManager.reloadChestCommandsPlugin();
				
				
				Logger.commandMsg(this, sender, "Done!");
			} else {
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Only console is allowed to execute this command.");
		}
		return true;
	}
}
