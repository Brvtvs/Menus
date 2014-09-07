package net.minedcontrol.bukkit.menus.basis.events;

import java.util.Stack;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuChoice;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;

/**
 * An event called when an option is undone from a menu.
 * <p>
 * Date Created: Jan 17, 2014
 * 
 * @author Brutus
 *
 */
public class MenuUndoEvent implements MenuEvent {

	private MenuOption option;
	private Stack<MenuChoice> path;
	private Menu menu;

	/**
	 * Class constructor.
	 * 
	 * @param selected	The option being undone, not <code>null</code>.
	 * @param menu		The menu that the option was undone with, not
	 * 					<code>null</code>.
	 * @param path		The path of the menu up until the point of the
	 * 					undoing, not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>.
	 */
	public MenuUndoEvent(MenuOption selected, Menu menu, 
			Stack<MenuChoice> path) throws IllegalArgumentException {
		
		
		if(selected == null || menu == null || path == null)
			throw new IllegalArgumentException("params cannot be null");

		this.option = selected;
		this.path = path;
		this.menu = menu;
	}

	@Override
	public void call(MenuListener listener) {
		if(listener == null)
			return;
		
		listener.onUndo(this);
	}

	/**
	 * Gets the option that was undone and triggered this event.
	 * 
	 * @return	The selected option.
	 */
	public MenuOption getUndone() { return this.option; }

	/**
	 * Gets the menu that undid the selection.
	 * 
	 * @return	The selection's menu.
	 */
	public Menu getMenu() { return this.menu; }

	/**
	 * Gets the path the menu took to get to the option in the form of
	 * a stack, with the most recent entries at the top of the stack.
	 * 
	 * @return	The path that led to the undone selection.
	 */
	@SuppressWarnings("unchecked")
	public Stack<MenuChoice> getPath() {
		return ((Stack<MenuChoice>) path.clone());
	}

}
