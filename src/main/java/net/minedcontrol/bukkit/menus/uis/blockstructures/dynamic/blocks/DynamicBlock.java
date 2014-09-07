package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.ChangeableBlock;
import net.minedcontrol.bukkit.menus.uis.packetediting.PacketEngine;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A changeable block that edits appearances per-user through fake packets
 * and packet editing.
 * <p>
 * Date Created: Jan 24, 2014
 * 
 * @author Brutus
 *
 * @see ChangeableBlock
 */
public abstract class DynamicBlock extends ChangeableBlock {

	private final PacketEngine engine;

	/**
	 * Class constructor.
	 * 
	 * @param loc		The location of the block. Cannot be changed 
	 * 					post-construction. Not <code>null</code>.
	 * @param engine	The engine to send/edit packets through.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 * @throws IllegalStateException	on being unable to access the 
	 * 									packet engine that drives dynamic
	 * 									behavior.
	 */
	public DynamicBlock(BlockLocation loc) 
			throws IllegalArgumentException,IllegalStateException {
		super(loc);
		
		//TODO convert this reference to the final version
		this.engine = Menus.getManager().getPacketEngine();

		if(engine == null)
			throw new IllegalStateException("could not access the packet engine");

	}




	/**
	 * Sends a block update to a player.
	 * 
	 * @param player		The player to send the update to.
	 * @param loc			The location of the block to update.
	 * @param appearance	The appearance to update the block to.
	 * 
	 * @throws NullPointerException	on a <code>null</code> parameter.
	 */
	protected void sendBlockUpdate(ZamaPlayer player,
			BlockAppearance appearance) throws NullPointerException {
		
		appearance.createFake(player, getLocation(), engine);
	}

}
