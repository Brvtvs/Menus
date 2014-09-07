package net.minedcontrol.bukkit.menus.uis.blockstructures.blocks;

import net.minedcontrol.zamalib.messaging.LanguageChoice;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;

/**
 * An immutable format for a single sign, allowing for external messages to 
 * be inserted into a formalized text structure, safe for a sign. Allows
 * for predefined, unchanging lines of text on a sign, a "format", around
 * which external text is inserted.
 * <p>
 * Format is defined per-line, meaning it is not possible to have both
 * external text and formatted text on the same line. Instead, this allows
 * for inputting headers, footers, or middle lines and filling the rest with
 * external text.
 * <p>
 * Date Created: Jan 12, 2014
 * 
 * @author Brutus
 *
 */

public final class SignFormat {
	
	private static final int LINE_LENGTH = 15;
	private static final int NUM_LINES = 4;
	
	private final MultilingualMessage[] format;
	private final int numNulls;
	
	/**
	 * Class Constructor. Takes a series of messages that make up the 
	 * predefined lines on the sign. Any message version that exceeds the 
	 * 15 character limit for signs will be truncated. <code>null</code>
	 * should be passed in for the lines that will display the externally
	 * input text.
	 * <p>
	 * Example (using simple strings for clarity): 
	 * <code>new SignFormat("Press to select", "", null, null)</code> would
	 * create a sign that has <code>Press to select</code> on the first
	 * line, an empty second line, and the externally input text on the
	 * third and fourth line.
	 * 
	 * @param line1		The predefined text on line 1 of the sign. 
	 * 					<code>null</code> not to define any text and allow
	 * 					external input to be put on the line.
	 * @param line2		The predefined text on line 2 of the sign. 
	 * 					<code>null</code> not to define any text and allow
	 * 					external input to be put on the line.
	 * @param line3		The predefined text on line 3 of the sign. 
	 * 					<code>null</code> not to define any text and allow
	 * 					external input to be put on the line.
	 * @param line4		The predefined text on line 4 of the sign. 
	 * 					<code>null</code> not to define any text and allow
	 * 					external input to be put on the line.
	 * 
	 */
	public SignFormat(MultilingualMessage line1, MultilingualMessage line2, 
			MultilingualMessage line3, MultilingualMessage line4) {
		
		this.format = new MultilingualMessage[NUM_LINES];
		
		format[0] = line1;
		format[1] = line2;
		format[2] = line3;
		format[3] = line4;
		
		int n = 0;
		for(int i = 0; i < format.length; i++) {
			if(format[i] == null)
				n++;
		}
		
		this.numNulls = n;
	}
	
	
	/**
	 * Generates a sign-safe text object in this defined format from a
	 * given message.
	 * 
	 * @param msg	The message to format into sign-safe text.
	 * @param lang	The version of the messages to use.
	 * @return		A sign-safe text object using this format.
	 */
	public SignText getSignText(MultilingualMessage msg, LanguageChoice lang) {
		
		String str = msg.getMessage(lang);
		
		int exIndex = 0;
		String[] external = SignUtil.translateText(str, LINE_LENGTH, 
				numNulls, false);
		
		String[] ret = new String[NUM_LINES];
		
		for(int i = 0; i < format.length; i++) {
			ret[i] = (format[i] != null ? 
					format[i].getMessage(lang) : external[exIndex++]);
		}
		
		return new SignText(ret[0], ret[1], ret[2], ret[3]);
	}
}
