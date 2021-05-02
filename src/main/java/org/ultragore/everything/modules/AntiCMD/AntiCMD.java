package org.ultragore.everything.modules.AntiCMD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

public class AntiCMD extends Module implements Listener, CommandExecutor {
	public static final String BYPASS_PERM_NODE = "anticmd.bypass";
	public static final String RELOAD_PERM_NODE = "anticmd.reload";
	
	
	private List<ExcludedCommand> excludedCommands;
	private String defaultCnacelMessage;
	
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		DottedMap config;
		try {
			config = implementConfig("org/ultragore/everything/modules/AntiCMD/config.yml", "anticmd.yml", false);
		} catch(Exception e) {
			printLog("Error occured while implementing config file. Check stacktrace below for more information");
			e.printStackTrace();
			return;
		}
		
		printLog("Reading config..");
		defaultCnacelMessage = config.getString("default_cancel_message");
		
		excludedCommands = new ArrayList<ExcludedCommand>();
		
		for(Object cmdMap: config.getList("excluded_commands")) {
			excludedCommands.add(new ExcludedCommand((Map<String, String>) cmdMap));
		}
		
		printLog("Registering commands and events..");
		plugin.getCommand("anticmd").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		this.active = true;
		Logger.moduleEnabled(this);
	}
	
	@Override
	public void disableModule() {
		this.active = false;
		printLog("Unregistering events..");
		HandlerList.unregisterAll(this);
		Logger.moduleDisabled(this);
	}
	
	@Override
	public boolean reloadConfiguration() {
		
		DottedMap config;
		try {
			config = implementConfig("org/ultragore/everything/modules/AntiCMD/config.yml", "anticmd.yml", false);
		} catch(Exception e) {
			printLog("Error occured while implementing config file. Check stacktrace below for more information");
			e.printStackTrace();
			return false;
		}
		
		printLog("Reading config..");
		defaultCnacelMessage = config.getString("default_cancel_message");
		
		excludedCommands = new ArrayList();
		
		for(Object cmdMap: config.getList("excluded_commands")) {
			excludedCommands.add(new ExcludedCommand((Map<String, String>) cmdMap));
		}
		
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0 || !args[0].equals("reload")) {
			return false;
		}
		
		if(sender instanceof Player && !((Player) sender).hasPermission(RELOAD_PERM_NODE)) {
			sender.sendMessage("§cYou have no permissions to do this command!");
			return true;
		}
		
		sender.sendMessage("[AntiCMD] Reloading module..");
		try {
			reloadConfiguration();
		} catch(Exception e) {
			sender.sendMessage("§cError occured while reloading module. Check console.");
			e.printStackTrace();
			return true;
		}
		sender.sendMessage("[AntiCMD] Reloaded!");
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(player == null || player.hasPermission(BYPASS_PERM_NODE)) {
			return;
		}
		
		String msg = event.getMessage();
		for(ExcludedCommand ecommand: excludedCommands) {
			if(ecommand.command.startsWith("^")) {
				if(!msg.startsWith(ecommand.command.substring(1))) {
					continue;					
				}
			} else if(!msg.equals(ecommand.command)) {
				continue;
			}
			
			
			
			event.setCancelled(true);
			
			if(ecommand.cancelMessage != null) {
				if(!ecommand.cancelMessage.equals("NONE")) {
					player.sendMessage(ecommand.cancelMessage);						
				}
			} else {
				player.sendMessage(defaultCnacelMessage);
			}
			
			if(ecommand.issueInsteadCommand != null) {
				player.performCommand(ecommand.issueInsteadCommand);
			}
			
			break;
		}
		
	}
}
