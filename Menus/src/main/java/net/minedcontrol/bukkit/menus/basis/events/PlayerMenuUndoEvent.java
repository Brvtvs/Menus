package net.minedcontrol.bukkit.menus.basis.events;

import java.util.Stack;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuChoice;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * An event called when an option is undone from a menu by a player.
 * <p>
 * Date Created: Jan 17, 2014
 * 
 * @author Brutus
 *
 */

public class PlayerMenuUndoEvent extends MenuUndoEvent implements MenuEvent {
	
	private ZamaPlayer player;

	/**
	 * Class constructor.
	 * 
	 * @param selected	The option being undone, not <code>null</code>.
	 * @param menu		The menu that the option was undone with, not
	 * 					<code>null</code>.
	 * @param path		The path of the menu up until the point of the
	 * 					undoing, not <code>null</code>.
	 * @param player	The player that undid the selection.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>.
	 */
	public PlayerMenuUndoEvent(MenuOption selected, Menu menu,
			Stack<MenuChoice> path, ZamaPlayer player) 
					throws IllegalArgumentException {
		
		super(selected, menu, path);
		
		if(player == null)
			throw new IllegalArgumentException("player cannot be null");
		
		this.player = player;
	}
	
	@Override
	public void call(MenuListener listener) {
		if(listener == null)
			return;
		
		super.call(listener);
		listener.onPlayerUndo(this);
	}

	/**
	 * Gets the player that undid the selection through the menu.
	 * 
	 * @return	The player that undid the selection.
	 */
	public ZamaPlayer getPlayer() { return this.player; }
	
}

