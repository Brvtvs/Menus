package net.minedcontrol.bukkit.menus;

import java.util.logging.Level;

import net.minedcontrol.bukkit.menus.basis.MenuOptionConfiguration;
import net.minedcontrol.bukkit.menus.underlays.graphs.config.DirectedGraphConfiguration;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * A collection of the configurations for the Menus utility, including
 * very specific (non-generic) code and API for loading and accessing 
 * each of them.
 * <p>
 * Date Created: Jan 21, 2014
 * 
 * @author Brutus
 *
 */
public class ConfigurationLoader {
	
	/*
	 * Loads ancillary configs on-first-access, but calls that access on
	 * initializing (to have cleaner, easier to use/edit code,
	 * encapsulating try/catch and construction in the per-config-type 
	 * methods)
	 */

	//the configuration for the plugin overall, the "config.yml" file
	private MenusConfiguration pluginConfig;

	//the configuration of the utility's menu options.
	private MenuOptionConfiguration optionsConfig;
	
	//a configuration for directed graph underlays.
	private DirectedGraphConfiguration graphConfig;


	
	//--------
	//PUBLIC
	//--------
	
	/**
	 * Gets the main configuration for the Menus utility.
	 * 
	 * @return	The Menus plugin configuration.
	 */
	public MenusConfiguration getPluginConfig() {
		return pluginConfig;
	}
	
	/**
	 * Gets the configured menu options.
	 * 
	 * @return	Configured menu options. Returns <code>null</code> if 
	 * 			unable to load them.
	 */
	public MenuOptionConfiguration getMenuOptionConfig() {
		if(optionsConfig == null) {
			try {
				optionsConfig = new MenuOptionConfiguration();
			}
			catch(Exception e) {
				String message = "Could not load the menu option "
						+ "configuration, returning null.";
				
				Zama.debug(Menus.getPlugin(), message, message);
				Zama.debug(Menus.getPlugin(), e, e);
			}
		}

		return optionsConfig;
	}
	
	/**
	 * Gets the configured directed graph menu underlays.
	 * 
	 * @return	Configured directed graph menu underlays. Returns 
	 * 			<code>null</code> if unable to load them.
	 */
	public DirectedGraphConfiguration getDirectedGraphUnderlayConfig() {
		if(graphConfig == null) {
			try {
				graphConfig = new DirectedGraphConfiguration();
			}
			catch(Exception e) {
				String message = "Could not load the directed graph menu "
						+ "underlay configuration, returning null.";
				
				Zama.debug(Menus.getPlugin(), message, message);
				Zama.debug(Menus.getPlugin(), e, e);
			}
		}
		
		return this.graphConfig;
	}
	
	
	//--------
	//DEFAULT
	//--------

	void initialize() {
		try {
			this.pluginConfig = new MenusConfiguration();
		}
		catch(Exception e) {
			Menus.getPlugin().getLogger().log(Level.SEVERE, "Encountered "
					+ "an error while loading Menus' config.yml");
			e.printStackTrace();
		}
		
		//THE ORDERING OF THE FOLLOWING METHODS MATTERS, THEY REFERENCE ONE 
		// ANOTHER

		//menu options (options.yml)
		getMenuOptionConfig();
		
		//directed graph menu underlays (underlays/directedgraphs.yml)
		getDirectedGraphUnderlayConfig();
	}
}
