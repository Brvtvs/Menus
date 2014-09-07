package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.uis.blockstructures.blocks.SignText;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.ToggleableBlock;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * A toggleable, dynamic structure which includes a sign whose text can be
 * dynamically set per-client. 
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 * 
 * @see DynamicStructure
 * @see ToggleableStructure
 */

public class DisplayStructure extends ToggleableStructure {

	protected ToggleableBlock sign;
	protected Block signBlock;
	
	/**
	 * Class constructor. All parameters must be in the same minecraft 
	 * world.
	 * 
	 * @param world		The world this structure is in. Not <code>null</code>.
	 * 					in the minecraft world. Must also have an 'on' state
	 * 					that is a type of button.
	 * @param sign		The sign for this structure, 
	 * 					not <code>null</code> and must actually exist 
	 * 					in the minecraft world. Must also have an 'on' state
	 * 					that is a type of sign.
	 * @param base		A map of the blocks that make up the base of the 
	 * 					structure and should be toggled on/off with this 
	 * 					structure, with how they should appear in each state 
	 * 					as the mapped values. 
	 * 					Not <code>null</code> but the blocks themselves can 
	 * 					exist in any state (including as air) in the 
	 * 					minecraft world.
	 * 
	 * @throws IllegalArgumentException	if any of the parameters are 
	 * 									<code>null</code>, if the display
	 * 									sign does not exist in the 
	 * 									minecraft world or if its 'on' 
	 * 									state is not a sign, or if the 
	 *									parameters are not located in the 
	 *									same world.
	 */
	public DisplayStructure(World world, ToggleableBlock sign, 
			Set<ToggleableBlock> base) throws IllegalArgumentException {
		
		super(world, base);
		
		if(sign == null)
			throw new IllegalArgumentException("the sign cannot be null");
		
		if(!sign.getLocation().toLocation().getWorld().equals(world))
			throw new IllegalArgumentException("the sign location must be"
					+ " in the same world as the structure.");
		
		Material onSign = sign.getOnState().getMaterial();
		if(onSign != Material.SIGN && onSign != Material.WALL_SIGN)
			throw new IllegalArgumentException("The sign's 'on' state must"
					+ " be a type of sign block");
		

		this.sign = sign;
		signBlock = world.getBlockAt(sign.getLocation().toLocation());
		
		if(!isValid())
			throw new IllegalArgumentException("The sign must "
					+ "exist in the minecraft world");
		
	}
	
	/**
	 * Gets the display sign of this structure.
	 * 
	 * @return	This structure's sign.
	 */
	public Block getSign() {
		return this.signBlock;
	}
	
	/**
	 * Gets the simple block location of this structure's button.
	 * 
	 * @return	This structure's button's simple location.
	 */
	public BlockLocation getSignLocation() {
		return this.sign.getLocation();
	}
	
	/**
	 * Updates the display sign's text of this structure for the player.
	 * <p>
	 * Should only be used if this structure already appears as 'on' to
	 * the player.
	 * 
	 * @param player	The player to change the sign's text for.
	 * @param text		The sign to change the text of.
	 */
	public void updateDisplay(ZamaPlayer player, SignText text) {
		try {
			//order matters here, the appearance of the sign must be
			// updated before its sign is subsequently updated
			sign.setOn(player);
			Menus.getManager().getPacketEngine().sendSignTextUpdate(player, 
					getSignLocation(), text);
		}
		catch(Exception e) {
			Zama.debug(Menus.getPlugin(), null, "An error was encountered"
					+ " while trying to send a sign update packet for " 
					+  (player != null ? "player " + player.getName() : 
						"a null player"));
			Zama.debug(Menus.getPlugin(), null, e);
		}
	}
	
	/**
	 * Checks whether the elements of the structure that need to actually
	 * exist in the minecraft world do currently exist.
	 * 
	 * @return	<code>true</code> if the necessary elements exist and this
	 * 			structure will function.
	 */
	public boolean isValid() {
		return signBlock.getType() != Material.AIR;
		/*return (signBlock.getType() == Material.SIGN 
				|| signBlock.getType() == Material.WALL_SIGN);*/
	}
	
	/**
	 * Sends update packets to the player's client to make them see the
	 * 'on' appearance of this structure.
	 * <p>
	 * Does nothing if the player and this structure are not in the same
	 * world.
	 * <p>
	 * The status of display signs are NOT an element of the on/off nature
	 * of this structure. Displays need to be edited separately following
	 * a change in state in order to trigger the update and are not saved
	 * across on/off states.
	 * 
	 * @param player	The player to display the 'on' block appearances to.
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if <code>null</code> is used.
	 */
	@Override
	public void setOn(ZamaPlayer player) {
		setBlock(player, sign, true);
		
		super.setOn(player);

	}
	
	/**
	 * Sends update packets to the player's client to make them see the
	 * 'on' appearance of this structure.
	 * <p>
	 * Does nothing if the player and this structure are not in the same
	 * world.
	 * 
	 * @param player	The player to display the 'off' block appearances to.
	 * 					Not <code>null</code>.
	 * 
	 * @throws NullPointerException	if <code>null</code> is used.
	 */
	@Override
	public void setOff(ZamaPlayer player) {
		setBlock(player, sign, false);
		
		super.setOff(player);
	}


	@Override
	public DisplayStructure copy(World world, int xOffset, int yOffset,
			int zOffset) {

		if(world == null)
			return null;
		
		Set<ToggleableBlock> offsetBlocks = new HashSet<ToggleableBlock>();
		for(ToggleableBlock tb : blocks) {
			if(tb == null) continue;
			offsetBlocks.add(tb.copy(world, xOffset, yOffset, zOffset));
		}
		
		ToggleableBlock offsetSign = sign.copy(world, xOffset, yOffset, zOffset);
		
		return new DisplayStructure(world, offsetSign, offsetBlocks);
		
	}
}
