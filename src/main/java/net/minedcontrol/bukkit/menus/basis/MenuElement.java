package net.minedcontrol.bukkit.menus.basis;

import org.bukkit.inventory.ItemStack;

import net.minedcontrol.bukkit.menus.uis.framework.Colorable;
import net.minedcontrol.bukkit.menus.uis.framework.Itemizable;
import net.minedcontrol.zamalib.bukkit.util.colors.ColorScheme;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;

/**
 * An element of a menu that contains basic information for how to display
 * it to a user.
 * <p>
 * Date Created: Feb 3, 2014
 * 
 * @author Brutus
 *
 */
public class MenuElement implements Colorable,Itemizable{
	
	private final MultilingualMessage name;
	private final MultilingualMessage desc;
	
	private final ItemStack displayItem;
	private final ColorScheme displayColor;
	
	/**
	 * Class constructor.
	 * 
	 * @param name			The name of the element. Should be relatively 
	 * 						short, as many user interfaces may have very 
	 * 						limited space to display the name and it will
	 * 						be truncated if it cannot fit.
	 * 						Not <code>null</code>.
	 * @param desc			The longer, more detailed description of the
	 * 						element. Not <code>null</code>.
	 * @param displayItem	An item that can be used to display this 
	 * 						element in a user interface, such as an 
	 * 						inventory menu, if any. Can be 
	 * 						<code>null</code> for none.
	 * @param displayColor	A color that can be used when displaying this 
	 * 						element in a user interface, such as an 
	 * 						inventory menu using wool blocks, if any. Can 
	 * 						be <code>null</code> for none.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> name or 
	 * 									description.
	 */
	public MenuElement(MultilingualMessage name, MultilingualMessage desc,
			ItemStack displayItem, ColorScheme displayColor) 
					throws IllegalArgumentException{
		
		if(name == null || desc == null)
			throw new IllegalArgumentException("params cannot be null.");
		
		this.name = name;
		this.desc = desc;
		
		this.displayItem = displayItem;
		this.displayColor = displayColor;
	}
	
	/**
	 * Gets the name of this option. Depending on the display, the name of
	 * the option is likely to be truncated if it is too long. 
	 * 
	 * @return	The name of this option.
	 */
	public MultilingualMessage getName() {
		return this.name;
	}
	
	/**
	 * Gets the more detailed description of this option.
	 * 
	 * @return	The option's description.
	 */
	public MultilingualMessage getDescription() {
		return this.desc;
	}

	@Override
	public ItemStack getItem() {
		return this.displayItem;
	}

	@Override
	public ColorScheme getColor() {
		return this.displayColor;
	}
	
}
