package net.minedcontrol.bukkit.menus.uis.blockstructures.blocks;

/**
 * A fixed collection of strings that is safe for the contents of a sign.
 * <p>
 * Not multilingual. Useful for instances where the literal text is required,
 * such as after the language has already been ascertained and a specific
 * version of a message needs to be displayed on a sign.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 *
 * @see MultilingualSignText
 */
public final class SignText {
	
	private static final int NUM_LINES = 4;
	private static final int STRING_LENGTH = 15;
	
	private final String[] text;
	
	/**
	 * Class constructor. Sanitize any invalid parameters, truncating strings
	 * longer than 15 characters and converting <code>null</code> values 
	 * to empty strings.
	 * 
	 * @param line1		The first line of text.
	 * @param line2		The second line of text.
	 * @param line3		The third line of text.
	 * @param line4		The fourth line of text.
	 */
	public SignText(String line1, String line2, String line3, String line4) {
		text = new String[NUM_LINES];
		
		text[0] = sanitizeText(line1);
		text[1] = sanitizeText(line2);
		text[2] = sanitizeText(line3);
		text[3] = sanitizeText(line4);
	}
	
	/**
	 * Gets an array version of this sign text, always of length 4 and with
	 * no <code>null</code> values or text that will not fit on a sign.
	 * 
	 * @return	this text's array.
	 */
	public String[] toArray() {
		return this.text.clone();
	}
	
	/**
	 * Makes a line of text sign-safe.
	 * 
	 * @param str	The string to sanitize.
	 * @return		The sanitized version of the string.
	 */
	private String sanitizeText(String str) {
		if(str == null)
			return "";
		
		if(str.length() > STRING_LENGTH) 
			str = str.substring(0, STRING_LENGTH);
		
		return str;
	}

}
