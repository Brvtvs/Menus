package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic;

import net.minedcontrol.bukkit.menus.uis.blockstructures.BlockStructure;

/**
 * A collection of blocks whose state can be altered dynamically per-client 
 * without editing the actual server-side version of the blocks.
 * <p>
 * Allows for the creation of per-player physical structures in a minecraft
 * world for things like physically represented GUIs.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 *
 * @see BlockStructure
 */

public abstract class DynamicStructure extends BlockStructure {
	
	/*
	 * Currently has no actual generic implementation as there is no
	 * currently shared aspects of the toggleable extension of this
	 * idea and any other implementation, but this does represent a logical
	 * parent/superclass of a toggleable structure. (January 10, 2014)
	 */

}
