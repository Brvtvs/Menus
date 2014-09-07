package net.minedcontrol.bukkit.menus.basis;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.basis.events.MenuEvent;
import net.minedcontrol.bukkit.menus.basis.events.MenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.MenuUndoEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuSelectEvent;
import net.minedcontrol.bukkit.menus.basis.events.PlayerMenuUndoEvent;
import net.minedcontrol.bukkit.menus.basis.listening.MenuListener;
import net.minedcontrol.bukkit.menus.basis.listening.ListenerPriority;
import net.minedcontrol.zamalib.bukkit.util.colors.ColorScheme;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A menu element that can be selected and will call any listeners that
 * have been registered to it.
 * <p>
 * Contains a string id that should be unique and can be used to quickly 
 * and simply access an instance of an option from the menu manager
 * singleton, so that listening to menu options is 
 * underlay-data-structure-independent and easy to deal with via the API.
 * <p>
 * Date Created: Jan 8, 2014
 * 
 * @author Brutus
 *
 * @see MenuElement
 * @see MenuListener
 */

public class MenuOption extends MenuElement {
	
	private static final ListenerPriority DEFAULT_PRIORITY = ListenerPriority.getDefaultPriority();
	
	//this menus unique identifier, used to access it from the collection
	// of all options.
	private final String id;
	
	//A map of sets of listeners, keyed by their priority. 
	private final Map<ListenerPriority, Set<MenuListener>> listeners;
	//A map of all listeners, with their priority as the value
	private final Map<MenuListener, ListenerPriority> priorities;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param id			The identifier of this option. Must be unique 
	 * 						within a runtime. Not <code>null</code> or
	 * 						empty.
	 * @param name			The name of the element. Should be relatively 
	 * 						short, as many user interfaces may have very 
	 * 						limited space to display the name and it will
	 * 						be truncated if it cannot fit.
	 * 						Not <code>null</code>.
	 * @param desc			The longer, more detailed description of the
	 * 						element. Not <code>null</code>.
	 * @param displayItem	An item that can be used to display this 
	 * 						element in a user interface, such as an 
	 * 						inventory menu, if any. Can be 
	 * 						<code>null</code> for none.
	 * @param displayColor	A color that can be used when displaying this 
	 * 						element in a user interface, such as an 
	 * 						inventory menu using wool blocks, if any. Can 
	 * 						be <code>null</code> for none.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> id, name, or 
	 * 									description.
	 */
	public MenuOption(String id, MultilingualMessage name, 
			MultilingualMessage desc, ItemStack displayItem, 
			ColorScheme displayColor ) throws IllegalArgumentException {
		
		super(name, desc, displayItem, displayColor);
		
		if(id == null || id.equals(""))
			throw new IllegalArgumentException("The unique id cannot be "
					+ "null or empty");
		
		this.id = id;
		
		this.priorities = new HashMap<MenuListener, ListenerPriority>();
		this.listeners = new EnumMap<ListenerPriority, 
				Set<MenuListener>>(ListenerPriority.class);
		
		Menus.getManager().addOption(this);
	}


	//--------
	//PUBLIC
	//--------
	
	/**
	 * Gets the String identifier of this option.
	 * 
	 * @return	This option's string id.
	 */
	public final String getId() {
		return this.id;
	}
	
	/**
	 * Gets a copy of the set of all the listeners of this menu option.
	 * 
	 * @return	The option's listeners. Returns an empty set if there are
	 * 			none.
	 */
	public final Set<MenuListener> getContents() {
		return new HashSet<MenuListener>(priorities.keySet());
	}
	
	/**
	 * Gets a copy of the set of the listeners for a given priority of this
	 * menu option.
	 * 
	 * @param priority	The priority of which to get the listeners for.
	 * @return			A copied set of the listeners for the given 
	 * 					priority. Returns an empty set if there are none.
	 */
	public final Set<MenuListener> getContents(ListenerPriority priority) {
		Set<MenuListener> ret = new HashSet<MenuListener>();
		
		if(priority == null)
			return ret;
		
		Set<MenuListener> src = listeners.get(priority);
		
		if(src == null)
			return ret;
		
		ret.addAll(src);
		return ret;
	}
	
	/**
	 * Registers a listener that will be triggered when this option is
	 * selected. Replaces the listener's previous state if it is already
	 * registered. 
	 * <p>
	 * Uses default priority.
	 * 
	 * @param listener	The listener to register.
	 */
	public final void registerListener(MenuListener listener) {
		registerListener(listener, null);
	}
	
	/**
	 * Registers a listener that will be triggered when this option is
	 * selected. Replaces the listener's previous state if it is already
	 * registered. 
	 * 
	 * @param listener	The listener to register.
	 * @param priority	The priority to listen at.
	 */
	public final void registerListener(MenuListener listener, 
			ListenerPriority priority) {
		
		if(listener == null)
			return;
		
		//if no priority was provided, uses the default
		if(priority == null)
			priority = DEFAULT_PRIORITY;
		
		//if a previous instance of this listener has been registered,
		// unregisters it before registering it again.
		if(priorities.containsKey(listener))
			unregisterListener(listener);
		
		priorities.put(listener, priority);
		
		
		Set<MenuListener> lSet = listeners.get(priority);
		
		//if no listeners at this priority have been added yet, creates a
		// set in the map for it.
		if(lSet == null) {
			lSet = new HashSet<MenuListener>();
			listeners.put(priority, lSet);
		}
		
		lSet.add(listener);
	}
	
	
	/**
	 * Unregisters a listener.
	 * 
	 * @param listener	The listener to unregister.
	 */
	public final void unregisterListener(MenuListener listener) {
		if(listener == null)
			return;
		
		//Iterates over all values to find the listener and unregister it.
		// Not very efficient, but unlikely to be used much and allows for
		// efficient addition and access through the EnumMap. If it becomes
		// an issue, a secondary index can be added (which will consume
		// more memory and cause less efficiency in additions) to make removal
		// more efficient.
		for(Map.Entry<ListenerPriority, Set<MenuListener>> ent : listeners.entrySet()) {
			ent.getValue().remove(listener);
		}
	}
	
	
	//--------
	//DEFAULT 
	//(for use by a menu object, but not elsewhere)
	//--------
	
	/**
	 * Selects this option, calling its listeners. Will pass on a player
	 * object if a player menu is provided.
	 * 
	 * @param menu	The menu selecting this option. If <code>null</code>
	 * 				or otherwise invalid, method will return early.
	 */
	final void select(Menu menu, MenuStage stage) {
		if(menu == null)
			return;
		
		ZamaPlayer user = menu.getUser();
		if(user != null) {
			callEvent(new PlayerMenuSelectEvent(this, menu, stage,
					menu.getPath(), user));
			
		} else {
			callEvent(new MenuSelectEvent(this, menu, stage, menu.getPath()));
		}
	}
	
	/**
	 * Undoes this option for the menu (and player if a player menu), 
	 * calling its listeners with an undo event.
	 * 
	 * @param menu	The menu undoing this option. If <code>null</code>
	 * 				or otherwise invalid, method will return early.
	 */
	final void undoLast(Menu menu) {
		if(menu == null)
			return;
		
		ZamaPlayer user = menu.getUser();
		if(user != null) {
			callEvent(new PlayerMenuUndoEvent(this, menu, 
					menu.getPath(), user));
			
		} else {
			callEvent(new MenuUndoEvent(this, menu, menu.getPath()));
		}
	}
	
	
	
	
	//--------
	//PRIVATE
	//--------
	
	
	/**
	 * Private helper method that calls a menu event for the listeners
	 * of this option.
	 * 
	 * @param event	The event to call.
	 */
	private void callEvent(MenuEvent event) {
		if(event == null)
			return;
		
		//for each listener priority, in the order that they are defined,
		// gets the set of listeners registered at that priority (if any)
		// and calls them, in arbitrary order within the set, using the
		// internal implementation of the specific event.
		for(ListenerPriority priority : ListenerPriority.values()) {
			
			Set<MenuListener> listenSet = listeners.get(priority);
			
			if(listenSet == null) continue;
			for(MenuListener listener : listenSet) {
				event.call(listener);
			}
		}
	}
	

}
