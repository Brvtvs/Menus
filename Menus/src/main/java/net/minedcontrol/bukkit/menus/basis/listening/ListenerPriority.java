package net.minedcontrol.bukkit.menus.basis.listening;

/**
 * Enumeration of listener priorities. Listeners will be triggered in order
 * by their priority, and then unpredictably within a priority. The order
 * is as follows: HIGH, NORMAL, LOW, MONITOR.
 * <p>
 * Uses a similar scheme to Bukkit's <code>EventPriority</code>.
 * <p>
 * Date Created: Jan 15, 2014
 * 
 * @author Brutus
 *
 * @see org.bukkit.event.EventPriority
 */
public enum ListenerPriority {

	//The order of declaration is important and defines what order they
	// are returned by #values(), and thus the order they are called in 
	HIGH(750),
	NORMAL(500),
	LOW(250),
	MONITOR(0),
	;
	
	private ListenerPriority(int priorityNum) {
		this.priorityNum = priorityNum;
	}
	
	private final int priorityNum;
	
	/**
	 * Gets an integer version of this priority, with a higher number
	 * indicating being triggered earlier.
	 * 
	 * @return	The priority's integer value.
	 */
	public int getInt() {
		return this.priorityNum;
	}
	
	/**
	 * Gets the normal, medium priority to use as default.
	 * 
	 * @return	The default priority.
	 */
	public static ListenerPriority getDefaultPriority() {
		return NORMAL;
	}
		
}
