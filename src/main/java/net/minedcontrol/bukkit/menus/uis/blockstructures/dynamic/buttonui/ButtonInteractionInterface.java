package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui;

import net.minedcontrol.bukkit.menus.basis.MenuInterface;
import net.minedcontrol.bukkit.menus.basis.MenuStage;
import net.minedcontrol.bukkit.menus.basis.responses.MenuResponse;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.players.ZamaPlayer;


/**
 * A per-player menu interface that uses a dynamic button-interaction 
 * structure in order to interact with the underlying menu object.
 * <p>
 * Date Created: Jan 6, 2014
 * 
 * @author Brutus
 *
 */

public class ButtonInteractionInterface extends MenuInterface {

	private ButtonInteractionStructure structure;
	

	/**
	 * Class constructor.
	 * 
	 * @param structure 	The structure for this user interface.
	 * 						Not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public ButtonInteractionInterface(ButtonInteractionStructure structure) 
			throws IllegalArgumentException {

		if(structure == null)
			throw new IllegalArgumentException("structure cannot be null");

		this.structure = structure;
	}

	/**
	 * Attempts an interaction with a button of the user interface at a
	 * given location.
	 * 
	 * @param loc	The location of the button.
	 * @return		The response from the menu for the selection of the 
	 * 				option represented by the button. Returns 
	 * 				<code>null</code> if the location does not correspond 
	 * 				to one of the active option buttons of this user 
	 * 				interface.
	 */
	public MenuResponse buttonInteract(BlockLocation loc) {
		if(!isOpen())
			return null;
		
		if(loc == null)
			return null;

		int bIndex = structure.getOptionButtonIndex(loc);
		
		MenuStage stage = getMenu().getCurrentStage();
		if(stage == null)
			return null;

		if(bIndex > -1 && bIndex < stage.numOptions())
			return select(stage.getOption(bIndex));

		return null;
	}
	
	/**
	 * Attempts an interaction with a sign of the user interface at a
	 * given location. It may either be a sign of the menu title or 
	 * a sign of one of the options.
	 * 
	 * @param loc	The location of the button.
	 * @return		The description of the menu element corresponding to 
	 * 				the sign. Returns <code>null</code> if the location 
	 * 				does not correspond 
	 * 				to one of the active display signs of this user 
	 * 				interface.
	 */
	public MultilingualMessage signInteract(BlockLocation loc) {
		if(!isOpen())
			return null;
		
		if(loc == null)
			return null;
		
		MenuStage stage = getMenu().getCurrentStage();
		if(stage == null)
			return null;
		
		//if the title sign, gets the title desc
		if(structure.isTitleSign(loc))
			return stage.getTitle().getDescription();
		
		//else if an option sign, gets the option's desc
		int sIndex = structure.getOptionSignIndex(loc);
		if(sIndex > -1 && sIndex < stage.numOptions())
			return stage.getOption(sIndex).getDescription();
		
		return null;
	}
	
	/**
	 * Changes the structure this user interface is making use of.
	 * 	
	 * @param newStructure		The new structure.
	 * @param teleportPlayer	<code>true</code> to teleport the player to
	 * 							the spawnpoint of the new structure.
	 */
	public void setStructure(ButtonInteractionStructure newStructure, 
			boolean teleportPlayer) {
		
		if(newStructure == null)
			return;
		
		ZamaPlayer user = getMenu().getUser();
		if(user == null)
			return;
		
		onClose();
		structure = newStructure;
		
		if(teleportPlayer)
			user.teleport(structure.getLocation());
		
		if(!isOpen()) {
			structure.hide(user);
			return;
		}
		
		updateDisplay(getMenu().getCurrentStage());
		
	}

	@Override
	public int maxOptions() {
		return structure.maxOptions();
	}

	@Override
	protected void updateDisplay(MenuStage stage) {
		ZamaPlayer user = getMenu().getUser();

		//does nothing if the menu does not have a user, because this
		// interface does not mean anything without a user.
		if(user == null)
			return;

		structure.displayTitle(user, stage.getTitle());
		structure.displayOptions(user, stage.getOptions());
	}

	@Override
	protected void onClose() {
		//debug
		//Thread.dumpStack();
		
		ZamaPlayer user = getMenu().getUser();

		//does nothing if the menu does not have a user, because this
		// interface does not mean anything without a user.
		if(user == null)
			return;

		structure.hide(user);
	}

}
