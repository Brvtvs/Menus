package net.minedcontrol.bukkit.menus.basis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A set of elements that represent a single, atomic, unchanging stage of a 
 * menu, what the user may see at a given time.
 * <p>
 * Cannot be edited after construction, representing an unchanging state
 * of the menu that can be repeatedly accessed over time, though it does
 * NOT guarantee consistency with the menu underlay, which may undergo
 * changes in that time.
 * <p>
 * Makes use of a hidden index system, enabling interfaces to interact with
 * the elements using indices without fear of null pointers in getting the 
 * list of options or list mutation.
 * <p>
 * Date Created: Jan 13, 2014
 * 
 * @author Brutus
 *
 */

public class MenuStage {

	private final MenuElement title;
	private final List<MenuOption> options;
	
	/**
	 * Class constructor.
	 * 
	 * @param title		The title of this stage, not <code>null</code>.
	 * @param options	The options of this stage, not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>.
	 */
	public MenuStage(MenuElement title, List<MenuOption> options) {
		if(title == null || options == null) 
			throw new IllegalArgumentException("params cannot be null");
		
		this.title = title;
				
		this.options = new ArrayList<MenuOption>(options);
		this.options.removeAll(Collections.singleton(null));
	}
	

	/**
	 * Gets a copied, ordered list of the options for this stage. The 
	 * indices of the list's initial state can be used to access option
	 * objects through this stage using {@link #getOption(int)}.
	 * 
	 * @return	This stage's options.
	 */
	public final List<MenuOption> getOptions() {
		return new ArrayList<MenuOption>(options);
	}
	
	/**
	 * Gets whether this stage contains a given option.
	 * 
	 * @param option	The option to check.
	 * @return			<code>true</code> if the stage contains this option.
	 */
	public final boolean contains(MenuOption option) {
		return options.contains(option);
	}

	/**
	 * Gets the current title, which may signify what the current choice of 
	 * options represent, or the name of the menu's role as a whole.
	 * 
	 * @return	The menu's current title.
	 */
	public final MenuElement getTitle() {
		return this.title;
	}
	
	
	/**
	 * Gets the option for a given index, with the first index being 0
	 * and the indices being the same as in the accessible list.
	 * 
	 * @param index	The index of the desired element.
	 * @return		The element at the given index.
	 * 
	 * @throws IndexOutOfBoundsException	if the index is out of range or
	 * 										negative.
	 */
	public final MenuOption getOption(int index) throws IndexOutOfBoundsException {
		return options.get(index);
	}
	
	/**
	 * Gets the index of a given option as it appears in this collection.
	 * 
	 * @param option	The option to get the index of.
	 * @return			The index of the option. Returns <code>-1</code> if
	 * 					the option is not found in this collection.
	 */
	public final int indexOf(MenuOption option) {
		return options.indexOf(option);
	}

	/**
	 * Gets the number of options in this collection.
	 * 
	 * @return	The number of options.
	 */
	public final int numOptions() {
		return options.size();
	}

}
