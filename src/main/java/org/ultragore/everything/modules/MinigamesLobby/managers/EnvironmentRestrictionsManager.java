package org.ultragore.everything.modules.MinigamesLobby.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.ultragore.everything.modules.MinigamesLobby.MinigamesLobby;
import org.ultragore.everything.modules.MinigamesLobby.events.LobbyJoinEvent;
import org.ultragore.everything.modules.MinigamesLobby.events.LobbyLeaveEvent;
import org.ultragore.everything.modules.MinigamesLobby.types.Lobby;
import org.ultragore.everything.modules.MinigamesLobby.types.TimeHolder;

import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;

public class EnvironmentRestrictionsManager implements Listener {
	
	private Plugin plugin;
	private LobbyManager lobbyManager;
	private List<TimeHolder> timeHolders = new ArrayList<TimeHolder>();
	
	
	public EnvironmentRestrictionsManager(Plugin plugin, LobbyManager lobbyManager) {
		this.plugin = plugin;
		this.lobbyManager = lobbyManager;
		
		TimeHolder th;
		for(Lobby lobby: lobbyManager.getLobbies()) {
			if(lobby.worldConstantTime != null) {
				th = new TimeHolder(lobby.getWorld(), lobby.worldConstantTime);
				th.task = Bukkit.getScheduler().runTaskTimer(plugin, th, 0, 20);
				timeHolders.add(th);
			}
		}
	}
	
	
	public void removeTimeHolders() {
		for(TimeHolder th: timeHolders) {
			th.task.cancel();
		}
		timeHolders.clear();
	}
	
	
	@EventHandler
	public void onLobbyJoin(LobbyJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM)) {
			return;
		}
		
		if(lobbyManager.hasLobby(e.getPlayer())) {
			Player player = e.getPlayer();
			Lobby lobby = lobbyManager.getLobby(player);
			if(!lobby.lobbyBox.contains(player.getLocation().toVector()))
			{
				player.teleport(lobby.spawnLocation);
			}
		}
	}
		
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Lobby lobby = lobbyManager.getLobby(e.getPlayer());
		if(lobby == null || e.getPlayer() != null && e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM)) {
			return;
		}
		
		Block block = e.getClickedBlock();
		if(block == null || !lobby.isInteractAllowed(block.getType())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		Lobby lobby = lobbyManager.getLobby(e.getWorld().getName());
		if(lobby != null) {
			World world = lobby.getWorld();
			if(!world.hasStorm()) {
				e.setCancelled(true);				
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(lobbyManager.hasLobby(e.getPlayer()) &&
		   !e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(lobbyManager.hasLobby(e.getBlock().getWorld().getName()) &&
		   !e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if(lobbyManager.hasLobby(e.getPlayer()) &&
		   !e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if(lobbyManager.hasLobby(e.getPlayer()) &&
		   !e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemPickUp(EntityPickupItemEvent e) {
		if(lobbyManager.hasLobby(e.getEntity().getWorld().getName()) &&
		   !e.getEntity().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		Lobby lobby = lobbyManager.getLobby(e.getEntity().getWorld().getName());
		if(lobby != null) {
			if(e.getEntity().getLocation().getY() < 0) {
				e.getEntity().teleport(lobby.spawnLocation);
			}
			
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e.getEntity();
				le.setHealth(20.0);
			}
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(lobbyManager.hasLobby(e.getWhoClicked().getWorld().getName()) &&
		   !e.getWhoClicked().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if(lobbyManager.hasLobby(e.getEntity().getWorld().getName())) {
			if(e instanceof LivingEntity) {
				e.setFoodLevel(20);
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMobSpawn(EntitySpawnEvent e) {
		if(lobbyManager.hasLobby(e.getEntity().getWorld().getName()) &&
		   !(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFrameInteract(PlayerInteractAtEntityEvent e) {
		if(EntityType.ITEM_FRAME == e.getRightClicked().getType() &&
		   lobbyManager.hasLobby(e.getRightClicked().getWorld().getName()) &&
		   !e.getPlayer().hasPermission(MinigamesLobby.BYPASS_PERM))
		{
			e.setCancelled(true);
		}
	}
}