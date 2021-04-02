package org.ultragore.everything.modules.WorldEditExtender;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.ultragore.everything.modules.WorldEditExtender.managers.ClipboardManager;
import org.ultragore.everything.modules.WorldEditExtender.managers.CooldownManager;
import org.ultragore.everything.modules.WorldEditExtender.managers.RestrictionManager;
import org.ultragore.everything.modules.WorldEditExtender.managers.WECommandManager;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

import com.sk89q.worldedit.EditSession.Stage;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.extent.inventory.BlockBagExtent;
import com.sk89q.worldedit.extent.reorder.ChunkBatchingExtent;
import com.sk89q.worldedit.extent.validation.BlockChangeLimiter;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import net.md_5.bungee.api.ChatColor;

public class WorldEditExtender extends Module implements CommandExecutor {

	public static final String CONFIG_FILE = "worldedit_extender.yml";
	public static final String CONFIG_FILE_CLASSPATH = "org/ultragore/everything/modules/WorldEditExtender/config.yml";
	
	private ClipboardManager clipboardManager;
	private RestrictionManager restrictionManager;
	private WECommandManager weCommandManager;
	private CooldownManager cooldownManager;
	
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		DottedMap config = implementConfig(CONFIG_FILE_CLASSPATH, CONFIG_FILE, false);
		
		printLog("Creating managers..");
		clipboardManager = new ClipboardManager();
		restrictionManager = new RestrictionManager(config.getMap("group_restrictions"), config.getMap("default_restrictions"));
		cooldownManager = new CooldownManager(plugin);
		weCommandManager = new WECommandManager(restrictionManager, clipboardManager, cooldownManager, new DottedMap(config.getMap("messages")));
		
		printLog("Registering events..");
		Bukkit.getPluginManager().registerEvents(clipboardManager, plugin);
		Bukkit.getPluginManager().registerEvents(weCommandManager, plugin);
		
		printLog("Registering command..");
		plugin.getCommand(moduleCommand).setExecutor(this);
		
		this.active = true;
		Logger.moduleEnabled(this);
	}
	
	@Override
	public void disableModule() {
		Logger.disablingModule(this);
		
		printLog("Unregistering events..");
		HandlerList.unregisterAll(weCommandManager);
		HandlerList.unregisterAll(clipboardManager);
		
		printLog("Removing cooldowns tasks..");
		cooldownManager.removeCooldowns();
		
		this.active = false;
		Logger.moduleDisabled(this);
	}
	
	@Override
	public boolean reloadConfiguration() {
		printLog("Unregistering events..");
		HandlerList.unregisterAll(weCommandManager);
		HandlerList.unregisterAll(clipboardManager);
		
		printLog("Removing cooldowns tasks..");
		cooldownManager.removeCooldowns();
		
		
		DottedMap config = implementConfig(CONFIG_FILE_CLASSPATH, CONFIG_FILE, false);
		
		printLog("Creating managers..");
		clipboardManager = new ClipboardManager();
		restrictionManager = new RestrictionManager(config.getMap("group_restrictions"), config.getMap("default_restrictions"));
		cooldownManager = new CooldownManager(plugin);
		weCommandManager = new WECommandManager(restrictionManager, clipboardManager, cooldownManager, new DottedMap(config.getMap("messages")));
		
		printLog("Registering events..");
		Bukkit.getPluginManager().registerEvents(clipboardManager, plugin);
		Bukkit.getPluginManager().registerEvents(weCommandManager, plugin);
		
		return true;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof ConsoleCommandSender) {
			if(args.length == 0 || args.length > 1) {
				return false;
			} else if(args[0].equals("reload")) {
				printLog("Reloading..");
				reloadConfiguration();
				printLog("Reloaded!");
				return true;
			} else {
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "It seems that you are not console.., so GFYS");
			return true;
		}
	}

}
