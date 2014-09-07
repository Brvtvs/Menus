package net.minedcontrol.bukkit.menus.uis.blockstructures.blocks;

import net.minedcontrol.bukkit.menus.uis.packetediting.PacketEngine;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

import org.bukkit.Material;

/**
 * A manufactured appearance of a block in a minecraft world.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 *
 */
public class BlockAppearance {
	
	private final Material mat;
	private final byte data;
	
	/**
	 * Class constructor taking a material and its byte data.
	 * 
	 * @param mat	The material of the block.
	 * @param data	The byte data value of the block. <code>0</code> is
	 * 				default.
	 * 
	 * @throws IllegalArgumentException	if the material is <code>null</code>.
	 */
	public BlockAppearance(Material mat, byte data) 
			throws IllegalArgumentException {
		
		if(mat == null)
			throw new IllegalArgumentException("material cannot be null");
		
		this.mat = mat;
		this.data = data;
	}
	
	/**
	 * Uses block update packets to create a 'fake' (client side) version
	 * of this block appearance for a player.
	 * 
	 * @param player	The player to create the fake for.
	 * @param loc		The location to manufacture the appearance.
	 * @param engine	The packet engine used to create the fake.
	 * 	
	 * @throws NullPointerException	on a <code>null</code> parameter.
	 */
	public void createFake(ZamaPlayer player, BlockLocation loc, 
			PacketEngine engine) throws NullPointerException {

		if(player == null || loc == null || engine == null)
			throw new NullPointerException("params cannot be null");
		
		engine.sendBlockUpdate(player, loc, this);
	}
	
	/**
	 * Gets the material of this block appearance. 
	 * 
	 * @return	this block appearance's material.
	 */
	public Material getMaterial() {
		return this.mat;
	}
	
	/**
	 * Gets the byte data of this block appearance. 
	 * 
	 * @return	this block appearance's data value.
	 */
	public byte getData() {
		return this.data;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + ((mat == null) ? 0 : mat.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockAppearance other = (BlockAppearance) obj;
		if (data != other.data)
			return false;
		if (mat != other.mat)
			return false;
		return true;
	}
	
	

}
