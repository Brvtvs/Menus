package net.minedcontrol.bukkit.menus.uis.framework;

import net.minedcontrol.zamalib.bukkit.util.colors.ColorScheme;


/**
 * A menu element that can be translated to a color (in various media) for 
 * a user interface.
 * <p>
 * Date Created: Jan 17, 2014
 * 
 * @author Brutus
 *
 */

public interface Colorable {
	
	/**
	 * Gets the display color for the element.
	 * 
	 * @return	The element's display color. Returns <code>null</code> if
	 * 			none was defined.
	 */
	public ColorScheme getColor();

}
