package org.ultragore.everything.modules.MinigamesLobby;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.ultragore.everything.modules.MinigamesLobby.managers.ChatManager;
import org.ultragore.everything.modules.MinigamesLobby.managers.CommandsManager;
import org.ultragore.everything.modules.MinigamesLobby.managers.EnvironmentRestrictionsManager;
import org.ultragore.everything.modules.MinigamesLobby.managers.LobbyItemsManager;
import org.ultragore.everything.modules.MinigamesLobby.managers.LobbyManager;
import org.ultragore.everything.modules.MinigamesLobby.managers.PlayerSnapshotManager;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.utils.DottedMap;
import org.ultragore.everything.utils.Logger;

public class MinigamesLobby extends Module {
	
	public final static String BYPASS_PERM = "everything.mlobby.bypass";
	
	private LobbyManager lobbyManager;
	private EnvironmentRestrictionsManager environmentRestrictionsManager;
	private PlayerSnapshotManager playerSnapshotManager;
	private CommandsManager commandsManager;
	private LobbyItemsManager lobbyItemsManager;
	private ChatManager chatManager;
	
	@Override
	public void enableModule() {
		Logger.enablingModule(this);
		
		DottedMap config = implementConfig("org/ultragore/everything/modules/MinigamesLobby/config.yml", "minigames_lobby.yml", false);
		
		printLog("Creating managers..");
		lobbyManager = new LobbyManager(Sound.valueOf(config.getString("lobby_join_sound")), config.getMap("lobbies"), config.getString("server_spawn_location"));
		environmentRestrictionsManager = new EnvironmentRestrictionsManager(plugin, lobbyManager);
		playerSnapshotManager = new PlayerSnapshotManager();
		commandsManager = new CommandsManager(this, lobbyManager, config.getMap("messages"));
		lobbyItemsManager = new LobbyItemsManager(lobbyManager);
		chatManager = new ChatManager(lobbyManager);
		
		printLog("Registering listeners..");
		Bukkit.getPluginManager().registerEvents(lobbyManager, plugin);
		Bukkit.getPluginManager().registerEvents(environmentRestrictionsManager, plugin);
		Bukkit.getPluginManager().registerEvents(playerSnapshotManager, plugin);
		Bukkit.getPluginManager().registerEvents(commandsManager, plugin);
		Bukkit.getPluginManager().registerEvents(lobbyItemsManager, plugin);
		Bukkit.getPluginManager().registerEvents(chatManager, plugin);
		
		printLog("Registering commands..");
		plugin.getCommand(moduleCommand).setExecutor(commandsManager);
		
		this.active = true;
		Logger.moduleEnabled(this);
	}
	
	@Override
	public void disableModule() {
		Logger.disablingModule(this);
		
		printLog("Unregistering listeners..");
		HandlerList.unregisterAll(commandsManager);
		HandlerList.unregisterAll(lobbyManager);
		HandlerList.unregisterAll(lobbyItemsManager);
		HandlerList.unregisterAll(environmentRestrictionsManager);
		HandlerList.unregisterAll(playerSnapshotManager);
		HandlerList.unregisterAll(chatManager);
		
		printLog("Cleaning managers data..");
		lobbyManager.teleportParticipantsToSpawn();
		environmentRestrictionsManager.removeTimeHolders();
		playerSnapshotManager.applySnapshots();
		
		this.active = false;
		Logger.moduleDisabled(this);
	}

	@Override
	public boolean reloadConfiguration() {
		
		DottedMap config = implementConfig("org/ultragore/everything/modules/MinigamesLobby/config.yml", "minigames_lobby.yml", false);
		
		printLog("Unregistering old listeners..");
		HandlerList.unregisterAll(lobbyManager);
		HandlerList.unregisterAll(lobbyItemsManager);
		HandlerList.unregisterAll(environmentRestrictionsManager);
		HandlerList.unregisterAll(playerSnapshotManager);
		HandlerList.unregisterAll(chatManager);
		
		printLog("Cleaning managers data..");
		lobbyManager.teleportParticipantsToSpawn();
		environmentRestrictionsManager.removeTimeHolders();
		playerSnapshotManager.applySnapshots();
		
		printLog("Creating new managers..");
		lobbyManager = new LobbyManager(Sound.valueOf(config.getString("lobby_join_sound")), config.getMap("lobbies"), config.getString("server_spawn_location"));
		environmentRestrictionsManager = new EnvironmentRestrictionsManager(plugin, lobbyManager);
		playerSnapshotManager = new PlayerSnapshotManager();
		lobbyItemsManager = new LobbyItemsManager(lobbyManager);
		chatManager = new ChatManager(lobbyManager);
		
		printLog("Updating CommandsManager data..");
		commandsManager.configUpdate(lobbyManager, config.getMap("messages"));
		
		printLog("Registering new listeners..");
		Bukkit.getPluginManager().registerEvents(lobbyManager, plugin);
		Bukkit.getPluginManager().registerEvents(environmentRestrictionsManager, plugin);
		Bukkit.getPluginManager().registerEvents(playerSnapshotManager, plugin);
		Bukkit.getPluginManager().registerEvents(lobbyItemsManager, plugin);
		Bukkit.getPluginManager().registerEvents(chatManager, plugin);
		
		
		return true;
	}
}