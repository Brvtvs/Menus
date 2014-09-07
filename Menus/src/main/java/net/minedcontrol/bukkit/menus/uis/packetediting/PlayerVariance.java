package net.minedcontrol.bukkit.menus.uis.packetediting;

import java.util.HashMap;
import java.util.Map;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

public class PlayerVariance {
	/*
	 * A collection of the differences between how a player should/does
	 * see the world differently from how it actually exists server-side.
	 * 
	 * TODO full documentation
	 */

	//should be created on player login (if the engine is functioning) and
	// abandoned on player logout. 
	// Would either need to be refreshed or totally redone on relogging in.
	// probably be a better idea for the things making the variance to
	// refresh them as appropriate on login/logout, rather than storing the
	// actual variances persistently


	//try to avoid unnecessary creation of objects when checking packets
	// to see if they are whitelisted (inefficient). Make complex, but
	// hidden code to do less objective, but still safe comparisons with 
	// simple API.
	

	//TODO what about when the player is offline? Add checks for that
	// that affect the return values?
	private ZamaPlayer player;
	
	//TODO add more types of variance, potentially use more advanced
	// types of storing them. Expiries? What they were before? Sign text?...
	private Map<BlockLocation, BlockAppearance> blocks;


	/**
	 * Class constructor.
	 * 
	 * @param player	The player this variance is for, not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public PlayerVariance(ZamaPlayer player) throws IllegalArgumentException {
		if(player == null)
			throw new IllegalArgumentException("player cannot be null");
		
		this.player = player;
		this.blocks = new HashMap<BlockLocation, BlockAppearance>();
	}
	
	/**
	 * Gets the player this variance is for.
	 * 
	 * @return	This variance's player.
	 */
	public ZamaPlayer getPlayer() {
		return this.player;
	}

	/**
	 * Gets the player-specific version of the block at a location, if any.
	 * 
	 * @param loc	The location to check. Not <code>null</code>.
	 * @return		The player specific version of the block at the location.
	 * 				Returns <code>null</code> if none was found.
	 */
	public BlockAppearance getPlayerVersion(BlockLocation loc)  {
		
		if(loc == null)
			throw new NullPointerException("null location used");
		
		return blocks.get(loc);
	}
	
	/**
	 * Adds a player-specific version of the block at a location. Overrides
	 * any previously set versions.
	 * 
	 * @param loc			The location of the block.
	 * @param appearance	How the block should appear.
	 */
	public void addPlayerVersion(BlockLocation loc, BlockAppearance appearance) {
		if(loc == null || appearance == null)
			return;
		
		blocks.put(loc, appearance);
	}
	
	/**
	 * Removes the player-specific version of the block at a location, if any.
	 * 
	 * @param loc	The location of the block. Not <code>null</code>.
	 */
	public void removePlayerVersion(BlockLocation loc) {
		blocks.remove(loc);
	}




}
