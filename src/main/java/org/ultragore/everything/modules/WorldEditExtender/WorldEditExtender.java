package org.ultragore.everything.modules.WorldEditExtender;

import org.bukkit.Bukkit;
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

public class WorldEditExtender extends Module {

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
		weCommandManager = new WECommandManager(restrictionManager, clipboardManager, cooldownManager);
		
		printLog("Registering events..");
		Bukkit.getPluginManager().registerEvents(clipboardManager, plugin);
		Bukkit.getPluginManager().registerEvents(weCommandManager, plugin);
		
		Logger.moduleEnabled(this);
	}
	
	
	@Override
	public void disableModule() {
		Logger.moduleDisabled(this);
	}

}
