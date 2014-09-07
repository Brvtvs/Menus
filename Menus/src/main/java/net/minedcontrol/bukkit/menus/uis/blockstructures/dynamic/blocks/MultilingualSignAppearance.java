package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks;

import org.bukkit.Material;

import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.BlockAppearance;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.bukkit.menus.uis.packetediting.PacketEngine;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.messaging.LanguageChoice;
import net.minedcontrol.zamalib.players.ZamaPlayer;

/**
 * A dynamic block appearance that supports multilingual sign text.
 * <p>
 * Date Created: Jan 28, 2014
 * 
 * @author Brutus
 *
 * @see BlockAppearance
 */
public class MultilingualSignAppearance extends BlockAppearance {
	
	private final MultilingualSignText text;

	/**
	 * Class constructor.
	 * 
	 * @param mat	The material of the sign block. Must be a type of sign.
	 * @param data	The data value of the sign block.
	 * @param text	The text on the sign. <code>null</code> is acceptable
	 * 				for no text, but is not recommended as it is the only
	 * 				functionality this class adds.
	 * 
	 * @throws IllegalArgumentException	if the material is not a type of
	 * 									sign.
	 */
	public MultilingualSignAppearance(Material mat, byte data, 
			MultilingualSignText text) throws IllegalArgumentException {
		
		super(mat, data);
		
		if(getMaterial() != Material.SIGN && getMaterial() != Material.WALL_SIGN)
			throw new IllegalArgumentException("material must be a type of sign");
		
		if(text == null)
			text = new MultilingualSignText(null, null, null, null);
		
		this.text = text;
		
	}
	
	@Override
	public void createFake(ZamaPlayer player, BlockLocation loc, 
			PacketEngine engine) throws NullPointerException {
		
		super.createFake(player, loc, engine);
		//after creating the fake sign, populates it with fake text in the
		// player's language (if supported)
		engine.sendSignTextUpdate(player, loc, 
				text.getSignText(player.getLanguagePreference()));
	}
	
	/**
	 * Gets a version of the text for the sign in a given language.
	 * 
	 * @param lang	The language to get the text in.
	 * @return		The version of the sign text for the given language.
	 */
	public SignText getSignText(LanguageChoice lang) {
		return text.getSignText(lang);
	}
	

}
