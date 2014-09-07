package net.minedcontrol.bukkit.menus.uis.blockstructures;

/**
 * A collection of blocks that together make up a structure.
 * <p>
 * Block structures face a challenge in being used in UIs because they 
 * have a per-server state, rather than a per-viewer state. Possible
 * implementations include per-user perspective changing with faked
 * block update packets, or structures that are only accessible by
 * one player at a time. Alternatively, a structure could be used in
 * a situation where all viewers represented one menu 'user' and saw the
 * same menu at the same time.
 * <p>
 * Date Created: Jan 25, 2014
 * 
 * @author Brutus
 *
 */

public abstract class BlockStructure implements CopyableStructure {
	
	/*
	 * Currently has no actual generic implementation as there is no
	 * currently shared aspects of the dynamic extension of this
	 * idea and any other implementation, but this does represent a logical
	 * parent/superclass of a dynamic structure. (January 25, 2014)
	 */

}
