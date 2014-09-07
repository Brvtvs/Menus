package net.minedcontrol.bukkit.menus.uis.packetediting;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

public interface PacketEngine {
	
	/*
	 * An interface for different implementations of ways/models of sending 
	 * packets like block and sign updates and potentially maintaining
	 * those changes.
	 * 
	 * Probably will be moved to its own plugin.
	 * 
	 */
	
	/**
	 * Sends a fake block update to a player. Behavior after the update
	 * depends on the implementation and settings of the engine.
	 * 
	 * @param player		The player to send the update to.
	 * @param block			The location of the block to update.
	 * @param appearance	The way the block should look.
	 */
	public void sendBlockUpdate(ZamaPlayer player, BlockLocation block, BlockAppearance appearance);
	
	/**
	 * Sends a fake block update to a player for the text on a sign,
	 * Does not ensure that the block is already displayed to the player as
	 * a sign. Behavior after the update depends on the implementation and 
	 * settings of the engine.
	 * <p>
	 * WARNING: If a sign update is sent and the block is not already a
	 * sign, it may crash their client.
	 * 
	 * @param player		The player to send the update to.
	 * @param block			The location of the block to update.
	 * @param appearance	The way the sign should look.
	 */
	public void sendSignTextUpdate(ZamaPlayer player, BlockLocation block, 
			SignText text);

}
