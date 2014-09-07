package net.minedcontrol.bukkit.menus.basis.events;

import java.util.Stack;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuChoice;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.MenuStage;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * Event thrown when an option in a menu is selected by a player, to the 
 * listeners of that menu option.
 * <p>
 * Date Created: Jan 16, 2014
 * 
 * @author Brutus
 *
 * @see MenuSelectEvent
 */
public class PlayerMenuSelectEvent extends MenuSelectEvent implements MenuEvent {
	
	private ZamaPlayer player;
	
	/**
	 * Class constructor. 
	 * 
	 * @param selected	The option that was selected, not <code>null</code>.
	 * @param menu		The menu that the option was selected with, not
	 * 					<code>null</code>.
	 * @param stage		The stage in which the selection was made.
	 * @param path		The path of the menu up until the point of the
	 * 					selection, not <code>null</code>.
	 * @param player	The player that made the selection, not 
	 * 					<code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>.
	 */
	public PlayerMenuSelectEvent(MenuOption selected, Menu menu, MenuStage stage,
			Stack<MenuChoice> path, ZamaPlayer player) 
					throws IllegalArgumentException {
		
		super(selected, menu, stage, path);
		
		if(player == null)
			throw new IllegalArgumentException("player cannot be null");
		
		this.player = player;
	}
	
	@Override
	public void call(MenuListener listener) {
		if(listener == null)
			return;
		
		super.call(listener);
		listener.onPlayerSelect(this);
	}

	/**
	 * Gets the player that made the selection through the menu.
	 * 
	 * @return	The player that made the selection.
	 */
	public ZamaPlayer getPlayer() { return this.player; }
	
}
