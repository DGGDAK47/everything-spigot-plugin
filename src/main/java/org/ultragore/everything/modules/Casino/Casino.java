package org.ultragore.everything.modules.Casino;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.ultragore.everything.modules.Casino.managers.MachineManager;
import org.ultragore.everything.modules.Casino.managers.RollManager;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

import net.milkbowl.vault.economy.Economy;

public class Casino extends Module implements CommandExecutor{

	private MachineManager machineManager;
	private RollManager rollManager;
	private Economy eco;
	
	public MachineManager getMachineManager() {
		return this.machineManager;
	}
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		DottedMap config;
		
		try {
			printLog("Fetching eco..");
			
			config = implementConfig("org/ultragore/everything/modules/Casino/config.yml", "casino.yml", false);
			
			eco = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
			
			printLog("Creating managers..");
			machineManager = new MachineManager( (Map<String, Map>) config.getMap("machines") );
			rollManager = new RollManager(this, eco, Sound.valueOf(config.getString("scroll_sound")), Sound.valueOf(config.getString("lose_sound")), Sound.valueOf(config.getString("reward_sound")), Sound.valueOf(config.getString("line_stop_sound")), new DottedMap(config.getMap("messages")));
			
			printLog("Registering events and commands..");
			Bukkit.getPluginManager().registerEvents(rollManager, plugin);
			plugin.getCommand("casino").setExecutor(this);
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		this.active = true;
		Logger.moduleEnabled(this);
	}

	@Override
	public void disableModule() {
		Logger.disablingModule(this);
		this.active = false;
		HandlerList.unregisterAll(rollManager);
		rollManager.deleteActiveRolls();
		Logger.moduleDisabled(this);
	}
	
	@Override
	public boolean reloadConfiguration() {
		
		printLog("Disabling RollManager..");
		HandlerList.unregisterAll(rollManager);
		rollManager.deleteActiveRolls();
		
		DottedMap config = implementConfig("org/ultragore/everything/modules/Casino/config.yml", "casino.yml", false);
		
		printLog("Initializing managers..");
		machineManager = new MachineManager( (Map<String, Map>) config.getMap("machines") );
		rollManager = new RollManager(this, eco, Sound.valueOf(config.getString("scroll_sound")), Sound.valueOf(config.getString("lose_sound")), Sound.valueOf(config.getString("reward_sound")), Sound.valueOf(config.getString("line_stop_sound")), new DottedMap(config.getMap("messages")));
		
		printLog("Registering events..");
		Bukkit.getPluginManager().registerEvents(rollManager, plugin);
		
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage("§cConsole, pls..");
			return true;
		}
		
		if(args.length == 0 || (args.length > 0 && !args[0].equals("reload")) ) {
			return false;
		} else {
			printLog("Reloading..");
			reloadConfiguration();
			printLog("Reloaded!");
			return true;
		}
	}

	
	
}
