package net.minedcontrol.bukkit.menus.listeners;

import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener that enables listing of data about a selected block for 
 * players with the correct permissions.
 * <p>
 * Date Created: Jan 13, 2014
 * 
 * @author Brutus
 *
 */

public class BlockHelpListener implements Listener {

	private static final String PERMISSION = "menus.bowlhelper";
	
	//private final PluginMain plugin;
	
	/**
	 * Class constructor.
	 * 
	 * @param plugin	The plugin main.
	 */
	public BlockHelpListener(MenusPlugin plugin) {
		//this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player p = event.getPlayer();
		if(p == null || !p.hasPermission(PERMISSION))
			return;
		
		Block block = event.getClickedBlock();
		
		if(p.getItemInHand().getType() != Material.BOWL)
			return;
		
		p.getPlayer().sendMessage("Debug: block = " + block.getType() 
				+ ", data = " + block.getData() + ", loc = " 
				+ (new BlockLocation(block.getLocation()).toString()));
	}
}
