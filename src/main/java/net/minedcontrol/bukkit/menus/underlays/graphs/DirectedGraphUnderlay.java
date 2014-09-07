package net.minedcontrol.bukkit.menus.underlays.graphs;


import java.util.HashSet;
import java.util.Set;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuElement;
import net.minedcontrol.bukkit.menus.basis.MenuUnderlay;

/**
 * A wrapper and manager for a set of underlay nodes organized into a
 * directed graph. This is a realized underlay that does not support
 * virtualization or context-specific options.
 * <p>
 * Also serves as a collection of the menus currently traversing this
 * underlay.
 * <p>
 * Date Created: Jan 20, 2014
 * 
 * @author Brutus
 *
 * @see DirectedGraphNode
 */

public class DirectedGraphUnderlay implements MenuUnderlay {
	
	private final String id;
	// The starting point of the underlay. User-defined, not inherent
	// in a directed graph.
	private final DirectedGraphNode start; 
	
	private Set<Menu> menus;
	
	/**
	 * Class constructor. Creates a new directed graph menu underlay, 
	 * starting by constructing its head element (the starting point).
	 * 
	 * @param underlayId	The string id of this underlay. Should be unique
	 * 						within a runtime and is used to access this
	 * 						underlay from the menu manager singleton.
	 * @param headNodeTitle	The title of the first node in the underlay.
	 * 
	 * @throws IllegalArgumentException	On a <code>null</code> parameter
	 * 									or an empty id.
	 */
	public DirectedGraphUnderlay(String underlayId, 
			MenuElement headNodeTitle) throws IllegalArgumentException {
		
		if(headNodeTitle == null || underlayId == null)
			throw new IllegalArgumentException("params cannot be null");
		
		if(underlayId.equals(""))
			throw new IllegalArgumentException("id cannot be empty");
		
		this.id = underlayId;
		this.start = new DirectedGraphNode(this, headNodeTitle);
		this.menus = new HashSet<Menu>();
		
		Menus.getManager().addUnderlay(this);
	}

	@Override
	public final DirectedGraphNode getStart(Menu menu) {
		return start;
	}

	@Override
	public final Set<Menu> getMenus() {
		return new HashSet<Menu>(menus);
	}

	@SuppressWarnings("deprecation")
	@Override
	public final boolean addMenu(Menu menu) {
		if(!equals(menu.getUnderlay()))
			return false;
		
		return menus.add(menu);
	}

	@Override
	public final boolean removeMenu(Menu menu) {
		return menus.remove(menu);
	}

	@Override
	public final String getId() {
		return this.id;
	}

}
