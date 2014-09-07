package net.minedcontrol.bukkit.menus.basis.listening;

import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.events.MenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.MenuUndoEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuUndoEvent;


/**
 * A listener that is called when an option element in a menu is selected.
 * <p>
 * Listeners must be registered with an option with the 
 * <code>MenuOption.registerListener(MenuListener)</code> method in order 
 * to be called.
 * <p>
 * The listening methods are only called when the option is given a valid
 * menu object to attribute the selection to.
 * <p>
 * Only implement a body to the methods that are actually required.
 * <p>
 * Date Created: Dec 22, 2013
 * 
 * @author Brutus
 * 
 * @see MenuOption
 * @see Menu
 */

public interface MenuListener {

	/**
	 * Called whenever the option is selected, whether by a player or not. 
	 * 
	 * @param event	The event called by the option's selection.
	 */
	public void onSelect(MenuSelectEvent event);
	
	/**
	 * Called when the option is selected, but only if it was by a player.
	 * 
	 * @param event	The event called by the option's selection by a player.
	 */
	public void onPlayerSelect(PlayerMenuSelectEvent event);
	
	/**
	 * Called when the option is undone, whether by a player or not.
	 * 
	 * @param event	The event called by the option's undoing.
	 */
	public void onUndo(MenuUndoEvent event);
	
	/**
	 * Called when the option is undone, whether by a player or not.
	 * 
	 * @param event	The event called by the option's undoing by a player.
	 */
	public void onPlayerUndo(PlayerMenuUndoEvent event);
	
}
