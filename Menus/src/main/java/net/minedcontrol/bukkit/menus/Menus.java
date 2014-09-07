package net.minedcontrol.bukkit.menus;

import org.bukkit.Bukkit;

/**
 * Static accessor for the plugin main and menu manager of the Menus
 * utility.
 * <p>
 * Date Created: Jan 21, 2014
 * 
 * @author Brutus
 *
 */

public class Menus {
	
	private static final String PLUGIN_NAME = "Menus";
	
	private static MenuManager manager;
	
	/**
	 * Gets the main plugin class for Menus.
	 * 
	 * @return	Menus's plugin class.
	 */
	public static MenusPlugin getPlugin() {
		return (MenusPlugin) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
	
	/**
	 * Gets the singleton manager of the Menus utiltity.
	 * 
	 * @return	The menu manager singleton.
	 */
	public static MenuManager getManager() {
		if(manager == null) {
			manager = new MenuManager();
			manager.initialize();
		}
		
		return manager;
	}

}
