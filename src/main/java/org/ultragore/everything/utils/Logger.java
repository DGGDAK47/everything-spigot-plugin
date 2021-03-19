package org.ultragore.everything.utils;

import org.bukkit.command.CommandSender;
import org.ultragore.everything.types.Module;

import net.md_5.bungee.api.ChatColor;

public class Logger {
	
	public static final String PREFIX = "[Everything]";
	
	// Decorations
	public static void printLine() {
		System.out.println(PREFIX + " ==================================");
	}
	
	// Core logging
	public static void printCoreLog(String serviceName, String text) {
		System.out.println(PREFIX + " " + serviceName + ": " + text);
	}
	
	// General logging
	public static void serverStop(String initiatorName) {
		System.out.println(PREFIX + " " + initiatorName + ": " + "Stopping server.");
	}
	
	// # Modules logging
	public static void moduleLog(Module module, String text) {
		System.out.println(PREFIX + " " + module.getModuleName() + ": " + text);
	}
	
	// 1) Module state
	public static void enablingModule(Module module) {
		moduleLog(module, "Enabling..");
	}
	public static void moduleEnabled(Module module) {
		moduleLog(module, "Enabled!");
	}
	
	public static void disablingModule(Module module) {
		moduleLog(module, "Disabling..");
	}
	public static void moduleDisabled(Module module) {
		moduleLog(module, "Disabled");
	}

	// Commands logging
	public static void commandMsg(Module module, CommandSender sender, String text) {
		sender.sendMessage(module.getModuleName() + ": " + text);
	}
	
	public static void commandMsgWarn(Module module, CommandSender sender, String text) {
		sender.sendMessage(ChatColor.YELLOW + module.getModuleName() + ": " + text);
	}
	
	public static void commandMsgError(Module module, CommandSender sender, String text) {
		sender.sendMessage(ChatColor.RED + module.getModuleName() + ": " + text);
	}
}