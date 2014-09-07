package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks;

import org.bukkit.World;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A two-stage collection of block appearances tied to a specific 
 * location, to toggle between an on and off visual state.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 *
 */

public class ToggleableBlock extends DynamicBlock {
	
	private BlockAppearance on;
	private BlockAppearance off;
	
	/**
	 * Class constructor.
	 * 
	 * @param loc	The location of the block. Not <code>null</code>.
	 * @param on	The appearance of the block in the on state.
	 * 				Not <code>null</code>.
	 * @param off	The appearance of the block in the off state.
	 * 				Not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if any parameter is <code>null</code>.
	 * @throws IllegalStateException	on being unable to access the 
	 * 									packet engine that drives dynamic
	 * 									behavior.
	 */
	public ToggleableBlock(BlockLocation loc, BlockAppearance on, 
			BlockAppearance off) throws IllegalArgumentException,IllegalStateException {
		
		super(loc);
		
		if(on == null || off == null)
			throw new IllegalArgumentException("parameters cannot be null");
		
		this.on = on;
		this.off = off;
	}
	
	/**
	 * Makes this block appear to be in its 'on' state for a player.
	 * 
	 * @param player	The player to change this block's appearance for.
	 */
	public void setOn(ZamaPlayer player) {
		sendBlockUpdate(player, on);
	}
	
	/**
	 * Makes this block appear to be in its 'off' state for a player.
	 * 
	 * @param player	The player to change this block's appearance for.
	 */
	public void setOff(ZamaPlayer player) {
		sendBlockUpdate(player, off);
	}
	
	/**
	 * Gets the appearance of the block for its "on" state.
	 * 
	 * @return	This block's "on" appearance.
	 */
	public BlockAppearance getOnState() {
		return this.on;
	}

	/**
	 * Gets the appearance of the block for its "off" state.
	 * 
	 * @return	This block's "off" appearance.
	 */
	public BlockAppearance getOffState() {
		return this.off;
	}
	
	/**
	 * Sets the appearance of this block for its "on" state.
	 * <p>
	 * Does nothing if <code>null</code> is used.
	 *  
	 * @param newOn	The new appearance.
	 */
	public void setOnState(BlockAppearance newOn) {
		if(newOn == null)
			return;
		
		this.on = newOn;
	}
	
	/**
	 * Sets the appearance of this block for its "off" state.
	 * <p>
	 * Does nothing if <code>null</code> is used.
	 *  
	 * @param newOff	The new appearance.
	 */
	public void setOffState(BlockAppearance newOff) {
		if(newOff == null)
			return;
		
		this.off = newOff;
	}

	@Override
	public ToggleableBlock copy(World world, int xOffset, int yOffset,
			int zOffset) {
		
		if(world == null)
			return null;
		
		BlockLocation l = getLocation();
		BlockLocation offsetLoc = new BlockLocation(world.getName(), 
				l.getX() + xOffset, l.getY() + yOffset, l.getZ() + zOffset);
		
		return new ToggleableBlock(offsetLoc, on, off);
	}
}
