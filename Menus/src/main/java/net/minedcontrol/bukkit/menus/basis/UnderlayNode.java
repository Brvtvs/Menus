package net.minedcontrol.bukkit.menus.basis;


/**
 * A node in an underlay that provides the current menu stage of the 
 * underlay and the next node for a given selection from that stage.
 * <p>
 * This is a generic definition of a portion of an underlay that a 
 * <code>Menu</code> object will use to iterate/traverse over the underlay.
 * <p>
 * Date Created: Jan 15, 2014
 * 
 * @author Brutus
 *
 */

public interface UnderlayNode {
	
	/**
	 * Gets the contents of this node, the options and appearance 
	 * information for this node of the underlay.
	 * 
	 * @param menu		The menu making the request. Not <code>null</code>.
	 * @return			This node's contents.
	 */
	public MenuStage getMenuStage(Menu menu);
	
	/**
	 * Gets the next node, based on a selection from the options of this
	 * node of the underlay.
	 * 
	 * @param menu		The menu making the request. Not <code>null</code>.
	 * @param selection	The selection for this stage of the underlay. Must 
	 * 					be one of the options for this node and not 
	 * 					<code>null</code>.
	 * @return			The next node, based on the selection that was 
	 * 					made. Returns <code>null</code> if there is no
	 * 					child node for that selection (the menu is
	 * 					completed).
	 * 
	 * @throws IllegalArgumentException	If the selection is not one of the
	 * 									options of this stage, including if
	 * 									it is a <code>null</code> value.
	 */
	public UnderlayNode getNext(Menu menu, MenuOption selection) 
			throws IllegalArgumentException;

}
