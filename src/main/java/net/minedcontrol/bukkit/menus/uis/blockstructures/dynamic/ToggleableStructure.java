package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.World;

import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.ToggleableBlock;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A dynamic block structure that can toggle between two states, an 'on' 
 * and an 'off' state. With the exception of processing user interaction
 * with elements of the structure, there is no need for either the 'on' or
 * off state to represent the 'real' server-side state of the component
 * blocks.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 * 
 * @see DynamicStructure
 */
public class ToggleableStructure extends DynamicStructure {

	protected World world;
	protected Set<ToggleableBlock> blocks;

	/**
	 * Class constructor.
	 * 
	 * @param blocks	A map of the blocks that make up the structure 
	 * 					and should be toggled on/off with this structure,
	 * 					with how they should appear in each state as
	 * 					the mapped values. 
	 * 					Not <code>null</code> but the blocks themselves can 
	 * 					exist in any state (including as air) in the 
	 * 					minecraft world.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>, or if the 
	 * 									parameters are not located in the
	 * 									same world.
	 */
	public ToggleableStructure(World world, Set<ToggleableBlock> blocks) {
		if(blocks == null || world == null)
			throw new IllegalArgumentException("blocks cannot be null");

		for(ToggleableBlock tb : blocks) {
			if(!world.equals(tb.getLocation().toLocation().getWorld())) 
				throw new IllegalArgumentException("the block locations "
						+ "must be in the same world as the structure.");
		}

		this.world = world;
		this.blocks = blocks;
	}


	//--------
	//PUBLIC
	//--------

	/**
	 * Gets the locations and states of the blocks of this structure.
	 * 
	 * @return	This structure's blocks.
	 */
	public Set<ToggleableBlock> getBlocks() {
		return new LinkedHashSet<ToggleableBlock>(blocks);
	}

	/**
	 * Sends update packets to the player's client to make them see the
	 * 'on' appearance of this structure.
	 * <p>
	 * Does nothing if the player and this structure are not in the same
	 * world.
	 * 
	 * @param player	The player to display the 'on' block appearances to.
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if <code>null</code> is used.
	 */
	public void setOn(ZamaPlayer player) {
		if(!player.getWorld().equals(world))
			return;

		for(ToggleableBlock tb : blocks) {
			setBlock(player, tb, true);
		}
	}

	/**
	 * Sends update packets to the player's client to make them see the
	 * 'on' appearance of this structure.
	 * <p>
	 * Does nothing if the player and this structure are not in the same
	 * world.
	 * 
	 * @param player	The player to display the 'off' block appearances to.
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if <code>null</code> is used.
	 */
	public void setOff(ZamaPlayer player) {
		if(!player.getWorld().equals(world))
			return;

		for(ToggleableBlock tb : blocks) {
			setBlock(player, tb, false);
		}
	}


	//--------
	//PROTECTED
	//--------

	/**
	 * Sets a block to its on or off state/appearance for a player.
	 * 
	 * @param player	The player to make the change for.
	 * @param block		The block to toggle.
	 * @param on		Whether to set the block on or off.
	 */
	protected static void setBlock(ZamaPlayer player, ToggleableBlock block, boolean on) {
		if(on)  block.setOn(player);
		else block.setOff(player);
	}


	@Override
	public ToggleableStructure copy(World world, int xOffset, int yOffset,
			int zOffset) {

		if(world == null)
			return null;
		
		Set<ToggleableBlock> offsetBlocks = new HashSet<ToggleableBlock>();
		for(ToggleableBlock tb : blocks) {
			if(tb == null) continue;
			offsetBlocks.add(tb.copy(world, xOffset, yOffset, zOffset));
		}
		
		return new ToggleableStructure(world, offsetBlocks);
		
	}



}
