package org.ultragore.everything.modules.MinigamesAdapter.managers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ultragore.everything.modules.MinigamesAdapter.MinigamesAdapter;
import org.ultragore.everything.modules.MinigamesAdapter.types.Lobby;
import org.ultragore.everything.utils.DottedMap;

public class CommandsManager implements CommandExecutor, Listener {
	
	public final static String RELOAD_COMMAND_PERM = "everything.mlobby.cmd.reload";
	public final static String LEAVE_COMMAND_PERM = "everything.mlobby.cmd.leave";
	public final static String JOIN_COMMAND_PERM = "everything.mlobby.cmd.join";
	
	private MinigamesAdapter module;
	private LobbyManager lobbyManager;
	private DottedMap messages;
	
	
	public CommandsManager(MinigamesAdapter module, LobbyManager lobbyManager, Map<String, Object> messages) {
		this.module = module;
		this.lobbyManager = lobbyManager;
		this.messages = new DottedMap(messages);
	}
	public void configUpdate(LobbyManager lobbyManager, Map<String, Object> messages) {
		this.lobbyManager = lobbyManager;
		this.messages = new DottedMap(messages);
	}
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Lobby lobby = lobbyManager.getLobby(e.getPlayer());
		if(lobby != null &&
		   !e.getPlayer().hasPermission(MinigamesAdapter.BYPASS_PERM) &&
		   !lobby.isCommandAllowed(e.getMessage()))
		{
			e.getPlayer().sendMessage(messages.getString("restricted_command"));
			e.setCancelled(true);
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length == 0) {
			return false;
		} else if(args[0].equals("reload") && args.length == 1) {
			if(sender.hasPermission(RELOAD_COMMAND_PERM) || sender instanceof ConsoleCommandSender) {
				sender.sendMessage("Reloading..");
				module.reloadConfiguration();
				sender.sendMessage("Reloaded!");
				return true;
			} else {
				sender.sendMessage("§cNot enough permissions.");
				return false;
			}
		} else if(args[0].equals("join")) {
			if(args.length == 1 || args.length > 2) {
				return false;
			} else if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage("For players only.");
				return true;
			} else if(!sender.hasPermission(JOIN_COMMAND_PERM)) {
				sender.sendMessage("§cNot enough permissions.");
				return true;
			}
			
			Lobby lobby = lobbyManager.getLobby(args[1]);
			if(lobby != null) {
				((Player) sender).teleport(lobby.spawnLocation);
			} else {
				sender.sendMessage("§cNo such lobby.");
			}
			
			return true;
		} else if(args[0].equals("leave") && args.length == 1) {
			if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage("For players only.");
				return true;
			} else if(!sender.hasPermission(LEAVE_COMMAND_PERM)) {
				sender.sendMessage("§cNot enough permissions.");
				return true;
			}
			
			Lobby lobby = lobbyManager.getLobby((Player) sender);
			if(lobby != null) {
				((Player) sender).teleport(lobbyManager.getServerSpawnLocation());
			} else {
				sender.sendMessage("You are not in lobby.");
			}
			
			return true;
		} else {
			return false;
		}
	}
	
}