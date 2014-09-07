package net.minedcontrol.bukkit.menus.uis.packetediting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;


public class WhitelistBlockUpdater {

	/*
	 * A packet listener that cancels all block update packets not 
	 * specifically whitelisted beforehand.
	 * 
	 * Removes an update from the whitelist when it is sent. (one-use)
	 * 
	 * TODO full documentation
	 */
	
	// The current listener
	private PacketAdapter listener;	
	
	//key: bukkit player name (not Zama's)
	//value: any registered differences between the player's view of the
	// world and the server's. In this context these are the only changes
	// that are allowed to be sent to the player.
	private Map<String, PlayerVariance> variances;


	public WhitelistBlockUpdater(Plugin plugin) 
			throws IllegalArgumentException {

		if(plugin == null)
			throw new IllegalArgumentException("plugin cannot be null");

		this.variances = new HashMap<String, PlayerVariance>();
		
		registerListener(plugin);

	}


	/**
	 * Whitelists block updates for a block if they match the given
	 * appearance. 
	 * <p>
	 * Does nothing on a <code>null</code> parameter.
	 * 
	 * @param player		The player the whitelisting is for. 
	 * @param loc			The location of the block. 
	 * @param appearance	The appearance to whitelist.
	 */
	public void whitelistBlockUpdate(ZamaPlayer player, BlockLocation loc,  
			BlockAppearance appearance) {

		if(loc == null || player == null || appearance == null)
			return;

		PlayerVariance var = variances.get(player.getBukkitName());
		if(var == null) {
			var = new PlayerVariance(player);
			variances.put(player.getBukkitName(), var);
		}
		
		var.addPlayerVersion(loc, appearance);
	}
	
	/**
	 * Gets the block update packet listener.
	 * 
	 * @return The block update listener.
	 */
	public PacketAdapter getListener() {
		return this.listener;
	}
	

	//TODO add more packets like MULTI_BLOCK_CHANGE, (?)block updates like a button being pressed?
	private void registerListener(Plugin plugin) {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				listener = new PacketAdapter(plugin, PacketType.Play.Server.BLOCK_CHANGE) {
					@Override
					public void onPacketSending(PacketEvent event) {
						
						Player player = event.getPlayer();
						
						PacketContainer packet = event.getPacket();

						StructureModifier<Integer> ints = packet.getIntegers();
						String worldName = player.getWorld().getName();
						BlockLocation loc = new BlockLocation(worldName, 
								ints.read(0), ints.read(1), ints.read(2));

						StructureModifier<Material> mats = packet.getBlocks();
						Material mat = mats.read(0);
						
						int data = ints.read(3);
						
						//if the packet is not whitelisted, cancels it
						if(!isBlockWhitelisted(player.getName(), loc, mat, 
								data, true))
							
							event.setCancelled(true);
					}	
				});
	}
	
	/**
	 * Gets whether a block in its given form is in the whitelist.
	 * 
	 * @param playerName	The name of the player to check for.
	 * @param loc			The location of the block.
	 * @param mat			The material of the block's form.
	 * @param data			The data of the block's form.
	 * @param remove		Whether to remove the block from the whitelist
	 * 						if it was there.
	 * @return				<code>true</code> if the form of the block is
	 * 						whitelisted. <code>false</code> if not or a
	 * 						<code>null</code> parameter is used.
	 */
	private boolean isBlockWhitelisted(String playerName, BlockLocation loc, 
			Material mat, int data, boolean remove) {
		
		if(playerName == null || loc == null || mat == null)
			return false;
		
		PlayerVariance var = variances.get(playerName);
		if(var == null)
			return false;
		
		BlockAppearance app = var.getPlayerVersion(loc);
		if(app == null) // nothing specifically defined, not whitelisted
			return false;
		
		boolean whitelisted = (app.getMaterial() == mat && app.getData() == data);
		
		if(whitelisted && remove)
			var.removePlayerVersion(loc);
		
		return whitelisted;
	}
	





}
