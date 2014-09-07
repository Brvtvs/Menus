package net.minedcontrol.bukkit.menus.basis;

import net.minedcontrol.bukkit.menus.basis.responses.MenuResponse;
import net.minedcontrol.bukkit.menus.basis.responses.MenuResponseType;

/**
 * An interface to display and interact with an underlying Menu. 
 * <p>
 * An interface has an inherently one-to-one relationship with its menu. 
 * As a result, in instances where the actual form the interface takes is
 * not per-user, it may be necessary to create an instance of this, per-user,
 * in order to wrap it and translate certain behaviors into a per-user 
 * formation. Alternatively, a broader definition of the "user" and the
 * frontend implementation of the interface could facilitate interaction
 * on a broader scale, so long as the one-to-one relationship between
 * a <code>Menu</code> and its <code>MenuInterface</code> is maintained.
 * <p>
 * This implementation assumes that each extended UI will be comprised of 
 * a set of option "slots" that can be used to display the name of an 
 * option and/or its description. It is also assumed that the
 * user interface will have a "slot" with which to display the title of
 * either the menu or its current set of options.
 * <p>
 * Potentially has a limited number of option slots it can contain and a 
 * limited length of option names/titles it can display.
 * <p>
 * To dissociate a menu from its interface, you must use the methods within
 * the underlying menu class, which govern and guarantee mutual 
 * dissociation so that it is not possible to do something like remove an 
 * interface from a menu but not remove the references to the menu from 
 * the interface.
 * <p>
 * Implementation note: when implementing a user interface, there are a two 
 * main requirements that define what a user interface is. First, the 
 * interface must display the state of the menu through implementing the 
 * {@link #onClose()}, and {{@link #updateDisplay(MenuStage)} methods. 
 * Secondly, the interface must allow the user to select an option from the 
 * state by implementing some kind of interact() method (whatever is 
 * appropriate for the type of UI) that calls {@link #select(MenuOption)} 
 * with the appropriate menu option for their selection and is triggered by 
 * some kind of player interaction. This can be done using menu stage 
 * indices to correlate indices in an interface to actual 
 * <code>MenuOption</code> objects, or through whatever other sufficient 
 * means. Unless these two aspects of the user interface are well 
 * implemented, the interface will not represent a true or usable menu 
 * interface.
 * <p>
 * Date Created: Jan 9, 2014
 * 
 * @author Brutus
 *
 * @see Menu
 */

public abstract class MenuInterface {
	
	private Menu menu;
	
	private boolean closed;
	
	
	/**
	 * Class constructor. 
	 * <p>
	 * Must be associated with a menu post-construction through the menu
	 * object's methods.
	 */
	public MenuInterface() {
		closed = true;
	}
	
	
	//--------
	//PUBLIC
	//--------
	
	/**
	 * Attempts to select an option from the current status of this 
	 * interface's status.
	 * 
	 * @param option	The option to select.
	 * @return			The status of the attempted selection.
	 */
	public final MenuResponse select(MenuOption option) {
		if(this.menu == null)
			return new MenuResponse(MenuResponseType.MENU_NOT_FOUND);
		
		return menu.select(option);
	}

	/**
	 * Gets the menu of this user interface.
	 * 
	 * @return	this interface's underlying menu.
	 */
	public final Menu getMenu() {
		return menu;
	}
	
	/**
	 * Gets whether this user interface currently has an underlying menu
	 * associated with it.
	 * 
	 * @return	<code>true</code> if this interface is associated with a
	 * 			menu.
	 */
	public final boolean hasMenu() {
		return menu != null;
	}
	
	/**
	 * Gets whether this user interface is open and displaying a stage of
	 * its menu.
	 * 
	 * @return	<code>true</code> if this ui is open.
	 */
	public final boolean isOpen() {
		return hasMenu() && !closed;
	}

	/**
	 * Gets the maximum possible options this user interface can display.
	 * 
	 * @return	This ui's max option slots.
	 */
	public abstract int maxOptions();
	
	
	
	//--------
	//PROTECTED
	//--------
	
	/**
	 * Called when the menu's stage changes and this needs to be reflected
	 * in the ui's display.
	 * 
	 * @param stage		The new stage to be displayed.
	 */
	protected abstract void updateDisplay(MenuStage stage);
	
	/**
	 * Called when the menu enters an inactive state, should close and/or
	 * hide the user interface.
	 */
	protected abstract void onClose();

	
	//--------
	//DEFAULT (Usable by this ui's menu)
	//--------
	
	/**
	 * Closes the user interface.
	 */
	final void close() {
		closed = true;
		
		onClose();
	}
	
	/**
	 * Displays a stage on this user interface. If the interface is closed
	 * and/or hidden, makes it visible again to display the stage.
	 * 
	 * @param stage	The stage to display. If <code>null</code>, closes this
	 * 				user interface.
	 */
	final void displayStage(MenuStage stage) {
		closed = false;
		
		if(stage == null) {
			close();
			
		} else {
			updateDisplay(stage);
		}
		
	}
	
	/**
	 * Sets the menu of this user interface.
	 * <p>
	 * Should only be called from within the context of a menu that is 
	 * currently associating itself from this user interface.
	 * 
	 * @param menu	The menu to display with this interface.
	 */
	final void setMenu(Menu menu) {
		
		//if this interface already has a menu different from the one it is
		// being given, calls a dissociation from the previous menu
		if(this.menu != null && !this.menu.equals(menu))
			menu.removeInterface();
		
		this.menu = menu;
		
		//if the new menu value is null, just removes the old one.
		if(menu == null)
			removeMenu();
			
	}
	
	/**
	 * Dissociates this interface's current menu from it and closes it.
	 * <p>
	 * Should only be called from within the context of a menu that is 
	 * currently dissociating itself from this user interface.
	 */
	final void removeMenu() {
		this.menu = null;
	}
}
