package net.minedcontrol.bukkit.menus.uis.blockstructures.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;

import net.minedcontrol.bukkit.menus.uis.blockstructures.CopyableStructure;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;

/**
 * A very generic abstract class that serves as the basis for many blocks
 * that can change their appearance in potentially many different ways.
 * <p>
 * Date Created: Jan 24, 2014
 * 
 * @author Brutus
 *
 */
public abstract class ChangeableBlock implements CopyableStructure {
	
	private final BlockLocation loc;
	
	/**
	 * Class constructor.
	 * 
	 * @param loc	The location of the block. Cannot be changed 
	 * 				post-construction. Not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public ChangeableBlock(BlockLocation loc) 
			throws IllegalArgumentException {
		
		if(loc == null)
			throw new IllegalArgumentException("location cannot be null");
		
		this.loc = loc;
	}
	
	/**
	 * Gets the location of this block.
	 * 
	 * @return	This block's location.
	 */
	public BlockLocation getLocation() {
		return this.loc;
	}
	
	/**
	 * Gets the real status of this block, if it currently exists on the
	 * server.
	 * 
	 * @return	The real appearance of the block. Returns <code>null</code>
	 * 			if the block does not actually exist in the server's
	 * 			current state.
	 */
	@SuppressWarnings("deprecation")
	public BlockAppearance getRealAppearance() {
		Location l = loc.toLocation();
		
		if(l == null)
			return null;
		
		Block block = l.getBlock();
		if(block == null)
			return null;
		
		return new BlockAppearance(block.getType(), block.getData());
	}

}
