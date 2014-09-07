package net.minedcontrol.bukkit.menus.basis;

import java.util.LinkedList;
import java.util.Stack;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.basis.responses.MenuResponse;
import net.minedcontrol.bukkit.menus.basis.responses.MenuResponseType;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A menu dialogue made up of an indefinite number of distinct stages, 
 * each of which has a set of possible options to be selected from.
 * <p>
 * May be hidden and internal to a program or may be navigated by a player 
 * using a separate frontend user interface.
 * <p>
 * Menus are per-user objects that traverse an underlying data structure of
 * possible subsets of options, which may be represented by a variety of
 * possible implementation models, both realized and virtualized, such as a
 * tree of the possible options. Concurrency with multiple menus should
 * always be supported, and menu objects offer a traversal of the 
 * underlying data structure that can stall indefinitely between decisions.
 * <p>
 * Menus cannot be reused and a new one should be constructed whenever
 * there is a need to open a new dialogue over a menu underlay. Once a 
 * user is defined in a menu, it cannot be redefined.
 * <p>
 * A menu can be used without a user interface, but when a user interface is
 * present, a menu has an inherently one-to-one relationship with its interface.
 * However, a user and a menu interface can be defined ambiguously, and
 * could be triggered from any source(s), depending on the implementation, 
 * so long as it is through the one-to-one relationship between the 
 * <code>Menu</code> and its <code>MenuInterface</code>.
 * <p>
 * When a user interface is present, a menu governs the content that it
 * displays or does not display, and the transitions between stages.
 * <p>
 * Date Created: Jan 9, 2014
 * 
 * @author Brutus
 *
 * @see MenuResponse
 * @see MenuStage
 * @see MenuOption
 * @see MenuInterface
 * @see MenuUnderlay
 * @see UnderlayNode
 */

public class Menu {
	
	//the maximum amount of decisions that will be remembered for a menu.
	private static final int MAX_PATH_SIZE = 50;

	private final MenuUnderlay underlay;
	
	//the choices made so far in the menu. New choices added to the end,
	// old choices removed from the front when it eclipses the max size.
	private final LinkedList<MenuChoice> path;

	private ZamaPlayer player;

	private UnderlayNode position;
	
	//stores a reference the current stage. Should be fine as long as 
	// this specific stage instance does not change over time. Only making
	// a single request for the stage theoretically allows the node to 
	// virtualize a stage each time it is asked for one, without the state 
	// of this menu object changing in between user decisions, which would 
	// violate its purpose and logical contract
	private MenuStage stage;

	private MenuInterface ui;


	/**
	 * Class constructor.
	 * 
	 * @param underlay	The underlay that this menu should traverse over.
	 * 					Not <code>null</code>.
	 * @param player	The player this menu is for. Can be <code>null</code>
	 * 					to indicate there is no player for this menu. A
	 * 					player cannot be added/changed later.
	 * 
	 * @throws IllegalArgumentException	if the underlay is <code>null</code>.
	 * @throws IllegalStateException	on being unable to get a valid
	 * 									starting point from the given 
	 * 									underlay or being unable to add 
	 * 									this menu to the underlay.
	 */
	public Menu(MenuUnderlay underlay, ZamaPlayer player) 
			throws IllegalArgumentException,IllegalStateException { 


		if(underlay == null)
			throw new IllegalArgumentException("underlay cannot be null");

		this.player = player;

		this.underlay = underlay;
		
		if(!underlay.addMenu(this))
			throw new IllegalStateException("could not add this menu to"
					+ " the underlay");

		this.path = new LinkedList<MenuChoice>();
		
		setPosition(underlay.getStart(this));
		if(this.position == null)
			throw new IllegalStateException("cannot get a starting point "
					+ "from the underlay");
		
		Menus.getManager().getMenuCollection().addMenu(this);
	}


	//--------
	//PUBLIC
	//--------

	/**
	 * Gets the underlay for this menu.
	 * 
	 * @return	This menu's underlay.
	 * 
	 * @deprecated	It is not recommended to access the underlay directly,
	 * 				and it may result in inconsistency and/or concurrency
	 * 				issues. Interaction should be done primarily through
	 * 				the Menu implementation.
	 */
	@Deprecated
	public final MenuUnderlay getUnderlay() { return this.underlay; }

	/**
	 * Gets the player user of this menu, if there is one.
	 * 
	 * @return	The user of this menu. Returns <code>null</code> if no
	 * 			player is associated with this menu.
	 */
	public final ZamaPlayer getUser() { return this.player; }

	/**
	 * Gets a copy of the path this menu took so far in the form of a 
	 * stack of past choices, with the most recent entries at the top of 
	 * the stack.
	 * <p>
	 * The path may not be complete. This menu may limit the maximum amount
	 * of choices it remembers.
	 * 
	 * @return	The path that led to the current state of the menu.
	 */
	public final Stack<MenuChoice> getPath() {
		Stack<MenuChoice> path = new Stack<MenuChoice>();
		
		for(MenuChoice choice : this.path) {
			path.add(choice);
		}
		
		return path;
	}

	/**
	 * Gets the user interface of the menu.
	 * 
	 * @return	this menu's user interface.
	 */
	public final MenuInterface getInterface() { return ui; }

	/**
	 * Gets the current stage of the menu, including its title and options.
	 * 
	 * @return	The menu's current stage. Returns <code>null</code> if this
	 * 			menu has already reached the end of its dialogue.
	 */
	public final MenuStage getCurrentStage() { return stage; }

	/**
	 * Attempts to select an option from the menu's current status.
	 * 
	 * @param option	The option to select.
	 * @return			The status of the attempted selection.
	 */
	public final MenuResponse select(MenuOption option) {
		
		if(position == null) // menu already completed previously
			return new MenuResponse(MenuResponseType.PREVIOUSLY_FINISHED);
		
		UnderlayNode next = null;
		MenuResponse response = null;

		try {
			
			if(option == null)
				throw new IllegalArgumentException("option cannot be null");

			next = position.getNext(this, option);

		} catch(IllegalArgumentException e) { // not a valid selection
			return new MenuResponse(MenuResponseType.INVALID_OPTION);

		} catch(Exception e) { // unknown error encountered
			return new MenuResponse(MenuResponseType.MISC_ERROR);
		}
		
		
		if(next == null) { // the end of the underlay
			response = new MenuResponse(MenuResponseType.FINISHED);
		
		} else { // a child node was provided by the underlay
			response = new MenuResponse(MenuResponseType.CONTINUE);
		}
		
		//calls the option's listeners
		option.select(this, stage);
		
		//adds a the selection to the path if it was valid
		addToPath(new MenuChoice(position, option));

		//progresses the menu to the result of the successful selection
		setPosition(next);
		
		return response;
	}

	/**
	 * Undoes the last menu selection, returning it to its previous state
	 * and notifying menu listeners of the reversal. 
	 * <p>
	 * Does not guarantee any specific behavior other than returning to the 
	 * last set of options. Behavior depends on how the option listeners 
	 * and underlay are set up. Caution should be exercised. May provide
	 * unexpected or even totally unsupported behavior.
	 * 
	 * @return	<code>true</code> if the undo was successful. <code>false</code>
	 * 			if it was not, such as if there was no decision to undo.
	 */
	public final boolean goBack() {
		//returns if there is no last decision
		if(path.isEmpty())
			return false;

		MenuChoice last = path.removeLast();
		last.getChoice().undoLast(this);
		setPosition(last.getWhere());
		return true;
	}

	/**
	 * Mutually associates this menu with a frontend user interface.
	 * 
	 * @param ui	The user interface to display the menu with.
	 * 				Can be <code>null</code> to dissociate the menu from 
	 * 				its interface. 
	 */
	public final void setInterface(MenuInterface ui) {
		//if the interface is the one currently in use, returns early.
		if(ui != null && ui.equals(this.ui))
			return;

		//removes the old interface, if there was one.
		removeInterface();

		//returns early if there is no new interface.
		if(ui == null)
			return;

		this.ui = ui;
		ui.setMenu(this);
		
		//updates the display of the interface after being associated
		if(position != null)
			ui.displayStage(stage);
	}

	/**
	 * Mutually dissociates this menu from its frontend user interface.
	 */
	public final void removeInterface() {
		if(ui == null)
			return;

		ui.close(); //closes the interface before dissociating
		ui.removeMenu();

		this.ui = null;
	}
	

	//--------
	//PRIVATE
	//--------
	
	/**
	 * Updates the current position of this menu, which should not be
	 * done directly. Triggers updates in the menu's ui and maintains
	 * integrity.
	 * 
	 * @param next	The new position. Can be <code>null</code> to signal
	 * 				the end of the menu dialogue.
	 */
	private void setPosition(UnderlayNode next) {
		
		if(next == null) { // the end of the underlay
			underlay.removeMenu(this);
			position = next;
			stage = null;
			if(ui != null)
				ui.close();
			
			Menus.getManager().getMenuCollection().removeMenu(this);
		}
		
		else if(next.equals(position))  { // no actual change
			return;
		
		}
		
		else { // a new node
			position = next;
			stage = position.getMenuStage(this);
			if(ui != null)
				ui.displayStage(stage);
		}
	}
	
	/**
	 * Adds a new choice to the path, making sure it does not eclipse the
	 * maximum path length.
	 * 
	 * @param choice	The choice to add.
	 */
	private void addToPath(MenuChoice choice) {
		if(choice == null)
			return;
		
		path.add(choice);
		
		while (path.size() > MAX_PATH_SIZE) {
			path.removeFirst();
		}
	}
}
