package net.minedcontrol.bukkit.menus;

import net.minedcontrol.zamalib.plugins.config.PluginConfiguration;

/**
 * A collection of the settings of the Menus utility plugin.
 * <p>
 * Date Created: Jan 21, 2014
 * 
 * @author Brutus
 *
 */

public class MenusConfiguration extends PluginConfiguration {
	
	
	public MenusConfiguration() 
			throws IllegalArgumentException {
		
		super(Menus.getPlugin());
	}

	@Override
	protected void onLoad() throws IllegalStateException {
		// TODO Add parsing of the config.yml into whatever fields are
		// necessary, such as the settings of the packet engine.
		
	}
	


}
