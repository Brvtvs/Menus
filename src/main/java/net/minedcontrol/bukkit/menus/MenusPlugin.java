package net.minedcontrol.bukkit.menus;

import net.minedcontrol.bukkit.menus.testing.TestClientMain;
import net.minedcontrol.zamalib.plugins.ZamaPlugin;

/**
 * Menus: a plugin library that facilitates the creation, organization,
 * and utilization of menus in bukkit.
 * <p>
 * Date Created: Feb 3, 2014
 * 
 * @author Brutus
 *
 */
public class MenusPlugin extends ZamaPlugin {
	
	public void postEnable() {
		MenuManager manager = Menus.getManager(); // initialize the manager
		
		//TODO Testing code (REMOVE)
		TestClientMain testing = new TestClientMain(this);
	}
	
	/**
	 * Gets the manager of the Menus utility.
	 * 
	 * @return	The Menus manager.
	 */
	public MenuManager getManager() {
		return Menus.getManager();
	}

}
