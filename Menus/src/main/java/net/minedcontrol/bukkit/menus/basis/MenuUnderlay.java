package net.minedcontrol.bukkit.menus.basis;


import java.util.Set;

/**
 * A data structure that provides stages of options, either realized or
 * virtualized, that a menu can traverse over.
 * <p>
 * Menu underlays and options cannot be interacted with except with a valid 
 * <code>Menu</code> object.
 * <p>
 * Implementations of menu underlays should either add themselves to the
 * menu manager singleton or be added by their constructor immediately
 * post-construction to maintain consistency and accessibility.
 * <p>
 * Date Created: Jan 9, 2014
 * 
 * @author Brutus
 *
 * @see Menu
 * @see UnderlayNode
 */

public interface MenuUnderlay {
	
	/**
	 * Gets the ID of this underlay, which should be unique within a 
	 * runtime and usable to access this underlay from the menu manager
	 * singleton.
	 * 
	 * @return	This underlay's string ID.
	 */
	public String getId();
	
	/**
	 * Gets the first node of the the underlay.
	 * 
	 * @param menu	The menu making the request. Not <code>null</code>.
	 * @return		The underlay's first node.
	 */
	public UnderlayNode getStart(Menu menu);
	
	/**
	 * Gets a copy of the set of all menus currently traversing this 
	 * underlay.
	 * 	
	 * @return	This underlay's current menus.
	 */
	public Set<Menu> getMenus();
	
	/**
	 * Called when a menu is constructed and adds the menu to the set of 
	 * menus currently traversing this underlay. Fails if the menu is not 
	 * currently traversing this underlay.
	 * 
	 * @param menu	The menu to add.
	 * @return		<code>true</code> if the menu was successfully added.
	 * 				<code>false</code> if the menu was already present,
	 * 				is not associated with this underlay, or was 
	 * 				<code>null</code>.
	 */
	public boolean addMenu(Menu menu);
	
	/**
	 * Removes a menu from the set of menus currently traversing this 
	 * underlay, if it is present there. Called when a menu reaches an
	 * end of its underlay.
	 * 
	 * @param menu	The menu to remove.
	 * @return		<code>true</code> if the menu was in the set.
	 */
	public boolean removeMenu(Menu menu);
	
	

}
