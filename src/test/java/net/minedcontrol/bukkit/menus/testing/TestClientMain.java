package net.minedcontrol.bukkit.menus.testing;

import org.bukkit.plugin.PluginManager;

import net.minedcontrol.bukkit.menus.MenuManager;
import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.listeners.BlockHelpListener;
import net.minedcontrol.bukkit.menus.listeners.ButtonMenuListener;

public class TestClientMain {
	
	public TestClientMain(MenusPlugin plugin) {
		MenuManager manager = Menus.getManager(); // initialize the manager

		//TODO TESTING CODE, REFACTOR
		TestMenuListener tml = new TestMenuListener();
		for(int i = 1; i < 100; i++) {
			String optId = "opt" + (i);
			MenuOption opt = manager.getOption(optId);
			if(opt == null)
				break;
			
			opt.registerListener(tml);
		}
		
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(new BlockHelpListener(plugin), plugin);
		pm.registerEvents(new ButtonMenuListener(plugin), plugin);
	}

}
