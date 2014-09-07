package net.minedcontrol.bukkit.menus.basis.responses;

/**
 * A response from the menu as to the status of a selection, including the 
 * type of response. 
 * <p>
 * Serves essentially as a wrapper for the <code>MessageResponseType</code> 
 * enum, but can be extended to add functionality and pass more information
 * from the menu back to the user interface.
 * <p>
 * Date Created: Jan 9, 2014
 * 
 * @author Brutus
 *
 */

public class MenuResponse {
	
	private final MenuResponseType type;
	
	/**
	 * Class constructor.
	 * 
	 * @param type		The type of response, not <code>null</code>.
	 * 
	 * @throws IllegalArgumentException	if type is <code>null</code>
	 */
	public MenuResponse(MenuResponseType type) {
		if(type == null) 
			throw new IllegalArgumentException("type cannot be null");
		
		this.type = type;
	}
	
	/**
	 * Gets the type of this response.
	 * 
	 * @return	The response's type.
	 */
	public MenuResponseType getResponseType() {
		return this.type;
	}
	

}
