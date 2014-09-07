package net.minedcontrol.bukkit.menus.basis.responses;

/**
 * An enumeration of the possible qualitatively distinct types of response 
 * from a menu. Does not contain detailed information about the circumstance
 * of the response, but provides distinct types to allow for common-sense
 * behavior, such as not progressing the menu if a selection failed, or
 * presenting the next set of options if it succeeded.
 * <p>
 * Date Created: Jan 9, 2014
 * 
 * @author Brutus
 *
 */
public enum MenuResponseType {
	
	OTHER(),	// unknown or none of the enumerated values apply
	
	FINISHED(),	// the menu is completed, the interface can conclude.
	
	PREVIOUSLY_FINISHED(),
	
	CONTINUE(),	// the next set of options should be displayed.
	
	INVALID_OPTION(),	// the selected option was invalid.
	
	MENU_NOT_FOUND(),	// the interface is not associated with a menu.
	
	MISC_ERROR(),	// an error was encountered while attempting to make the 
					// selection
	;

	/**
	 * Gets whether the type represents a successful selection that can
	 * be acted upon.
	 * 
	 * @param type	The type to check.
	 * @return		<code>true</code> if it is a response that indicates a
	 * 				successful selection.
	 */
	public static boolean successful(MenuResponseType type) {
		return (type == FINISHED || type == CONTINUE);
	}
}
