package org.ultragore.everything.modules.WorldEditExtender.managers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.BoundingBox;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.EditBlocksLimitException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.EditOthersRegionException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.EditRegionlessAreaException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.IncompleteSelectException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.InvalidArgumentException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.InvalidWECommandSyntaxException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.NoClipboardStoredException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.NotAllowedArgumentException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.NotEnoughArgumentsException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.PermissionDeniedException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.PlayerUnderCooldownException;
import org.ultragore.everything.modules.WorldEditExtender.exceptions.TooManyArgumentsException;
import org.ultragore.everything.modules.WorldEditExtender.types.AffectedArea;
import org.ultragore.everything.modules.WorldEditExtender.types.ClipboardArea;
import org.ultragore.everything.modules.WorldEditExtender.types.WECommand;
import org.ultragore.everything.utils.WorldGuardUtils;
import org.ultragore.everything.utils.WorldUtils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WECommandManager implements Listener {
	
	private enum WECommandsData {
		SET("//set", null, "setCommand", "worldedit.region.set"),
		LINE("//line", null, "lineCommand", "worldedit.region.line"),
		REPLACE("//replace", Arrays.asList("//re", "//rep"), "replaceCommand", "worldedit.region.replace"),
		OVERLAY("//overlay", null, "overlayCommand", "worldedit.region.overlay"),
		WALLS("//walls", null, "wallsCommand", "worldedit.region.walls"),
		FACES("//faces", Arrays.asList("//outline"), "facesCommand", "worldedit.region.faces"),
		FOREST("//forest", null, "forestCommand", "worldedit.region.forest"),
		FLORA("//flora", null, "floraCommand", "worldedit.region.flora"),
		
		CYL("//cyl", Arrays.asList("//hcyl"), "cylCommand", "worldedit.generation.cylinder"),
		SPHERE("//sphere", Arrays.asList("//hsphere"), "sphereCommand", "worldedit.generation.sphere"),
		PYRAMID("//pyramid", Arrays.asList("//hpyramid"), "pyramidCommand", "worldedit.generation.pyramid"),
		
		COPY("//copy", null, "copyCommand", "worldedit.clipboard.copy"),
		CUT("//cut", null, "cutCommand", "worldedit.clipboard.cut"),
		PASTE("//paste", null, "pasteCommand", "worldedit.clipboard.paste"),
		CLEARCLIPBOARD("/clearclipboard", null, "clearClipboardCommand", "worldedit.clipboard.clear"),
		
		UNDO("//undo", Arrays.asList("/undo"), "undoCommand", "worldedit.history.undo.self"),
		REDO("//redo", Arrays.asList("/redo"), "redoCommand", "worldedit.history.redo.self"),
		CLEARHISTORY("//clearhistory", Arrays.asList("/clearhistory"), "clearhistoryCommand", "worldedit.history.clear");
		
		private String command;
		private List<String> alias;
		private String methodName;
		private String permission;
		
		public static WECommandsData getCommandData(String command) {
			for(WECommandsData handlerData: WECommandsData.values()) {
				if(handlerData.command.equals(command) || handlerData.alias.contains(command)) {
					return handlerData;
				}
			}
			return null;
		}
		
		public static Method getHandlerMethod(WECommandsData handlerData) {
			try {
				return WECommandManager.class.getMethod(handlerData.methodName, Player.class, String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		WECommandsData(String command, List<String> alias, String methodName, String permission) {
			this.command = command;
			this.alias = alias == null ? new ArrayList<String>() : alias;
			this.methodName = methodName;
			this.permission = permission;
		}
	};
	
	private static final String MAKE_RESTRICTIONS_CHECKS_PERM = "everything.wee.restrict";
	
	private RestrictionManager restrictionManager;
	private ClipboardManager clipboardManager;
	private CooldownManager cooldownManager;
	private WorldEditPlugin worldEditPlugin;
	
	
	public WECommandManager(RestrictionManager restrictionManager, ClipboardManager clipboardManager, CooldownManager cooldownManager) {
		this.restrictionManager = restrictionManager;
		this.clipboardManager = clipboardManager;
		this.cooldownManager = cooldownManager;
		this.worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	
	
	
	
	
	public LocalSession getSession(Player p) {
		return worldEditPlugin.getSession(p);
	}
	
	public static AffectedArea getAffectedAreaOfSelection(LocalSession ls, int expandX, int expandY, int expandZ) {
		World w;
		if(ls.getSelectionWorld() == null || ls.getSelectionWorld().getName() == null) {
			throw new IncompleteSelectException();
		} else {
			w = Bukkit.getWorld(ls.getSelectionWorld().getName());			
		}
		
		CuboidRegion cr;
		try {
			cr = ls.getSelection(ls.getSelectionWorld()).getBoundingBox();
		} catch(IncompleteRegionException e) {
			throw new IncompleteSelectException();
		}
		
		BlockVector3 pos1 = cr.getPos1();
		BlockVector3 pos2 = cr.getPos2();
		BoundingBox selectBox = new BoundingBox(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ(),
										 		pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
		selectBox = selectBox.expand(expandX, expandY, expandZ);
		return new AffectedArea(selectBox, w);
	}
	
	public static ClipboardArea getClipboardAreaOfSelection(LocalSession ls, Player executor) {
		BoundingBox selectBox = getAffectedAreaOfSelection(ls, 0, 0, 0).getAreaBox();
		return new ClipboardArea(executor,
			(int) selectBox.getWidthX(),
			(int) selectBox.getHeight(),
			(int) selectBox.getWidthZ(),
			(int) (executor.getLocation().getBlockX() - selectBox.getMinX()),
			(int) (executor.getLocation().getBlockY() - selectBox.getMinY()),
			(int) (executor.getLocation().getBlockZ() - selectBox.getMinZ())
		);
	}
	
	
	
	
	
	
	
	
	
	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage().toLowerCase();
		if(!(message.startsWith("/undo") ||
		     message.startsWith("/redo") ||
		     message.startsWith("/clearhistory") ||
		     message.startsWith("/clearclipboard")) && !message.startsWith("//")) {
			return;
		}
		
		if(event.getPlayer() == null || !event.getPlayer().hasPermission(MAKE_RESTRICTIONS_CHECKS_PERM)) {
			return;
		}
		
		String command = message.split(" ")[0];
		WECommandsData commandData = WECommandsData.getCommandData(command);
		if(commandData == null) {
			return;
		} else if(!event.getPlayer().hasPermission(commandData.permission)) {
			throw new PermissionDeniedException();
		}
		
		try {
			cooldownManager.createCooldown(event.getPlayer(), restrictionManager.getIssueCooldown(event.getPlayer()));
		} catch(PlayerUnderCooldownException exception) {
			handleCommandException(event, exception);
			return;
		}
		
		Method handler = WECommandsData.getHandlerMethod(commandData);
		try {
			handler.invoke(this, event.getPlayer(), event.getMessage());
		} catch(InvocationTargetException e) {
			handleCommandException(event, e.getTargetException());
			return;
		}catch(Exception e) {
			e.printStackTrace();
			event.setCancelled(true);
		}
	}
	
	private void handleCommandException(PlayerCommandPreprocessEvent event, Throwable exception) {
		Player p = event.getPlayer();
		
		if(exception instanceof EditBlocksLimitException) {
			p.sendMessage("limit");
			event.setCancelled(true);
		} else if(exception instanceof EditOthersRegionException) {
			p.sendMessage("edit other region");
			event.setCancelled(true);
		} else if(exception instanceof EditRegionlessAreaException) {
			p.sendMessage("edit world");
			event.setCancelled(true);
		} else if(exception instanceof IncompleteSelectException) {
			p.sendMessage("incomplete select");
		} else if(exception instanceof InvalidArgumentException) {
			p.sendMessage("invalid argument");
		} else if(exception instanceof InvalidWECommandSyntaxException) {
			p.sendMessage("invalid command");
		} else if(exception instanceof NoClipboardStoredException) {
			p.sendMessage("no clipboard");
		} else if(exception instanceof NotAllowedArgumentException) {
			p.sendMessage("not allowed argument");
			event.setCancelled(true);
		} else if(exception instanceof NotEnoughArgumentsException) {
			p.sendMessage("not enough arguments");
		} else if(exception instanceof TooManyArgumentsException) {
			p.sendMessage("too many arguments");
		} else if(exception instanceof PermissionDeniedException) {
			p.sendMessage("permission denied");
		} else if(exception instanceof PlayerUnderCooldownException) {
			p.sendMessage("wait cooldown");
			event.setCancelled(true);
		} else {
			exception.printStackTrace();
			event.setCancelled(true);
		}
	}
	
	
	
	
	
	private void checkEditRestrictions(AffectedArea area, Player executor) {
		
		// blocks limit check
		int blocksChangeLimit = restrictionManager.getBlocksChangeLimit(executor);
		if(area.getAreaVolume() > blocksChangeLimit) {
			throw new EditBlocksLimitException(blocksChangeLimit, (int) area.getAreaBox().getVolume());
		}
		
		// edit non region areas check
		if(!restrictionManager.canEditWorld(executor) &&
			WorldGuardUtils.isRegionlessAreaCrossed(area.getAreaBox(), area.getWorld(), true))
		{
			throw new EditRegionlessAreaException();
		}
		
		// edit others regions check
		if(!restrictionManager.canEditOthersRegions(executor)) {
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(executor);
			List<ProtectedRegion> crossedRegions = WorldGuardUtils.getCrossedRegions(area.getAreaBox(), area.getWorld(), true);
			for(ProtectedRegion pr: crossedRegions) {
				if(!pr.isOwner(localPlayer)) {
					throw new EditOthersRegionException();
				}
			}
		}
		
	}
	
	
	private void selectionCommandsChecks(Player executor, int expandX, int expandY, int expandZ) {
		LocalSession ls = getSession(executor);
		if(ls == null) {
			throw new IncompleteSelectException();
		}
		AffectedArea area = getAffectedAreaOfSelection(ls, expandX, expandY, expandZ);
		checkEditRestrictions(area, executor);
	}
	
	public void setCommand(Player executor, String message) {
		// //set <pattern>
		WECommand cmd = new WECommand(message, null);
		
		int argsSummary = cmd.allArgumentsSummary();
		if(argsSummary == 0) {
			throw new NotEnoughArgumentsException();
		} else if(argsSummary > 1) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 0, 0, 0);
	}
	
	public void lineCommand(Player executor, String message) {
		// //line [-h] <pattern> [thickness]
		WECommand cmd = new WECommand(message, null);
		
		int thickness = 0;
		if(cmd.allArgumentsSummary() == 0) {
			throw new NotEnoughArgumentsException();
		} else if(cmd.getArgumentsAmount() > 2) {
			throw new TooManyArgumentsException();
		} else if(cmd.getArgumentsAmount() == 2) {
			try {
				thickness = Integer.parseInt(cmd.getArgument(1));				
			} catch(NumberFormatException e) {
				throw new InvalidArgumentException();
			}
		}
		
		selectionCommandsChecks(executor, thickness, thickness, thickness);
	}
	
	public void replaceCommand(Player executor, String message) {
		// //replace [from] <to>
		WECommand cmd = new WECommand(message, null);
		
		int argsSummary = cmd.allArgumentsSummary();
		if(argsSummary == 0) {
			throw new NotEnoughArgumentsException();
		} else if(argsSummary > 2) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 0, 1, 0);
	}
	
	public void overlayCommand(Player executor, String message) {
		// //overlay <pattern>
		WECommand cmd = new WECommand(message, null);
		
		int argsSummary = cmd.allArgumentsSummary();
		if(argsSummary == 0) {
			throw new NotEnoughArgumentsException();
		} else if(argsSummary > 1) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 0, 1, 0);
	}
	
	public void wallsCommand(Player executor, String message) {
		// walls <pattern>
		WECommand cmd = new WECommand(message, null);
		
		int argumentsSummary = cmd.allArgumentsSummary();
		if(argumentsSummary == 0) {
			throw new NotEnoughArgumentsException();
		} else if(argumentsSummary > 1) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 0, 0, 0);
	}
	
	public void facesCommand(Player executor, String message) {
		// //faces <pattern>
		WECommand cmd = new WECommand(message, null);
		
		int argsSummary = cmd.allArgumentsSummary();
		if(argsSummary == 0) {
			throw new NotEnoughArgumentsException();
		} else if(argsSummary > 1) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 0, 0, 0);
	}
	
	public void forestCommand(Player executor, String message) {
		// //forest [type] [density]
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 2) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 2, 2 ,2);
	}
	
	public void floraCommand(Player executor, String message) {
		// //flora [density]
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 1) {
			throw new TooManyArgumentsException();
		}
		
		selectionCommandsChecks(executor, 2, 1, 2);
	}
	
	
	
	private void generationCommandsChecks(Player executor, BoundingBox area) {
		checkEditRestrictions(new AffectedArea(area, executor.getWorld()), executor);
	}
	
	public void cylCommand(Player executor, String message) {
		// //cyl -h <pattern> <radii> [height]
		// //hcyl <pattern> <radii> [height]
		if(message.startsWith("//hcyl")) {
			message = message.replaceFirst("^//hcyl", "//cyl -h");
		}
		
		WECommand cmd = new WECommand(message, null);
		
		int argumentsAmount = cmd.getArgumentsAmount();
		if(argumentsAmount <= 1) {
			throw new NotEnoughArgumentsException();
		} else if(argumentsAmount > 3) {
			throw new TooManyArgumentsException();
		}
		
		int radius;
		int height = 0;
		try {
			radius = Integer.parseInt(cmd.getArgument(1));
			if(argumentsAmount == 3) {
				height = Integer.parseInt(cmd.getArgument(2)) - 1;
			}
		} catch(NumberFormatException e) {
			throw new InvalidArgumentException();
		}
		
		generationCommandsChecks(executor, WorldUtils.boxOverLocationCenter(executor.getLocation(), radius * 2, height, radius * 2));
	}
	
	public void sphereCommand(Player executor, String message) {
		// //sphere [-hr] <pattern> <radii>
		// //hsphere [-r] <pattern> <radii>
		WECommand cmd = new WECommand(message, null);
		
		if(message.startsWith("//hsphere")) {
			cmd.setCommand("//sphere");
			cmd.addOption("h");
		}
		
		int argumentsAmount = cmd.getArgumentsAmount();
		if(argumentsAmount <= 1) {
			throw new NotEnoughArgumentsException();
		} else if(argumentsAmount > 2) {
			throw new TooManyArgumentsException();
		}
		
		int radius;
		try {
			radius = Integer.parseInt(cmd.getArgument(1));
		} catch(NumberFormatException e) {
			throw new InvalidArgumentException();
		}
		
		BoundingBox box;
		if(cmd.hasOption("r")) {
			box = WorldUtils.boxAtLocationCenter(executor.getLocation().add(0, radius, 0), radius * 2, radius * 2, radius * 2);
		} else {
			box = WorldUtils.boxAtLocationCenter(executor.getLocation(), radius * 2, radius * 2, radius * 2);
		}
		
		generationCommandsChecks(executor, box);
	}
	
	public void pyramidCommand(Player executor, String message) {
		// //pyramid [-h] <pattern> <size>
		// //hpyramid <pattern> <size>
		if(message.startsWith("//hpyramid")) {
			message.replaceFirst("^//hpyramid", "//pyramid -h");
		}
		WECommand cmd = new WECommand(message, null);
		
		int argumentsAmount = cmd.getArgumentsAmount();
		if(argumentsAmount <= 1) {
			throw new NotEnoughArgumentsException();
		} else if(argumentsAmount > 2) {
			throw new TooManyArgumentsException();
		}
		
		int size;
		try {
			size = Integer.parseInt(cmd.getArgument(1));
		} catch(NumberFormatException e) {
			throw new InvalidArgumentException();
		}
		
		generationCommandsChecks(executor, WorldUtils.boxOverLocationCenter(executor.getLocation(), size, size - 1, size));
	}
	
	
	
	
	private void clipboardCommandsChecks(Player executor, AffectedArea area) {
		checkEditRestrictions(area, executor);
	}
	
	public void copyCommand(Player executor, String message) {
		// //copy [-be] [-m <mask>]
		// -be -m arguments are not allowed
		WECommand cmd = new WECommand(message, Arrays.asList("m"));
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new NotAllowedArgumentException();
		}
		
		AffectedArea area = getAffectedAreaOfSelection(getSession(executor), 0, 0, 0);
		clipboardCommandsChecks(executor, area);
		
		LocalSession ls = getSession(executor);
		if(ls == null) {
			throw new IncompleteSelectException();
		}
		
		clipboardManager.addClipboard(getClipboardAreaOfSelection(ls, executor));
	}
	
	public void cutCommand(Player executor, String message) {
		// //cut [-be] [leavePattern] [-m <mask>]
		// -be leavePattern -m arguments are not allowed
		WECommand cmd = new WECommand(message, Arrays.asList("m"));
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new NotAllowedArgumentException();
		}
		
		LocalSession ls = getSession(executor);
		if(ls == null) {
			throw new IncompleteSelectException();
		}
		
		AffectedArea area = getAffectedAreaOfSelection(ls, 0, 0, 0);
		clipboardCommandsChecks(executor, area);
		
		clipboardManager.addClipboard(getClipboardAreaOfSelection(ls, executor));
	}
	
	public void pasteCommand(Player executor, String message) {
		// //paste [-abenos] [-m <sourceMask>]
		// -abenos -m arguments are not allowed
		WECommand cmd = new WECommand(message, Arrays.asList("m"));
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new NotAllowedArgumentException();
		}
		
		ClipboardArea clipboard = clipboardManager.getClipboard(executor);
		if(clipboard == null) {
			throw new NoClipboardStoredException();
		}
		
		Location loc = executor.getLocation();
		AffectedArea pasteArea = new AffectedArea(clipboard.getBoxFromCoords(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), executor.getWorld());
		
		clipboardCommandsChecks(executor, pasteArea);
	}
	
	public void clearClipboardCommand(Player executor, String message) {
		// /clearclipboard
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new TooManyArgumentsException();
		}
		
		clipboardManager.removeClipboard(executor);
	}
	
	
	
	
	
	public void undoCommand(Player executor, String message) {
		// //undo [times] [player]
		// /undo [times] [player]
		// times player arguments are not allowed
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new NotAllowedArgumentException();
		}
	}
	
	public void redoCommand(Player executor, String message) {
		// //redo [times] [player]
		// /redo [times] [player]
		// times player arguments are not allowed
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new NotAllowedArgumentException();
		}
	}
	
	public void clearhistoryCommand(Player executor, String message) {
		// //clearhistory
		// /clearhistory
		WECommand cmd = new WECommand(message, null);
		
		if(cmd.allArgumentsSummary() > 0) {
			throw new TooManyArgumentsException();
		}
	}
}