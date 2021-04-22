package org.ultragore.everything.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

public class PermsUtils {
	
	public static LuckPerms getLuckPermsPluginApi() {
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
		    return provider.getProvider();
		} else {
			return null;
		}
	}
	
	public static List<String> getPlayerGroups(Player player) {
		User user = getLuckPermsPluginApi().getUserManager().getUser(player.getUniqueId());
		if(user == null) {
			return new ArrayList<String>();
		}
		
		List<String> toReturn = new ArrayList<String>();
		
		Collection<Node> nodes = user.getNodes();
		for(Node node: nodes) {
			if(node instanceof InheritanceNode) {
				toReturn.add(((InheritanceNode) node).getGroupName());
			}
		}
		
		return toReturn;
	}
	
	public static void setPlayerGroups(Player player, List<String> groups) {
		LuckPerms luckPerms = getLuckPermsPluginApi();
		User user = luckPerms.getUserManager().getUser(player.getUniqueId());
		if(user == null) {
			return;
		}
		
		NodeMap nodeMap = user.data();
		nodeMap.clear();
		Node node;
		for(String groupName: groups) {
			node = Node.builder("group." + groupName).build();
			nodeMap.add(node);
		}
		
		luckPerms.getUserManager().saveUser(user);
	}
	
	public static boolean hasPermissionGroup(Player player, String groupName) {
		return player.hasPermission("group." + groupName);
	}
}
