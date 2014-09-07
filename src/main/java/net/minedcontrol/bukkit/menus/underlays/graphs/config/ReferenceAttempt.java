package net.minedcontrol.bukkit.menus.underlays.graphs.config;

import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.underlays.graphs.DirectedGraphNode;

/**
 * Stores information about a desired reference in a directed graph until 
 * a resolution can be attempted.
 * <p>
 * Date Created: Jan 22, 2014
 * 
 * @author Brutus
 *
 */

public final class ReferenceAttempt {
	

	private final DirectedGraphNode referencer;
	private final MenuOption option;
	private final String referencedId;
	
	/**
	 * Class constructor.
	 * 
	 * @param referencer	The node attempting the reference. 
	 * @param option		The option that would lead to the referenced 
	 * 						node.	
	 * @param referencedId	the unique (within a graph underlay) id of the 
	 * 						node to reference.
	 * 
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public ReferenceAttempt(DirectedGraphNode referencer, MenuOption option, 
			String referencedId) throws IllegalArgumentException {
		
		if(referencer == null || option == null || referencedId == null)
			throw new IllegalArgumentException("params cannot be null");
		
		this.referencer = referencer;
		this.option = option;
		this.referencedId = referencedId;
	}
	
	/**
	 * Gets the node that attempted the reference. 
	 * 
	 * @return The node that attempted the reference. 
	 */
	public DirectedGraphNode getReferencer() { return this.referencer; }
	
	/** 
	 * Gets the option that would lead to the referenced node.
	 * 
	 * @return	The option that would lead to the referenced node.
	 */
	public MenuOption getOption() { return this.option; }
	
	/**
	 * Gets the id of the node to attempt a reference to.
	 * 
	 * @return	the id of the node to reference.
	 */
	public String getReferencedId() { return this.referencedId; }

}
