package net.minedcontrol.bukkit.menus.uis.blockstructures.blocks;

import java.lang.reflect.InvocationTargetException;

import net.minedcontrol.bukkit.menus.uis.packetediting.packets.WrapperPlayServerUpdateSign;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import com.comphenix.protocol.ProtocolLibrary;


/**
 * Static utility for sign-related string/string array processing and 
 * dynamically updating sign-text per-client.
 * <p>
 * Date Created: Jan 7, 2014
 * 
 * @author Brutus
 *
 */

public class SignUtil {

	//TODO divide this up as static members of the SignText/SignFormat 
	// class and non-static members of the packet-management engine?

	//the maximum supported length of lines on a sign.
	private static final int MAX_LINE_LENGTH = 15;


	/**
	 * Sends a sign update packet to a player to create a clientside only
	 * appearance for a sign.
	 * <p>
	 * Does not check to make sure the block is already displayed to the
	 * player as a sign or that it will not crash their client.
	 * 
	 * @param player	The player to send the packet to.
	 * @param sign		The sign block to update.
	 * @param text		The text to display on the sign.
	 * 
	 * @throws IllegalArgumentException	if the player and sign are not in
	 * 									the same world.
	 * @throws RuntimeException			if the packet sending fails.
	 * @throws NullPointerException		if any of  the parameters are null
	 * 									or if the sign location cannot be
	 * 									translated to a real location.
	 */
	public static void updateSignText(ZamaPlayer player, BlockLocation sign, SignText text) 
			throws IllegalArgumentException,NullPointerException,RuntimeException {

			if(!player.getPlayer().getWorld().getName().equalsIgnoreCase((sign.getWorldName())))
				throw new IllegalArgumentException("Player and sign must be in"
						+ " the same world.");
			

			WrapperPlayServerUpdateSign wrapper = new WrapperPlayServerUpdateSign();
			wrapper.setX(sign.getX());
			wrapper.setY((short)sign.getY());
			wrapper.setZ(sign.getZ());
			wrapper.setLines(text.toArray());

		try {
			
			//debug
			/*String[] arr = text.toArray();
			String msg = "";
			for(int i = 0; i < arr.length; i++) {
				msg += arr[i] + (i < 3 ? ", " : "");
			}
			Zama.debug(Menus.getPlugin(), null, "sending a sign update of [" 
			+ msg + "] to player " + player.getName() + " at " + sign.toString());*/
			
			ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), wrapper.getHandle());
		}
		catch(InvocationTargetException e) {
			throw new RuntimeException("Cannot send sign update packet", e);
		}

	}


	/**
	 * Translates a string into a sign-safe array of strings and prepends
	 * a message, such as a formatting code, to it.
	 * 
	 * @param msg				The message to translate.
	 * @param lineLength		The max length of each line. Cannot be 
	 * 							larger than <code>15</code>.
	 * @param numLines			The number of lines to translate it into.
	 * 							If the msg cannot be translated into this
	 * 							number of lines, it will be truncated on
	 * 							the end. Can be more than <code>4</code>
	 * 							to allow for translating a message into
	 * 							a multi-sign text.
	 * @param centerVertically	Whether to center the occupied lines of the
	 * 							result in the array, rather than leaving
	 * 							all of the empty lines at the end.
	 * 							Does nothing if the text will not fit on
	 * 							a single sign.
	 * @param prepend			The string to prepend to each line.
	 * 							Will replace <code>'&'</code> with 
	 * 							<code>'§'</code> in order to support
	 * 							easy minecraft formatting codes.
	 * @return					The message, translated into a new array of
	 * 							strings, without <code>null</code> pointers
	 * 							and with each prepended with the defined 
	 * 							string, and each line being within the 
	 * 							defined maximum length.
	 */
	public static String[] translateText(String msg, int lineLength, 
			int numLines, boolean centerVertically, String prepend) {

		String[] ret = translateText(msg, lineLength - 2, numLines, 
				centerVertically);

		if(prepend != null && !prepend.equals(""))
			return prepend(ret, prepend, lineLength, false);

		return ret;
	}


	/**
	 * Translates a string into a sign-safe array of strings.
	 * 
	 * @param msg				The message to translate.
	 * @param lineLength		The max length of each line. Cannot be 
	 * 							larger than <code>15</code>.
	 * @param numLines			The number of lines to translate it into.
	 * 							If the msg cannot be translated into this
	 * 							number of lines, it will be truncated on
	 * 							the end. Can be more than <code>4</code>
	 * 							to allow for translating a message into
	 * 							a multi-sign text.
	 * @param centerVertically	Whether to center the occupied lines of the
	 * 							result in the array, rather than leaving
	 * 							all of the empty lines at the end.
	 * 							Does nothing if the text will not fit on
	 * 							a single sign.
	 * @return					The message, translated into a new array of
	 * 							strings, without <code>null</code> pointers
	 * 							and with each line being within the 
	 * 							defined maximum length.
	 */
	public static String[] translateText(String msg, int lineLength, 
			int numLines, boolean centerVertically) {

		String[] ret = new String[numLines];

		//If no valid lines are requested, returns the empty array
		if(numLines < 1)
			return ret;

		//if the message is null or empty, or there the line length cannot 
		// fit any characters, just returns an array of empty strings.
		if(msg == null || msg.equals("") || lineLength < 1) {
			return removeNull(ret);
		}

		//if a line length longer than the maximum supported by signs,
		// is provided reduce the it to the max.
		if(lineLength > MAX_LINE_LENGTH)
			lineLength = MAX_LINE_LENGTH;


		int lines = 0;				// number of lines made so far.
		String remainingMsg = msg;	// remaining message to translate.

		//while it has not finished processing the msg and there are lines
		// left to use.
		while(remainingMsg != null && !remainingMsg.equals("") && lines 
				< numLines) {

			
			//if this is the last line necessary to fully translate the 
			// string (it fits into the max line length)
			if(remainingMsg.length() <= lineLength) {
				ret[lines++] = remainingMsg;
				remainingMsg = "";
				break;
			}

			
			
			
			//gets a line-length-long chunk of the remaining message and
			// looks for a natural break (a ' ' character in it).
			String current = null;
			
			//if the character immediately after the chunk that is being
			// processed is a break, uses that instead of finding a break
			// within the chunk. (Allows for a chunk that is the max line 
			// length when there is a break immediately after it)
			if(remainingMsg.length() > lineLength && remainingMsg.charAt(lineLength) == ' ') {
				current = remainingMsg.substring(0, lineLength + 1);
			
			} else {
				current = remainingMsg.substring(0, lineLength);
			}
			
			int breakIndex = current.lastIndexOf(' ');
			
			

			//if there is no break in this line chunk at all, adds a break 
			// as the last character, using a '-', unless it is the last
			// line
			if(breakIndex == -1) {

				if(lines < numLines - 1)
					current = remainingMsg.substring(0, lineLength - 1) + "-";
				else
					current = remainingMsg.substring(0, lineLength);

				remainingMsg = remainingMsg.substring(lineLength - 1, 
						remainingMsg.length());


				//else ends the line at the last break (' ') and leaves the
				// character out, replacing it with the line break
			} else {
				current = remainingMsg.substring(0, breakIndex);
				remainingMsg = remainingMsg.substring(current.length() + 1, 
						remainingMsg.length());
			}

			ret[lines++] = current;
		}

		//if there are at least two lines free after translating the
		// message and the user wants to center the text lines in the
		// array, and it will fit on a single sign, shifts the text lines 
		// to the center.
		if(centerVertically && numLines < 6 && lines < numLines - 1) {
			int shift = (numLines - lines) / 2;

			//shifts the lines down the array
			for(int i = ret.length - 1; i >= shift; i--) {
				ret[i] = ret[i - shift];
			}

			//clears the lines at the top of the array that were shifted
			// but not subsequently overwritten
			for(int i = 0; i < shift; i++) {
				ret[i] = "";
			}
		}

		return removeNull(ret);
	}


	/**
	 * Clones the array and prepends each element of with a defined string, 
	 * such as a formatting code. 
	 * 
	 * @param text			The array of strings to prepend.
	 * @param prepend		The string to prepend. 
	 * 						Will replace <code>'&'</code> with 
	 * 						<code>'§'</code> in order to support
	 * 						easy minecraft formatting codes.
	 * @param lineLength	The maximum length of each string.
	 * 						If a string is over this length after being
	 * 						prepended, it will be truncated from the end.
	 * @param prependEmpty	Whether to prepend the string to empty and
	 * 						<code>null</code> elements.
	 * @return				A new string array containing the strings from
	 * 						the given array with the defined string 
	 * 						prepended to each, and without any 
	 * 						<code>null</code> pointers.
	 */
	public static String[] prepend(String[] text, String prepend, 
			int lineLength, boolean prependEmpty) {

		if(text == null) return new String[]{};

		String[] ret = text.clone();

		//if there is nothing to prepend, just removes null pointers and 
		// returns
		if(prepend == null || prepend.equals("")) 
			return removeNull(ret);

		//replaces '&' characters to allow them to be used to signal a
		// formatting code.
		prepend = prepend.replace('&', '§');

		//prepends each string in the array, removing null pointers
		for(int i = 0; i < ret.length; i++) {
			if(ret[i] == null)
				ret[i] = "";

			String str = ret[i];

			//if the string is not empty, or the user wants to prepend
			// empty strings
			if(!str.equals("") || prependEmpty)
				str = prepend + str;

			//if prepending the string put it over the max length, truncates
			// it.
			if(str.length() > lineLength)
				str = str.substring(0, lineLength);

			ret[i] = str;
		}

		return ret;

	}


	/**
	 * Removes <code>null</code>s from a string array, returning a new 
	 * array instance rather than editing the array argument.
	 * 
	 * @param arr	The array to get a version of without <code>null</code> 
	 * 				pointers.
	 * @return		A cloned version of the array, without any <code>null</code>
	 * 				pointers.
	 */
	public static String[] removeNull(String[] arr) {
		if(arr == null) return new String[]{};

		String[] ret = arr.clone();

		for(int i = 0; i < ret.length; i++) {
			if(ret[i] == null)
				ret[i] = "";
		}

		return ret;
	}






	//--------
	//UNIT TESTING
	//--------

	public static void main(String[] args) {
		String test = "this is a test message";

		String[] translated = translateText(test, 15, 4, true);
		printArray(translated);

		translated = translateText(test, 15, 4, true, "&7");
		printArray(translated);

		translated = translateText(test, 15, 4, false);
		printArray(translated);

		translated = translateText(test, 15, 4, false, "&7");
		printArray(translated);



		test = "optimalistically I go to the ball";

		translated = translateText(test, 15, 5, true);
		printArray(translated);

		translated = translateText(test, 15, 5, true, "&7");
		printArray(translated);


		test = "optimalisticallyIgototheballABCDEFG______ASDA_WD_QWD_ADS";

		translated = translateText(test, 15, 4, true);
		printArray(translated);

		translated = translateText(test, 15, 4, true, "&7");
		printArray(translated);
		
		
		test = "Uh oh, this was a ruse. Prepare for a deluge of sentimentalism.";

		translated = translateText(test, 15, 4, true);
		printArray(translated);

		translated = translateText(test, 15, 4, true, "&7");
		printArray(translated);
		
		test = "relate to, who I have interest in, and who understands me.";

		translated = translateText(test, 15, 4, true);
		printArray(translated);

		translated = translateText(test, 15, 4, true, "&7");
		printArray(translated);
		
	}

	//Unit testing
	public static void printArray(String[] arr) {
		if(arr == null) {
			System.out.println("Attempted to print an empty array");
			return;
		}

		System.out.println("");

		for(int i = 0; i < arr.length; i++) {
			System.out.println("[" + i + "] " + arr[i]);
		}

		System.out.println("");
	}

}
