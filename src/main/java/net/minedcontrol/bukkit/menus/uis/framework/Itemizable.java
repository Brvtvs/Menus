package net.minedcontrol.bukkit.menus.uis.framework;

import org.bukkit.inventory.ItemStack;

/**
 * A menu element that can be translated to a minecraft item for various
 * user interfaces, such as an item menu or an item frame menu.
 * <p>
 * Date Created: Jan 17, 2014
 * 
 * @author Brutus
 *
 */

public interface Itemizable {
	
	/**
	 * Gets the item that can be used to display this element.
	 * 
	 * The item is generally a way of displaying the element that does not 
	 * include the text of the element, because the element's text must be
	 * dealt with multilingually, and the appropriate language is not
	 * known by the element when getting its item.
	 * 
	 * @return	An item stack that can represent the element, sans any
	 * 			language-specific text metadata. Returns <code>null</code> 
	 * 			if none was defined.
	 */
	public ItemStack getItem();
	
}
