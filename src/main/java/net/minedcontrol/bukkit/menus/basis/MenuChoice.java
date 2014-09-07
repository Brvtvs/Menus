package net.minedcontrol.bukkit.menus.basis;

/**
 * Stores a choice that was made in a menu.
 * <p>
 * Date Created: Jan 20, 2014
 * 
 * @author Brutus
 *
 */

public class MenuChoice {

	private final MenuOption chosen;
	private final UnderlayNode where;

	private final long when;


	/**
	 * Class constructor. 
	 * 
	 * @param where		In what underlying node the choice was made.
	 * 					Not <code>null</code>.
	 * @param when		When the choice was made, as a millisecond 
	 * 					timestamp. 
	 * @param chosen	What option was chosen. Not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public MenuChoice(UnderlayNode where, long when, MenuOption chosen) 
			throws IllegalArgumentException {
		
		if(chosen == null || where == null)
			throw new IllegalArgumentException("params cannot be null");

		this.where = where;
		this.chosen = chosen;
		this.when = when;
	}
	
	/**
	 * Class constructor. Does not take a timestamp and assumes the
	 * current time instead.
	 * 
	 * @param where		In what underlying node the choice was made.
	 * 					Not <code>null</code>.
	 * @param chosen	What option was chosen. Not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public MenuChoice(UnderlayNode where, MenuOption chosen) 
			throws IllegalArgumentException {
		
		if(chosen == null || where == null)
			throw new IllegalArgumentException("params cannot be null");

		this.where = where;
		this.chosen = chosen;
		
		this.when = System.currentTimeMillis();
		
	}
	
	
	/**
	 * Gets the option that was chosen.
	 * 
	 * @return	The chosen option.
	 */
	public MenuOption getChoice() {
		return chosen;
	}

	/**
	 * Gets in what underlay node the choice was made.
	 * 
	 * @return	Where the choice was made.
	 */
	public UnderlayNode getWhere() {
		return where;
	}

	/**
	 * Gets when the choice was made as a millisecond timestamp.
	 * 
	 * @return	When the choice was made.
	 */
	public long getWhen() {
		return when;
	}

}
