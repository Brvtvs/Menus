package net.minedcontrol.bukkit.menus.uis.packetediting;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignUtil;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import net.minedcontrol.zamalib.runtime.master.Zama;

public class WhitelistEngine implements PacketEngine {

	/*
	 * A packet engine that only allows edits that it whitelists first.
	 * 
	 * TODO full documentation
	 */

	private JavaPlugin plugin;

	private WhitelistBlockUpdater blockUpdates;

	/**
	 * Class constructor.
	 * 
	 * @throws IllegalStateException	if unable to find its dependency
	 * 									"ProtocolLib"
	 */
	public WhitelistEngine() throws IllegalStateException {
		if(Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null)
			throw new IllegalStateException("This engine relies on ProtocolLib, which was not found");

		this.plugin = Zama.getMaster().getPlugin();
		this.blockUpdates = new WhitelistBlockUpdater(plugin);
	}


	@Override
	public void sendBlockUpdate(ZamaPlayer player, BlockLocation block,
			BlockAppearance appearance) {

		try {

			if(player == null || block == null || appearance == null)
				throw new NullPointerException();

			blockUpdates.whitelistBlockUpdate(player, block, appearance);

			//debug
			/*Zama.debug(Menus.getPlugin(), null, "sending an update of [" 
					+ appearance.getMaterial().name() + ", " + appearance.getData() 
					+ "] to " + player.getName() + " at " + block.toString());*/

			player.sendBlockChange(block.toLocation(), 
					appearance.getMaterial(), appearance.getData());

		}
		catch(Exception e) {
			Zama.debug(plugin, null, "Encountered an error "
					+ "while attempting to send a block update for " + 
					(player != null ? "player " + player.getName() : 
							"a null player"));
			Zama.debug(plugin, null, e);
		}

	}

	@Override
	public void sendSignTextUpdate( ZamaPlayer player, BlockLocation block,
			SignText text) {

		try {
			if(player == null || block == null || text == null)
				throw new NullPointerException();

			SignUtil.updateSignText(player, block, text);
		}
		catch(Exception e) {
			Zama.debug(plugin, null, "Encountered an error "
					+ "while attempting to send a sign text update for " + 
					(player != null ? "player " + player.getName() : 
							"a null player"));
			Zama.debug(plugin, null, e);
		}
	}


}
