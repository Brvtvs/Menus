package net.minedcontrol.bukkit.menus.basis.events;

import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;

public interface MenuEvent {
	
	/**
	 * A method to allow implementations of MenuEvents to interact with
	 * a listener in their own specific, polymorphic ways.
	 * 
	 * @param listener	The listener to (potentially) call.
	 */
	public void call(MenuListener listener);

}
