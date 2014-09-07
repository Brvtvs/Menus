package net.minedcontrol.bukkit.menus.testing;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.minedcontrol.bukkit.menus.basis.events.MenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.MenuUndoEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuUndoEvent;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;

/**
 * A simple menu listener for use in testing.
 * <p>
 * Date Created: Jan 17, 2014
 * 
 * @author Brutus
 *
 */

public class TestMenuListener implements MenuListener {

	private static final String PREFIX = "[" + ChatColor.RED + "Menu-Testing" 
			+ ChatColor.RESET + "] ";


	@Override
	public void onSelect(MenuSelectEvent event) { }

	@Override
	public void onPlayerSelect(PlayerMenuSelectEvent event) {
		Bukkit.broadcastMessage(PREFIX + "Player " + event.getPlayer().getName() 
				+ " selected option " + ChatColor.AQUA 
				+ event.getSelected().getName().getMessage());
	}

	@Override
	public void onUndo(MenuUndoEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerUndo(PlayerMenuUndoEvent event) {
		Bukkit.broadcastMessage(PREFIX + "Player " + event.getPlayer().getName()
				+ " undid option " + ChatColor.AQUA 
				+ event.getUndone().getName().getMessage());

	}

}
