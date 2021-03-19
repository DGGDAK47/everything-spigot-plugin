package org.ultragore.everything.managers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.ultragore.everything.Everything;
import org.ultragore.everything.types.Module;
import org.ultragore.everything.types.Modules;
import org.ultragore.everything.utils.Logger;

public class ModulesManager {
	
	public static final String SERVICE_NAME = "ModulesManager";
	private List<Module> modules = new ArrayList<Module>();
	private Everything plugin;
	
	public ModulesManager(Everything plugin) {
		this.plugin = plugin;
		
		printLog("Initializing modules..");
		
		Module module;
		Field field;
		Class<? extends Module> moduleClass;
		Class<Module> moduleSuperClass;
		for(Modules enumModule: Modules.values()) {
			try {
				moduleClass = enumModule.getModuleClass();
				moduleSuperClass = enumModule.getModuleClass().getSuperclass();
				module = (Module) moduleClass.getConstructor().newInstance();
				
				field = moduleSuperClass.getDeclaredField("plugin");
				field.setAccessible(true);
				field.set(module, this.plugin);
				field.setAccessible(false);
				
				field = moduleSuperClass.getDeclaredField("moduleName");
				field.setAccessible(true);
				field.set(module, enumModule.getModuleName());
				field.setAccessible(false);
				
				field = moduleSuperClass.getDeclaredField("moduleCommand");
				field.setAccessible(true);
				field.set(module, enumModule.getModuleCommand());
				field.setAccessible(false);
				
				modules.add(module);
			} catch (Exception e) {
				printLog("Error occured while initializing modules. Check stacktrace down for more info.");
				e.printStackTrace();
				Logger.serverStop(SERVICE_NAME);
				plugin.getServer().shutdown();
				return;
			}
		}
		
		printLog("Modules initialized.");
	}
	
	private void printLog(String text) {
		Logger.printCoreLog(SERVICE_NAME, text);
	}
	private void printEnablingErrorLog(String moduleName) {
		printLog("Error occured while enabling module " + moduleName + ". Check stacktrace down for more information.");
	}
	private void printDisablingErrorLog(String moduleName) {
		printLog("Error occured while disabling module " + moduleName + ". Check stacktrace down for more information.");
	}
	
	public void enableModules() {
		printLog("Enabling modules...");
		
		for(Module m: modules) {
			if(!m.isActive()) {
				try {
					m.enableModule();
				} catch(Exception e) {
					printEnablingErrorLog(m.getModuleName());
					e.printStackTrace();
					Logger.serverStop(SERVICE_NAME);
					plugin.getServer().shutdown();
					return;
				}
			} else {
				printLog(String.format("Module '%s' is already enabled", m.getModuleName()));
			}
		}
	}
	public void disableModules() {
		printLog("Disabling modules...");
		
		for(Module m: modules) {
			if(m.isActive()) {
				try {
					m.disableModule();
				} catch(Exception e) {
					printDisablingErrorLog(m.getModuleName());
					e.printStackTrace();
				}		
			} else {
				printLog(String.format("Module '%s' is not active", m.getModuleName()));
			}
		}
		
		printLog("Modules disabled.");
	}
	
	public Module getModuleByName(String name) {
		for(Module m: modules) {
			if(m.getModuleName().equals(name)) {
				return m;
			}
		}
		
		return null;
	}
	public Module getModuleByCommand(String command) {
		for(Module m: modules) {
			if(m.getModuleCommand().equals(command)) {
				return m;
			}
		}
		
		return null;
	}
}
