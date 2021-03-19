package org.ultragore.everything.modules.OPRegions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.ultragore.everything.utils.Logger;

import net.md_5.bungee.api.ChatColor;

public class CommandsManager implements CommandExecutor {

	private OPRegions module;
	
	public CommandsManager(OPRegions module) {
		this.module = module;
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length == 0) {
			return false;
		}
		
		if(sender instanceof ConsoleCommandSender && args.length > 0 && args[0].equals("reload")) {
			Logger.commandMsg(module, sender, "Reloading..");
			if(module.reloadConfiguration()) {
				Logger.commandMsg(module, sender, "Reloaded!");
			} else {
				Logger.commandMsgError(module, sender, "Something wrong happened while reloading configuration");
			}
			
		} else if(args.length > 0) {
			Logger.commandMsgError(module, sender, "unknown parameter - " + args[0]);
		} else {
			Logger.commandMsgError(module, sender, "only console can execute this command.");
		}
		
		return true;
	}
	
}
