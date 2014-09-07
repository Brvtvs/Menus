package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.basis.MenuElement;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignFormat;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.DisplayStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.DynamicStructure;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.messaging.LanguageChoice;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * An in-game construct that is represented by a series of dynamic structures
 * comprised of a sign and a base of miscellaneous blocks (and a button in
 * the case of the menu options). Can be used to dynamically alter the
 * appearance of the component blocks and signs in order to represent the
 * content of a menu per-player.
 * <p>
 * The interface is represented by a "physical" set of blocks in the
 * minecraft world, but is edited totally per-client and can exist in a 
 * different state for each player observing it by sending fake block and 
 * sign-text updates to their client. 
 * <p>
 * This is only used as a logical structure to construct sets of block 
 * updates to send to the client and stores no information about what a 
 * player is/should be seeing after the block updates are sent. However, 
 * options are always displayed in the same order that they are passed in 
 * and it is possible to reverse engineer what option a player tried to 
 * interact with by correlating the index of the sign/button representing 
 * the option to the index of the option in their current menu's option set. 
 * <p>
 * This is essentially a shortcut to translate the static, multi-viewer 
 * minecraft world into a dynamic, per-viewer state. It is not a user
 * interface per se, but instead an organizational tool for user interfaces
 * to take advantage of. It allows a user interface to translate player
 * interaction with blocks into selection, to send fake block updates
 * en masse, and to allow multiple users to use the same structure for
 * their own individual menu interfaces simultaneously. 
 * <p>
 * Date Created: Jan 13, 2014
 * 
 * @author Brutus
 *
 */
public class ButtonInteractionStructure extends DynamicStructure {


	//a base location for this structure, to be used in teleporting players
	// to/around this structure
	private Location loc;

	private DisplayStructure titleStructure;
	//an ordered list of the option structures, to use and occupy them in
	// the same order they were passed in.
	private LinkedList<ButtonOptionStructure> structures;

	//maps of the structures' list indexes keyed by the location of their 
	// elements that players can interact with. Sacrifices memory 
	// requirements and construction time in exchange for fast hash access 
	// by block location.
	private Map<BlockLocation, Integer> buttonIndexes, signIndexes;

	//the text format for the display signs of this structure
	private SignFormat titleFormat, optionFormat;

	/**
	 * Class constructor.
	 * 
	 * @param location			The base location of this structure, for
	 * 							use in teleporting players to/around this
	 * 							structure.
	 * 							Not <code>null</code>.
	 * @param title				The structure representing and displaying 
	 * 							the title.
	 * 							Not <code>null</code>.
	 * @param optionStructures	The set of structures in which the options
	 * 							will be displayed and interacted with.
	 * 							Not <code>null</code>.
	 * @param titleFormat		The format of the title display sign.
	 * 							Can be <code>null</code> for no defined
	 * 							formatting.
	 * @param optionFormat		The format of the option display sign.
	 * 							Can be <code>null</code> for no defined
	 * 							formatting.
	 * 
	 * @throws IllegalArgumentException	on an incorrectly <code>null</code>
	 * 									parameter.
	 */
	public ButtonInteractionStructure(Location location, DisplayStructure title, 
			List<ButtonOptionStructure> optionStructures, 
			SignFormat titleFormat, SignFormat optionFormat) 
					throws IllegalArgumentException {

		if(location == null || title == null || optionStructures == null)
			throw new IllegalArgumentException("params cannot be null");

		this.loc = location;
		this.titleStructure = title;
		
		if(titleFormat == null)
			titleFormat = new SignFormat(null, null, null, null);
		this.titleFormat = titleFormat;
		
		if(optionFormat == null)
			optionFormat = new SignFormat(null, null, null, null);
		this.optionFormat = optionFormat;

		//creates a set of the structures with the same ordering as the
		// passed in list.
		structures = new LinkedList<ButtonOptionStructure>();
		buttonIndexes = new HashMap<BlockLocation, Integer>(); 
		signIndexes = new HashMap<BlockLocation, Integer>();

		int index = 0;
		for(ButtonOptionStructure bos : optionStructures) {
			if(bos != null && !structures.contains(bos)) {
				structures.add(bos);

				buttonIndexes.put(new BlockLocation(bos.getButton().getLocation()), index);
				signIndexes.put(new BlockLocation(bos.getSign().getLocation()), index);

				index++;
			}
		}
	}

	/**
	 * Displays a set of options on the option-slots of this structure
	 * using fake block and sign-text updates. Toggles any option-slots it 
	 * will use to display options to their "on" state and any unused 
	 * option-slots to their "off" state.
	 * 
	 * @param player	The player to send the sign update to. 
	 * 					Not <code>null</code>.
	 * @param options	An ordered list of the options to display.
	 * 
	 * @throws NullPointerException	if player or list are <code>null</code>.
	 */
	public void displayOptions(ZamaPlayer player, List<MenuOption> options) {

		LanguageChoice lang = player.getLanguagePreference();

		//Iterates in order over the structure list. Displays each option
		// for the player, and then turns off the rest. Will truncate the
		// options list if there are not enough structures.
		for(int i = 0; i < structures.size(); i++) {

			ButtonOptionStructure bos = structures.get(i);

			// displays the next option on the structure if there are more 
			// options.
			if(i < options.size()) { 
				MenuOption option = options.get(i);

				bos.setOn(player);
				bos.updateDisplay(player, optionFormat.getSignText(option.getName(), lang));

			} else { // turns off the unused structures
				bos.setOff(player);
			}
		}
	}

	/**
	 * Displays a title on the title display sign of this structure for
	 * a player using a fake sign-text update. Toggles the title section
	 * of the structure "on" in order to display the text.
	 * 
	 * @param player	The player to send the sign update to. 
	 * 					Not <code>null</code>.
	 * @param title		The text to display.
	 * 
	 * @throws NullPointerException	if player or title are <code>null</code>.
	 */
	public void displayTitle(ZamaPlayer player, MenuElement title) {

		LanguageChoice lang = player.getLanguagePreference();
		SignText text = titleFormat.getSignText(title.getName(), lang);

		titleStructure.setOn(player);
		titleStructure.updateDisplay(player, text);
	}

	/**
	 * Shows the structure to a player by toggling the appearance of all 
	 * of its elements to their "on" state with fake block updates. 
	 * <p>
	 * Depending on how the elements of this structure are constructed, 
	 * entering the "off" state may not be visually represented by 
	 * appearing from being hidden.
	 * <p>
	 * Does not keep track of what a player has been sent or currently 
	 * should be seeing on signs, so it may be necessary to refresh the
	 * displayed options/title.
	 * 
	 * @param player	The player to send the block updates to. 
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if player is <code>null</code>.
	 */
	public void show(ZamaPlayer player) {

		//debug
		Zama.debug(Menus.getPlugin(), null, "Showing a structure for player " + player.getName());

		titleStructure.setOn(player);
		for(ButtonOptionStructure bos : structures) {
			bos.setOn(player);
		}
	}

	/**
	 * Hides the structure from a player by toggling the appearance of all 
	 * of its elements to their "off" state with fake block updates. 
	 * <p>
	 * Depending on how the elements of this structure are constructed, 
	 * entering the "off" state may not be visually represented by hiding.
	 * <p>
	 * Structures are not automatically hidden if their server-side state
	 * differs from the configured "off" state. As such, it may be necessary
	 * to manually hide a structure when a player loads it in order for the
	 * "off" state to be the default appearance.
	 * 
	 * @param player	The player to send the block updates to. 
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if player is <code>null</code>.
	 */
	public void hide(ZamaPlayer player) {

		//debug
		Zama.debug(Menus.getPlugin(), null, "Hiding a structure for player " + player.getName());

		titleStructure.setOff(player);
		for(ButtonOptionStructure bos : structures) {
			bos.setOff(player);
		}
	}

	/**
	 * Gets the index of the option displayed on a sign at a given location,
	 * if any.
	 * 
	 * @param loc	The location of the sign.
	 * @return		The index of option that the sign is displaying.
	 * 				Returns <code>-1</code> if none of this
	 * 				structure's option display signs are at the location.
	 */
	public int getOptionSignIndex(BlockLocation loc) {
		if(loc == null) return -1;

		Integer index = signIndexes.get(loc);

		//if the location does not match any of the option display signs
		// of this structure, returns -1
		if(index == null)
			return -1;

		return index.intValue();
	}

	/**
	 * Gets the index of the option whose button is in a given location,
	 * if any.
	 * 
	 * @param loc	The location of the button.
	 * @return		The index of option that the button is meant to
	 * 				interact with. Returns <code>-1</code> if none of this
	 * 				structure's option buttons are at the location.
	 */
	public int getOptionButtonIndex(BlockLocation loc) {
		if(loc == null) return -1;

		Integer index = buttonIndexes.get(loc);

		//if the location does not match any of the option display signs
		// of this structure, returns -1
		if(index == null)
			return -1;

		return index.intValue();
	}

	/**
	 * Gets whether the block at a location is the title sign for this
	 * structure.
	 * 
	 * @param loc	The location to check.
	 * @return		<code>true</code> if it is the same location as the
	 * 				title display sign.
	 */
	public boolean isTitleSign(BlockLocation loc) {
		return titleStructure.getSignLocation().equals(loc);
	}

	/**
	 * Gets the max number of options this structure can display at once.
	 * 	
	 * @return	This structur's number of slots for options.
	 */
	public int maxOptions() {
		return this.structures.size();
	}

	/**
	 * Gets the base location of this structure, where a user can be 
	 * teleported to interact with the menu.
	 * <p>
	 * The 'legitimacy' and safety of this location cannot be guaranteed as 
	 * there is  no inherent logical definition of where this location 
	 * should be. It is defined without significant constraints by the 
	 * constructor of this object.
	 * 
	 * @return	The structure's base location.
	 */
	public Location getLocation() {
		return this.loc;
	}

	@Override
	public ButtonInteractionStructure copy(World world, int xOffset, int yOffset,
			int zOffset) {

		if(world == null)
			return null;

		Location offsetLoc = loc.clone().add(xOffset, yOffset, zOffset);
		offsetLoc.setWorld(world);

		DisplayStructure offsetTitle = titleStructure.copy(world, xOffset, yOffset,
				zOffset);

		List<ButtonOptionStructure> offsetStructures = new LinkedList<ButtonOptionStructure>();
		for(ButtonOptionStructure bos : structures) {
			if(bos == null) continue;

			offsetStructures.add(bos.copy(world, xOffset, yOffset, zOffset));
		}

		//passes on same instances of the formats, which are immutable
		return new ButtonInteractionStructure(offsetLoc, offsetTitle, 
				offsetStructures, titleFormat, optionFormat);

	}

}
