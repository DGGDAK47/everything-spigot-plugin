package org.ultragore.everything.utils;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ultragore.everything.Everything;

public class Configs {
	
	public static final String CONFIGS_DIR_PATH = "plugins/Everything";
	public static final String CONFIGS_DIR_NAME = "Everything";
	
	
	public static boolean configsDirExists() {
		return new File(CONFIGS_DIR_PATH).exists();
	}
	
	public static boolean createConfigsDir() {
		return new File(CONFIGS_DIR_PATH).mkdir();
	}
	
	public static boolean configExists(String configFile) {
		return new File(CONFIGS_DIR_PATH + "/" + configFile).exists();
	}
	
	
	/**
	 * Extracts config file from Everything plugin's jar by classpath
	 * 
	 * @param classPath - path in jar to config file
	 * @param configFile - config file name, with ext, thet will be placed in configs directory
	 * @throws IOException 
	 * @throws InterruptedException
	 * 
	 * @return returns shell command exit code. 0 indicates normal termination
	 */
	public static int extractConfigFile(String classPath, String configFile) throws IOException, InterruptedException {
		String pluginFileName = ((Everything) Bukkit.getPluginManager().getPlugin("Everything")).getPluginFileName();
		String command = String.format("unzip -p plugins/%s %s > %s/%s", pluginFileName, classPath, CONFIGS_DIR_PATH, configFile);
		String[] script = { "/bin/sh", "-c", command };
		return Runtime.getRuntime().exec(script).waitFor();
	}

}