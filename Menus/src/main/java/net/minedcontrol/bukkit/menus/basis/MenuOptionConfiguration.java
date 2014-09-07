package net.minedcontrol.bukkit.menus.basis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.plugins.config.Configuration;
import net.minedcontrol.zamalib.plugins.config.YAMLConfigAccessor;

/**
 * A configuration for menu options, using a file in the plugin jar's home 
 * directory and the plugin's data folder, as well as referencing the 
 * plugin's configured multilingual messages.
 * <p>
 * Includes heavy and detailed error throwing for incorrect config 
 * formatting.
 * <p>
 * Date Created: Jan 21, 2014
 * 
 * @author Brutus
 *
 */
public class MenuOptionConfiguration extends Configuration {

	private static final String FILE_NAME = "options.yml";
	//stores it directly inside the plugin's data folder.
	private static final String SUBDIRECTORY = null;

	private Map<String, MenuOption> options;

	/**
	 * Class constructor. Also loads the configuration.
	 * 
	 * @throws IllegalStateException	If the options configuration is
	 * 									incorrectly defined/formatted.
	 */
	public MenuOptionConfiguration() throws IllegalStateException {

		super(new YAMLConfigAccessor(Menus.getPlugin(), FILE_NAME, 
				SUBDIRECTORY));
	}
	
	/**
	 * Gets an option from the configured map.
	 * 
	 * @param id	The unique string identified of the option.
	 * @return		The menu option for the given id, if any is found.
	 * 				Returns <code>null</code> if not found.
	 */
	public MenuOption getOption(String id) {
		return options.get(id);
	}

	@Override
	protected void onLoad() throws IllegalStateException {
		
		this.options = new HashMap<String, MenuOption>();
		
		MenusPlugin plugin = Menus.getPlugin();
		FileConfiguration config = getConfig();

		ConfigurationSection optionSec = config.getConfigurationSection("options");
		if(optionSec == null)
			throw new IllegalStateException("Error loading " 
					+ FILE_NAME + ". Could not find the 'options' section");

		Set<String> optIds = optionSec.getKeys(false);
		if(optIds == null)
			return;

		//for each option defined, attempts to get a name and description
		// message for it and add it to the map.
		for(String optId : optIds) {
			if(optId == null || optId.equals("")) continue;
			
			String nameId = optionSec.getString(optId + ".name");
			String descId = optionSec.getString(optId + ".description");

			if(nameId == null || descId == null || nameId.equals("") 
					|| descId.equals(""))
				throw new IllegalStateException("Error loading " 
					+ FILE_NAME + ". Option " + optId + "is not correctly"
							+ " configured");

			MultilingualMessage name = plugin.getLanguageSettings().getMultilingualMessage(nameId);
			if(name == null)
				throw new IllegalStateException("Error loading " 
						+ FILE_NAME + ". Option " + optId 
						+ " could not be loaded, no message with the "
						+ "name '" + nameId + "' was found in Menus' "
						+ "language configuration");

			MultilingualMessage desc = plugin.getLanguageSettings().getMultilingualMessage(descId);
			if(desc == null)
				throw new IllegalStateException("Error loading " 
						+ FILE_NAME + ". Option " + optId 
						+ " could not be loaded, no message with the "
						+ "name '" + descId + "' was found in Menus' "
						+ "language configuration");
			
			//TODO add ability to define colors and/or items for display
			options.put(optId, new MenuOption(optId, name, desc, null, null));
		}

	}

}
