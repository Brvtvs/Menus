package net.minedcontrol.bukkit.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuCollection;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.MenuUnderlay;
import net.minedcontrol.bukkit.menus.uis.packetediting.PacketEngine;
import net.minedcontrol.bukkit.menus.uis.packetediting.WhitelistEngine;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * The singleton manager of the Menus utility.
 * <p>
 * Date Created: Jan 28, 2014
 * 
 * @author Brutus
 *
 */
public class MenuManager {
	
	private ConfigurationLoader configs;
	
	private MenuCollection menus;
	
	private Map<String, MenuOption> options;
	private Map<String, MenuUnderlay> underlays;
	
	private PacketEngine engine;
	
	
	/**
	 * Constructs a new menu manager singleton and loads the configuration
	 * files for Menus.
	 */
	public MenuManager() {
		this.menus = new MenuCollection();
		this.options = new HashMap<String, MenuOption>();
		this.underlays = new HashMap<String, MenuUnderlay>();
	}
	
	
	//--------
	//PUBLIC
	//--------
	
	/**
	 * Gets the collection of all menus on the server.
	 * 
	 * @return	The menu collection singleton.
	 */
	public MenuCollection getMenuCollection() {
		return this.menus;
	}
	
	/**
	 * Convenience method that gets a copy of the set of menus for a 
	 * player from the server-wide collection.
	 * 
	 * @param player	The player to get the menus of.
	 * @return			The player's menus.
	 */
	public Set<Menu> getMenus(ZamaPlayer player) {
		//the menu collection guarantees a not-null return value, so it is
		// not done here.
		return menus.getMenus(player);
	}
	
	
	/**
	 * Gets the engine being use to manage packets.
	 * 
	 * @return	The packet engine.
	 * 
	 * @deprecated	Packet engine will be moved into its own library
	 */
	@Deprecated
	public PacketEngine getPacketEngine() {
		return engine;
	}
	
	/**
	 * Gets the collection of the configurations for the Menus utility.
	 * 
	 * @return	Menus' configurations.
	 */
	public ConfigurationLoader getConfigurationManager() {
		if(configs == null)
			initialize();
		
		return configs;
	}
	
	/**
	 * Gets the menu option object for a given string id, if it is found.
	 * 
	 * @param id	The id of the desired menu option.
	 * @return		The menu option for the given id, if found, else
	 * 				<code>null</code>.
	 */
	public MenuOption getOption(String id) {
		return options.get(id);
	}
	
	/**
	 * Gets the menu underlay object for a given string id, if it is found.
	 * 
	 * @param id	The id of the desired menu underlay.
	 * @return		The menu option for the given id, if found, else
	 * 				<code>null</code>.
	 */
	public MenuUnderlay getUnderlay(String id) {
		return underlays.get(id);
	}
	
	/**
	 * Adds a menu option to the central, key-accessed collection,
	 * accessible by its string id value.
	 * 
	 * @param option	The menu option to add.
	 * @return			<code>true</code> if the option was successfully
	 * 					added.
	 */
	public boolean addOption(MenuOption option) {
		if(option == null)
			return false;
		
		String key = option.getId();
		if(key == null || key.equals(""))
			return false;
		
		options.put(key, option);
		return true;
	}
	
	/**
	 * Adds a menu underlay to the central, key-accessed collection, 
	 * accessible by its string id value. 
	 * 
	 * @param underlay	The menu underlay to add.
	 * @return			<code>true</code> if the underlay was successfully
	 * 					added.
	 */
	public boolean addUnderlay(MenuUnderlay underlay) {
		if(underlay == null)
			return false;
		
		String key = underlay.getId();
		if(key == null || key.equals(""))
			return false;
		
		underlays.put(key, underlay);
		return true;
	}
	

	//--------
	//DEFAULT
	//--------

	void initialize() {
		this.configs = new ConfigurationLoader();
		this.configs.initialize();
		
		//TODO TESTING IMPLEMENTATION (TEMPORARY)
		try {
			this.engine = new WhitelistEngine(); 
		}
		catch(Exception e) {
			String message = "Error encountered while initializing the packet engine.";
			Zama.debug(Menus.getPlugin(), message, message);
			Zama.debug(Menus.getPlugin(), e, e);
		}
	}

}
