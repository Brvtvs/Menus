package net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui;

import java.util.HashSet;
import java.util.Set;

import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.DisplayStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.DynamicStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.ToggleableStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.blocks.ToggleableBlock;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.players.ZamaPlayer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A toggleable, dynamic structure with a sign display that can be interacted
 * with using a button.
 * <p>
 * Date Created: Jan 10, 2014
 * 
 * @author Brutus
 *
 * @see DynamicStructure
 * @see ToggleableStructure
 * @see DisplayStructure
 */
public class ButtonOptionStructure extends DisplayStructure {
	
	private ToggleableBlock button;
	
	private Block buttonBlock;
	
	//for avoiding checking the button before it is identified (when
	// constructing the super constructor)
	private boolean checkBlock;
	
	
	/**
	 * Class constructor. All parameters must be in the same minecraft 
	 * world.
	 * 
	 * @param world		The world this structure is in. Not <code>null</code>.
	 * @param button	The button for this structure, 
	 * 					not <code>null</code> and must actually exist as a 
	 * 					non-air block in the minecraft world. Must also 
	 * 					have an 'on' state that is a type of button.
	 * @param sign		The sign for this structure, 
	 * 					not <code>null</code> and must actually exist as a 
	 * 					non-air block in the minecraft world. Must also 
	 * 					have an 'on' state that is a type of sign.
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
	 * 									sign or button do not exist in
	 * 									the minecraft world or if their 
	 * 									'on' state is not a sign and button 
	 * 									respectively, or if the parameters 
	 * 									are not located in the same world.
	 */
	public ButtonOptionStructure(World world, ToggleableBlock button, ToggleableBlock sign, 
			Set<ToggleableBlock> base) throws IllegalArgumentException {
		
		super(world, sign, base);
		
		if(button == null)
			throw new IllegalArgumentException("button cannot be null");
		
		if(!button.getLocation().toLocation().getWorld().equals(world))
			throw new IllegalArgumentException("the button location must be"
					+ " in the same world as the structure.");
		
		Material onButton = button.getOnState().getMaterial();
		if(onButton != Material.STONE_BUTTON && onButton != Material.WOOD_BUTTON)
			throw new IllegalArgumentException("The button's 'on' state must"
					+ " be a type of button block");
		
		
		this.button = button;
		buttonBlock = world.getBlockAt(button.getLocation().toLocation());
		checkBlock = true;
		
		if(!isValid())
			throw new IllegalArgumentException("The button must "
					+ "exist in the minecraft world");
	}
	
	/**
	 * Gets the button of this structure.
	 * 
	 * @return	This structure's button block.
	 */
	public Block getButton() {
		return this.buttonBlock;
	}
	
	/**
	 * Gets the simple block location of this structure's button.
	 * 
	 * @return	This structure's button's simple location.
	 */
	public BlockLocation getButtonLocation() {
		return this.button.getLocation();
	}
	
	@Override
	public void setOn(ZamaPlayer player) {
		super.setOn(player);
		
		setBlock(player, button, true);
	}
	
	@Override
	public void setOff(ZamaPlayer player) {
		super.setOff(player);
		
		setBlock(player, button, false);
	}

	@Override
	public boolean isValid() {
		return (super.isValid() && (!checkBlock || 
				buttonBlock.getType() != Material.AIR));
		/*return (super.isValid() && (!checkBlock || 
				(buttonBlock.getType() == Material.STONE_BUTTON || 
				buttonBlock.getType() == Material.WOOD_BUTTON)));*/
	}
	

	@Override
	public ButtonOptionStructure copy(World world, int xOffset, int yOffset,
			int zOffset) {

		if(world == null)
			return null;
		
		Set<ToggleableBlock> offsetBlocks = new HashSet<ToggleableBlock>();
		for(ToggleableBlock tb : blocks) {
			if(tb == null) continue;
			offsetBlocks.add(tb.copy(world, xOffset, yOffset, zOffset));
		}
		
		ToggleableBlock offsetSign = sign.copy(world, xOffset, yOffset, 
				zOffset);
		ToggleableBlock offsetButton = button.copy(world, xOffset, yOffset, 
				zOffset);
		
		return new ButtonOptionStructure(world, offsetButton,
				offsetSign, offsetBlocks);
		
	}
	

}
