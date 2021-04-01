package org.ultragore.everything.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.luckperms.api.LuckPerms;

public class PermsUtils {
	
	public static LuckPerms getLuckPermsPluginApi() {
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
		    return provider.getProvider();
		} else {
			return null;
		}
	}
	
	
	public static boolean hasPermissionGroup(Player player, String groupName) {
		return player.hasPermission("group." + groupName);
	}
}
