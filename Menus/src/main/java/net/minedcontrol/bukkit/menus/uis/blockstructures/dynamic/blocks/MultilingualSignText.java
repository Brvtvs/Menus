package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.zamalib.messaging.LanguageChoice;
import net.minedcontrol.zamalib.messaging.messages.LiteralMessage;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;


/**
 * A multilingual set of messages that can be converted to an array of
 * strings, ensured to be safe for a sign.
 * <p>
 * Date Created: Jan 28, 2014
 * 
 * @author Brutus
 *
 */

public final class MultilingualSignText {
	
	private static final int NUM_LINES = 4;
	private static final int LINE_LENGTH = 15;
	
	private final MultilingualMessage[] lines;
	
	/**
	 * Class constructor. Max line length is 15, so any messages that
	 * have a version in excess of that will be truncated when converted
	 * to text. <code>null</code> parameters are sanitized into empty
	 * messages.
	 * 
	 * @param line1		The message for the first line of the sign.
	 * @param line2		The message for the second line of the sign.
	 * @param line3		The message for the third line of the sign.
	 * @param line4		The message for the fourth line of the sign.
	 */
	public MultilingualSignText(MultilingualMessage line1, MultilingualMessage line2, 
			MultilingualMessage line3, MultilingualMessage line4) {
		
		this.lines = new MultilingualMessage[NUM_LINES];
		
		lines[0] = sanitizeMessage(line1);
		lines[1] = sanitizeMessage(line2);
		lines[2] = sanitizeMessage(line3);
		lines[3] = sanitizeMessage(line4);
		
	}
	
	
	/**
	 * Sanitizes a message so it is safe to use in this object.
	 * 
	 * @param msg	The message to sanitize.
	 * @return		The sanitized version of the message.
	 */
	private MultilingualMessage sanitizeMessage(MultilingualMessage msg) {
		if(msg == null)
			return new LiteralMessage("");
		
		return msg;
	}


	/**
	 * Gets the version of this message for a given language, if supported.
	 * <p>
	 * Versions are requested on a per-line basis, depending on the
	 * language support and defaults of each line, the text as a whole may
	 * not be in a consistent language.
	 * 
	 * @param lang	
	 * @return
	 */
	public SignText getSignText(LanguageChoice lang) {

		String[] arr = new String[NUM_LINES];
		
		for(int i = 0; i < lines.length; i++) {
			arr[i] = lines[i].getMessage(lang);
			if(arr[i].length() > LINE_LENGTH)
				arr[i] = arr[i].substring(0, LINE_LENGTH);
		}
		
		return new SignText(arr[0], arr[1], arr[2], arr[3]);
	}

}
