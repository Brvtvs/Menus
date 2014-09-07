package net.minedcontrol.bukkit.menus.basis.events;

import java.util.Stack;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuChoice;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.MenuStage;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;

/**
 * Event thrown when an option in a menu is selected, to the listeners of
 * that menu option.
 * <p>
 * Date Created: Jan 16, 2014
 * 
 * @author Brutus
 *
 * @see PlayerMenuSelectEvent
 */
public class MenuSelectEvent implements MenuEvent {
	
	private final MenuOption option;
	private final Stack<MenuChoice> path;
	private final Menu menu;
	private final MenuStage stage;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param selected	The option that was selected, not <code>null</code>.
	 * @param menu		The menu that the option was selected with, not
	 * 					<code>null</code>.
	 * @param stage		The stage in which the selection was made.
	 * @param path		The path of the menu up until the point of the
	 * 					selection, not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>.
	 */
	public MenuSelectEvent(MenuOption selected, Menu menu, MenuStage stage, 
			Stack<MenuChoice> path) throws IllegalArgumentException {
		
		if(selected == null || menu == null || path == null)
			throw new IllegalArgumentException("params cannot be null");
		
		this.option = selected;
		this.path = path;
		this.menu = menu;
		this.stage = stage;
	}

	@Override
	public void call(MenuListener listener) {
		if(listener == null)
			return;
		
		listener.onSelect(this);
	}
	
	/**
	 * Gets the option that was selected and triggered this event.
	 * 
	 * @return	The selected option.
	 */
	public MenuOption getSelected() { return this.option; }
	
	/**
	 * Gets the menu that made the selection.
	 * 
	 * @return	The selection's menu.
	 */
	public Menu getMenu() { return this.menu; }
	
	/**
	 * Gets the menu stage that the selection was made in.
	 * 
	 * @return	the selection's stage.
	 */
	public MenuStage getMenuStage() { return this.stage; }
	
	/**
	 * Gets the path the menu took to get to the option in the form of
	 * a stack, with the most recent entries at the top of the stack.
	 * 
	 * @return	The path that led to the selection.
	 */
	@SuppressWarnings("unchecked")
	public Stack<MenuChoice> getPath() {
		return ((Stack<MenuChoice>) path.clone());
	}
	

}
