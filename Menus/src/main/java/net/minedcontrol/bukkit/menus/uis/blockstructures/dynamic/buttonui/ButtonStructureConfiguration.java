package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignFormat;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.DisplayStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.MultilingualSignAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.MultilingualSignText;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.ToggleableBlock;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.plugins.LanguageSettings;
import net.minedcontrol.zamalib.plugins.config.Configuration;
import net.minedcontrol.zamalib.plugins.config.YAMLConfigAccessor;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * A configuration for button interaction structures, using a file in the 
 * the subdirectory of the plugin's jar and its data folder.
 * <p>
 * Includes heavy and detailed error throwing for incorrect config 
 * formatting.
 * <p>
 * Makes use of deprecated byte-data values for block variation, though
 * there is no currently available alternative. Configurations may become
 * out of date and result in unintended block appearances as a result.
 * <p>
 * Date Created: Jan 24, 2014
 * 
 * @author Brutus
 *
 */

public class ButtonStructureConfiguration extends Configuration {

	/*
	 * This is not a part of the menus logical model. It is a natively
	 * supported implementation of a UI, but they are not UIs themselves 
	 * and do not necessarily have a place in the MenuManager class.
	 * 
	 * (UIs and Menus, if at all, should be accessible through their
	 * users)
	 * 
	 * Instead, this object or the map of structures should be passed into
	 * the listener that handles them and their activation.
	 * 
	 * TODO Should structures be moved outside of Menus into ZamaLib or their
	 * own library?
	 * 
	 * TODO consider reevaluating this code. Extremely specific and complex, 
	 * not very readable, coded while tired and out of it.
	 * 
	 * TODO make this much more polymorphic, general, etc. when split off
	 * into structures.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * TODO text sign formats and text
	 */

	private static final String FILE_NAME = "buttonstructures.yml";
	//stores it directly inside the plugin's data folder.
	private static final String SUBDIRECTORY = "userinterfaces";

	private MenusPlugin plugin;

	private Map<String, ButtonInteractionStructure> structures;

	private String currentStruct;

	/**
	 * Class constructor.
	 * 
	 * @throws IllegalStateException	if the config file is incorrectly
	 * 									configured/formatted.
	 */
	public ButtonStructureConfiguration()
			throws IllegalStateException {

		super(new YAMLConfigAccessor(Menus.getPlugin(), FILE_NAME, 
				SUBDIRECTORY));

		Zama.debug(Menus.getPlugin(), null, "Loaded button structures: " + structures.size());
	}

	/**
	 * Gets a structure from the configuration with the given id, if any
	 * exists.
	 * 
	 * @param id	The id of the desired structure.
	 * @return		The structure for the given id. Returns <code>null</code>
	 * 				if none is found.
	 */
	public ButtonInteractionStructure getStructure(String id) {
		return structures.get(id);
	}

	@Override
	protected void onLoad() throws IllegalStateException {
		if(plugin == null) plugin = Menus.getPlugin();

		this.structures = new HashMap<String, ButtonInteractionStructure>();


		FileConfiguration config = getConfig();

		ConfigurationSection structsSec = config.getConfigurationSection("structures");
		if(structsSec == null)
			throw new IllegalStateException("Error while loading " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "could not find the 'structures' section");

		Set<String> structIds = structsSec.getKeys(false);
		if(structIds == null || structIds.isEmpty())
			return;

		Set<BlockLocation> definedBlocks = new HashSet<BlockLocation>();

		//for each graph entry, attempts to create a graph from it
		for(String structId : structIds) {
			if(structId == null || structId.equals("")) continue;

			currentStruct = structId;

			ButtonInteractionStructure structure = parseStructure(
					structsSec.getConfigurationSection(structId), definedBlocks);

			if(structure != null)
				structures.put(structId, structure);

		}
	}

	/**
	 * Gets a button interaction structure from a config.
	 * 
	 * @param sec		The config to parse.
	 * @param blocks	The set of blocks that are already part of
	 * 					structures, to avoid two structures using the same
	 * 					block.
	 * @return			The parsed structure.
	 * 
	 * @throws NullPointerException		on a <code>null</code> parameter.
	 * @throws IllegalStateException	if the config section is incorrectly
	 * 									formatted.
	 */
	private ButtonInteractionStructure parseStructure(ConfigurationSection sec, 
			Set<BlockLocation> blocks) 
					throws NullPointerException,IllegalStateException {

		//-- gets the spawn point and world --
		String spawnString = sec.getString("spawnpoint");
		if(spawnString == null || spawnString.equals(""))
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "spawnpoint not found");

		Location spawn = parseLocation(spawnString);
		if(spawn == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "spawnpoint incorrectly defined");

		World world = spawn.getWorld();
		if(world == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "spawnpoint's world not found on server");


		//-- gets the title display substructure --


		//gets the title display sign's format
		SignFormat titleFormat = parseSignFormat(sec.getConfigurationSection("title.format"));

		//gets the title display sign's location and appearance info
		ConfigurationSection titleSignSec = sec.getConfigurationSection("title.sign");
		if(titleSignSec == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "title's sign incorrectly formatted or not found");

		ToggleableBlock titleSign = parseBlock(titleSignSec,  world,
				blocks);

		//gets the location/appearance of the title structure's base
		Set<ToggleableBlock> titleBase = null;
		
		ConfigurationSection titleBaseSec = sec.getConfigurationSection("title.blocks");
		
		if(titleBaseSec != null) {
			titleBase = parseBlocks(titleBaseSec, world,
					blocks);
			
		} else {
			titleBase = new HashSet<ToggleableBlock>();
		}
		
		DisplayStructure title = new DisplayStructure(world, titleSign, titleBase);



		//-- gets the option slots of the structure --

		List<ButtonOptionStructure> slots = new LinkedList<ButtonOptionStructure>();


		ConfigurationSection optionsSec = sec.getConfigurationSection("options");

		//gets the option display signs' format
		SignFormat optFormat = parseSignFormat(optionsSec.getConfigurationSection("format"));

		Set<String> optionKeys = optionsSec.getKeys(false);
		if(optionKeys == null || optionKeys.isEmpty())
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "options not found");

		//for each option sub structure, gets the sign, button, and base
		// and constructs the object
		for(String optionKey : optionKeys) {
			if(optionKey == null || optionKey.equals("format")) continue;

			//gets the section for this option slot
			ConfigurationSection optSec = optionsSec.getConfigurationSection(optionKey);

			//gets the slot's sign
			ConfigurationSection optSignSec = optSec.getConfigurationSection("sign");
			if(optSignSec == null)
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "option " + optionKey + "'s sign "
								+ "incorrectly formatted or not found");

			ToggleableBlock optSign = parseBlock(optSignSec, world, 
					blocks);


			//gets the slot's button
			ConfigurationSection optButtonSec = optSec.getConfigurationSection("button");
			if(optButtonSec == null)
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "option " + optionKey + "'s button "
								+ "incorrectly formatted or not found");

			ToggleableBlock optButton = parseBlock(optButtonSec, world, 
					blocks);

			//gets the slot's base
			ConfigurationSection optBaseSec = optSec.getConfigurationSection("blocks");
			if(optBaseSec == null)
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "option " + optionKey + "'s base blocks"
								+ "incorrectly formatted or not found");

			Set<ToggleableBlock> optBase = parseBlocks(optBaseSec, world,
					blocks);

			slots.add(new ButtonOptionStructure(world, optButton, optSign, optBase));
		}


		//constructs the configured structure
		ButtonInteractionStructure orig = new ButtonInteractionStructure(spawn, 
				title, slots, titleFormat, optFormat);



		//-- gets any copies of the structure --
		//TODO test copying
		ConfigurationSection copiesSec = sec.getConfigurationSection("copies");
		if(copiesSec == null)
			return orig;

		Set<String> copyKeys = copiesSec.getKeys(false);
		if(copyKeys == null || copyKeys.isEmpty())
			return orig;

		for(String copyKey : copyKeys) {

			ConfigurationSection copySec = copiesSec.getConfigurationSection(copyKey);

			String copySpawnString = copySec.getString("spawnpoint");
			if(copySpawnString == null || copySpawnString.equals(""))
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "spawnpoint for copy " + copyKey + " not found");

			Location copySpawn = parseLocation(copySpawnString);
			if(copySpawn == null)
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "spawnpoint copy " + copyKey 
								+ " incorrectly defined");

			World copyWorld = copySpawn.getWorld();
			if(copyWorld == null)
				throw new IllegalStateException("Error while loading the "
						+ "structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ "copy " + copyKey + "'s spawnpoint's "
								+ "world not found on server");

			int xOffset = spawn.getBlockX() - copySpawn.getBlockX();
			int yOffset = spawn.getBlockY() - copySpawn.getBlockY();
			int zOffset = spawn.getBlockZ() - copySpawn.getBlockZ();


			ButtonInteractionStructure copy = orig.copy(copyWorld, 
					xOffset, yOffset, zOffset);

			structures.put(copyKey, copy);
		}


		return orig;
	}

	/**
	 * Gets a sign format from an applicable configuration section.
	 * 
	 * @param sec	The section to parse.
	 * @return		A sign format from the given section.
	 * 				Returns <code>null</code> if no format is defined in
	 * 				the section or if the section is incorrectly formatted.
	 * 
	 * @throws IllegalStateException	on being unable to find a 
	 * 									multilingual message used in the
	 * 									configuration.
	 */
	private SignFormat parseSignFormat(ConfigurationSection sec) 
			throws IllegalStateException {

		if(sec == null)
			return null;
		
		MultilingualMessage[] msgs = new MultilingualMessage[4];
		LanguageSettings msgSrc = Menus.getPlugin().getLanguageSettings();
		
		for(int i = 0; i < 4; i++) {
			String msgId = sec.getString("line" + (i+1));
			
			if(msgId == null || msgId.equals("")) continue;
			
			MultilingualMessage msg = msgSrc.getMultilingualMessage(msgId);
			if(msg == null)
				throw new IllegalStateException("Error while loading a sign "
						+ "format in the structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ " no message with the name '" + msgId 
								+ "' was found in Menus' language configuration");
			
			msgs[i] = msg;
		}

		return new SignFormat(msgs[0], msgs[1], msgs[2], msgs[3]);
	}

	/**
	 * Gets a location from a string version of it.
	 * 
	 * @param spawnString	The string to parse.
	 * @return				The location for the string. <code>null</code>
	 * 						if it cannot be parsed.
	 */
	private Location parseLocation(String str) {
		try {
			String[] arr = (str.trim()).split(",");
			if(arr.length != 6) return null;

			for(int i = 0; i < arr.length; i++) {
				arr[i] = arr[i].trim();
			}

			World world = Bukkit.getWorld(arr[0]);
			if(world == null)
				return null;

			double x = Double.parseDouble(arr[1]);
			double y = Double.parseDouble(arr[2]);
			double z = Double.parseDouble(arr[3]);
			float yaw = Float.parseFloat(arr[4]);
			float pitch = Float.parseFloat(arr[5]);

			return new Location(world, x, y, z, yaw, pitch);
		}
		catch(Exception e) {
			return null;
		}
	}

	/**
	 * Gets a set of toggleable block from an applicable configuration section.
	 *  
	 * @param sec		The section to parse.
	 * @param world		The world the blocks are in.
	 * @param blocks	The blocks defined so far, to prevent duplicates.
	 * @return			The toggleable blocks for the config section.
	 * 
	 * @throws NullPointerException		on a <code>null</code> parameter.
	 * @throws IllegalStateException	if the configuration section is
	 * 									incorrectly formatted/defined,
	 * 									if the block is in an unloaded
	 * 									world, or if a block at the given
	 * 									location was already defined as
	 * 									part of a structure.
	 */
	public Set<ToggleableBlock> parseBlocks(ConfigurationSection sec, 
			World world, Set<BlockLocation> blocks) 
					throws NullPointerException,IllegalStateException {

		Set<ToggleableBlock> ret = new HashSet<ToggleableBlock>();

		Set<String> blockKeys = sec.getKeys(false);
		if(blockKeys == null)
			return ret;

		for(String blockKey : blockKeys) {
			ret.add(parseBlock(sec.getConfigurationSection(blockKey), 
					world, blocks));
		}

		return ret;
	}

	/**
	 * Gets a toggleable block from an applicable configuration section.
	 * 
	 * @param sec		The section to parse.
	 * @param world		The world the block is in.
	 * @param blocks	The blocks defined so far, to prevent duplicates.
	 * @return			The toggleable block for the config section.
	 * 
	 * @throws NullPointerException		on a <code>null</code> parameter.
	 * @throws IllegalStateException	if the configuration section is
	 * 									incorrectly formatted/defined,
	 * 									if the block is in an unloaded
	 * 									world, or if a block at the given
	 * 									location was already defined as
	 * 									part of a structure.
	 */
	public ToggleableBlock parseBlock(ConfigurationSection sec, 
			World world, Set<BlockLocation> blocks) 
					throws NullPointerException,IllegalStateException {

		//gets the materials
		//NOTE: uses paths with 'true' and 'false', because YAML converts 
		// 'on' to true, 'off' to false
		String onMatId = sec.getString("true.material");
		String offMatId = sec.getString("false.material");
		if(onMatId == null || offMatId == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "block with no material encountered");

		Material onMat = Material.valueOf(onMatId.trim().toUpperCase());
		Material offMat = Material.valueOf(offMatId.trim().toUpperCase());
		if(onMat == null || offMat == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "block with invalid encountered");
		
			

		//gets the data
		int onData = sec.getInt("true.data");
		int offData = sec.getInt("false.data");

		//gets the location
		BlockLocation loc = BlockLocation.fromString(world.getName() + ","
				+ sec.getString("location"));

		if(loc == null)
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "invalid block location encountered");

		if(blocks.contains(loc))
			throw new IllegalStateException("Error while loading the "
					+ "structure '" + currentStruct + "' in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
							+ FILE_NAME) + ": "
							+ "encountered a blocklocation that is already"
							+ " in use");
		
		
		BlockAppearance on = null;

		//checks for sign text if the appearances are signs.
		if(onMat == Material.SIGN || onMat == Material.WALL_SIGN) {
			MultilingualSignText text = parseSignText(sec.getConfigurationSection("true.text"));
			if(text != null)
				on = new MultilingualSignAppearance(onMat, (byte)onData, text);
		}
			
		//if a sign appearance was not constructed, constructs a standard one
		if(on == null)
			on = new BlockAppearance(onMat, (byte)onData);
			
		
		BlockAppearance off = null;
		
		if(offMat == Material.SIGN || offMat == Material.WALL_SIGN) {
			MultilingualSignText text = parseSignText(sec.getConfigurationSection("false.text"));
			if(text != null)
				off = new MultilingualSignAppearance(offMat, (byte)offData, text);
		}
			
		//if a sign appearance was not constructed, constructs a standard one
		if(off == null)
			off = new BlockAppearance(offMat, (byte)offData);
		
		
		//creates the toggleable block from the read elements
		return new ToggleableBlock(loc, on, off);
	}

	/**
	 * Gets a multilingual sign text from an applicable configuration 
	 * section.
	 * 
	 * @param sec	The section to parse.
	 * @return		A multilingual sign text from the given section.
	 * 				Returns <code>null</code> if no text is defined in
	 * 				the section or if the section is incorrectly formatted.
	 * 
	 * @throws IllegalStateException	on being unable to find a 
	 * 									multilingual message used in the
	 * 									configuration.
	 */
	private MultilingualSignText parseSignText(ConfigurationSection sec) 
			throws IllegalStateException {
		
		if(sec == null)
			return null;
		

		MultilingualMessage[] msgs = new MultilingualMessage[4];
		LanguageSettings msgSrc = Menus.getPlugin().getLanguageSettings();
		
		for(int i = 0; i < 4; i++) {
			String msgId = sec.getString("line" + (i+1));
			
			if(msgId == null || msgId.equals("")) continue;
			
			MultilingualMessage msg = msgSrc.getMultilingualMessage(msgId);
			if(msg == null)
				throw new IllegalStateException("Error while loading a sign "
						+ "format in the structure '" + currentStruct + "' in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") 
								+ FILE_NAME) + ": "
								+ " no message with the name '" + msgId 
								+ "' was found in Menus' language configuration");
			
			msgs[i] = msg;
		}
		
		
		return new MultilingualSignText(msgs[0], msgs[1], msgs[2], msgs[3]);
	}



}
