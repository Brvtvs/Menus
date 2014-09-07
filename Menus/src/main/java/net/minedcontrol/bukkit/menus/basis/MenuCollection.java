package net.minedcontrol.bukkit.menus.basis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minedcontrol.zamalib.players.PlayerUID;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A collection of menus. Intended to serve as a server-wide, singleton 
 * collection.
 * <p>
 * Date Created: Jan 28, 2014
 * 
 * @author Brutus
 *
 */

public class MenuCollection {
	
	private Map<PlayerUID, Set<Menu>> userMenus;
	private Set<Menu> userlessMenus;
	
	public MenuCollection() {
		this.userMenus = new HashMap<PlayerUID, Set<Menu>>();
		this.userlessMenus = new HashSet<Menu>();
	}

	//--------
	//PUBLIC
	//--------
	
	/**
	 * Gets a copy of the set of all of the menus in the collection.
	 * 
	 * @return	All of the menus in the collection.
	 */
	public Set<Menu> getAllMenus() {
		Set<Menu> ret = new HashSet<Menu>();
		
		for(Set<Menu> menus : userMenus.values()) {
			if(menus == null) continue;
			
			ret.addAll(menus);
		}
		
		ret.addAll(userlessMenus);
		return ret;
	}
	
	/**
	 * Gets a copy of the set of menus for a player.
	 * 
	 * @param player	The player to get the menus of.
	 * @return			The player's menus.
	 */
	public Set<Menu> getMenus(ZamaPlayer player) {
		if(player == null)
			return new HashSet<Menu>();
		
		Set<Menu> menus = userMenus.get(player);
		if(menus == null)
			return new HashSet<Menu>();
		
		return new HashSet<Menu>(menus);
	}
	
	/**
	 * Gets all menus from the collection that do not have a user 
	 * associated with them.
	 * 
	 * @return	The set of userless menus.
	 */
	public Set<Menu> getUserlessMenus() {
		return new HashSet<Menu>(userlessMenus);
	}
	
	
	//--------
	//DEFAULT
	//--------
	
	/**
	 * Adds a menu to the collection. Done automatically when a menu is 
	 * created.
	 * 
	 * @param menu	The menu to add.
	 */
	void addMenu(Menu menu) {
		if(menu == null)
			return;
		
		ZamaPlayer user = menu.getUser();
		if(user == null)
			userlessMenus.add(menu);
		
		else {
			Set<Menu> menus = userMenus.get(user.getUID());
			
			if(menus == null)
				userMenus.put(user.getUID(), menus = new HashSet<Menu>());
			
			menus.add(menu);
		}
	}
	
	/**
	 * Removes a menu from the collection. Done automatically when a menu
	 * completes.
	 * 
	 * @param menu	The menu to remove.
	 */
	void removeMenu(Menu menu) {
		if(menu == null) 
			return;
		
		ZamaPlayer user = menu.getUser();
		if(user == null)
			userlessMenus.remove(menu);
		
		else {
			Set<Menu> menus = userMenus.get(user.getUID());
			
			if(menus == null)
				return;
			
			menus.remove(menu);
			
			//removes the player's key,value pair if they no longer have
			// any menus active.
			if(menus.isEmpty())
				userMenus.remove(user.getUID());
			
		}
		
	}

}
