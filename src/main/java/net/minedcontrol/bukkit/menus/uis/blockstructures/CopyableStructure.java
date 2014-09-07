package net.minedcontrol.bukkit.menus.uis.blockstructures;

import org.bukkit.World;

/**
 * An interface for coordinate-based objects that can be copied, either
 * in place or offset by a user-defined amount.
 * <p>
 * Date Created: Jan 25, 2014
 * 
 * @author Brutus
 *
 */

public interface CopyableStructure {
	
	/**
	 * Gets a copy of this object in the desired world, with its
	 * coordinates offset by the defined amounts.
	 * 
	 * @param world		The world the copy should be in.
	 * @param xOffset	How far to offset the <code>x</code> coordinate.
	 * @param yOffset	How far to offset the <code>y</code> coordinate.
	 * @param zOffset	How far to offset the <code>z</code> coordinate.
	 * 
	 * @return			A copy of the object in the defined world, offset
	 * 					appropriately. Returns <code>null</code> on
	 * 					a <code>null</code> parameter.
	 */
	public CopyableStructure copy(World world, int xOffset, int yOffset, int zOffset);

}
