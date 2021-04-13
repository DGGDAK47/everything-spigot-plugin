package org.ultragore.everything.modules.Torch;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.ultragore.everything.modules.Torch.managers.CooldownManager;
import org.ultragore.everything.modules.Torch.managers.ItemsManager;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

public class Torch extends Module implements CommandExecutor {
	public static final String GIVE_COMMAND_PERM = "everything.torch.give";
	public static final String RELOAD_COMMAND_PERM = "everything.torch.reload";
	public static final String LIST_COMMAND_PERM = "everything.torch.list";
	
	private CooldownManager cooldownManager;
	private ItemsManager itemsManager;
	private DottedMap messages;
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		DottedMap config = implementConfig("org/ultragore/everything/modules/Torch/config.yml", "torch.yml", false);
		messages = new DottedMap(config.getMap("messages"));
		
		printLog("Creating managers..");
		cooldownManager = new CooldownManager(plugin, config.getMap("group_cooldowns"));
		itemsManager = new ItemsManager(plugin, cooldownManager, config.getMap("usable_items"), messages);
		
		printLog("Registering events and commands..");
		Bukkit.getPluginManager().registerEvents(itemsManager, plugin);
		plugin.getCommand("torch").setExecutor(this);
		
		Logger.moduleEnabled(this);
	}

	@Override
	public void disableModule() {
		Logger.disablingModule(this);
		
		printLog("Unregistering events..");
		HandlerList.unregisterAll(itemsManager);
		
		Logger.moduleDisabled(this);
	}
	
	@Override
	public boolean reloadConfiguration() {
		DottedMap config = implementConfig("org/ultragore/everything/modules/Torch/config.yml", "torch.yml", false);
		messages = new DottedMap(config.getMap("messages"));
		
		printLog("Unregistering events..");
		HandlerList.unregisterAll(itemsManager);
		
		printLog("Creating managers..");
		cooldownManager = new CooldownManager(plugin, config.getMap("group_cooldowns"));
		itemsManager = new ItemsManager(plugin, cooldownManager, config.getMap("usable_items"), messages);
		
		printLog("Registering events..");
		Bukkit.getPluginManager().registerEvents(itemsManager, plugin);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length == 0) {
			return false;
		} else if(args[0].equals("reload") && args.length == 1) {
			
			if(sender instanceof Player && !sender.hasPermission(RELOAD_COMMAND_PERM)) {
				sender.sendMessage(messages.getString("permission_denied"));
				return true;
			}
			
			if(args.length == 1) {
				sender.sendMessage("Reloading..");
				reloadConfiguration();
				sender.sendMessage("Reloaded!");
				return true;
			}
			
			
		} else if(args[0].equals("give")) {
			
			if(sender instanceof Player && !sender.hasPermission(GIVE_COMMAND_PERM)) {
				sender.sendMessage(messages.getString("permission_denied"));
				return true;
			}
			
			if(args.length >= 3 && args.length <= 4) {
				
				Player p = Bukkit.getPlayer(args[1]);
				if(p == null) {
					sender.sendMessage("§cNo such player");
					return true;
				}
				
				int amount = 1;
				if(args.length == 4) {
					try {
						amount = Integer.parseInt(args[3]);						
					} catch(NumberFormatException e) {
						sender.sendMessage("§cInvalid amount format");
						return true;
					}
				}
				
				int code = itemsManager.giveItem(p, args[2], amount);
				if(code == 1) {
					sender.sendMessage(String.format("§2%s %s has been given to %s", args[2], amount, p.getName()));
				} else if(code == 0) {
					sender.sendMessage("§cNo such label.");
				}
				
			} else {
				return false;
			}
			
		} else if(args[0].equals("list") && args.length == 1) {
			
			if(sender instanceof Player && !sender.hasPermission(LIST_COMMAND_PERM)) {
				sender.sendMessage(messages.getString("permission_denied"));
				return true;
			}
			
			String labels = "";
			for(String l: itemsManager.getLabels()) {
				labels += l + ", ";
			}
			
			labels = labels.substring(0, labels.length() - 2);
			
			sender.sendMessage("Loaded items labels:");
			sender.sendMessage(labels);
		} else {
			return false;
		}
		
		return true;
	}

}
