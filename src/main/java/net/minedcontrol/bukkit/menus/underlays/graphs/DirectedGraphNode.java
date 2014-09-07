package net.minedcontrol.bukkit.menus.underlays.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuElement;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.basis.MenuStage;
import net.minedcontrol.bukkit.menus.basis.UnderlayNode;

/**
 * A node in a realized directed graph menu underlay. Contains a set of 
 * options which either lead to a child node or an end of the graph. Cannot 
 * get parent nodes from this object, as it is directed and does not store 
 * or allow access to its parents.
 * <p>
 * Does NOT prevent cycling. There is a many-to-many relationship between
 * parent and child which does not account for or prevent cycling. This
 * can be useful for more complex menu structures, provided the graph
 * is correctly constructed.
 * <p>
 * Does prevent relationships between two different graph underlays. Two 
 * nodes in a structure must share the same underlay wrapper object in 
 * order to be parent and child. This is largely to preserve safety, 
 * sanity, and problems that are excessively hard to diagnose.
 * <p>
 * Does not ensure that all nodes of the graph underlay are reachable. It 
 * is possible to exclude or remove intermediary connections between parts 
 * of the graph, temporarily or permanently, and cause a portion to be 
 * totally disconnected and unreachable from the user-defined start of the 
 * underlay. In this case it would no longer technically be a single graph 
 * structure, even though the nodes are considered part of the same 
 * underlay. Disconnected nodes cannot be repurposed except as 
 * children/parents within the same underlay, as their underlay cannot be 
 * changed post-construction. 
 * <p>
 * All nodes are technically disconnected when they are constructed even 
 * though they are considered as part of a graph underlay wrapper, and it 
 * is up to the user to connect or not connect them to parents and 
 * children. However, it is also possible to create potentially large 
 * structures within a directed graph underlay that are disconnected or 
 * subsequently rendered to be disconnected, temporarily or permanently, 
 * simply by not creating or by removing a path of parent-child 
 * relationships from the user-defined "start" node to the head of the 
 * disconnecting structure. An example of the flexibility from this lack of 
 * restraint is that a graph could undergo major changes in structure 
 * during runtime, and potentially enormous portions of the graph could be 
 * arbitrarily removed, reincorporated, an reorganized as desired.
 * <p>
 * Date Created: Jan 20, 2014
 * 
 * @author Brutus
 *
 * @see DirectedGraphUnderlay
 */
public class DirectedGraphNode implements UnderlayNode {
	
	//The underlay this graph is a part of.
	private final DirectedGraphUnderlay underlay;
	
	//The title of this node, how it is presented when actually displayed
	// in a menu interface.
	private final MenuElement title;
	
	//An ordered collection of all of the options of this node. 
	// This collection is meant primarily to generate the MenuStage and 
	// provide the options in order for the frontend. The options of this 
	// node are stored/referenced twice, once here and once in one of the 
	// other following collections, which govern the structure of the 
	// options and the behavior resulting from selections.
	private final Queue<MenuOption> optQueue;
	
	//options that lead to a child node. (<option, child>)
	private final Map<MenuOption, DirectedGraphNode> children;
	//options that do not lead to another node, that mark an end of the 
	// graph structure.
	private final Set<MenuOption> leaves;
	
	
	//a non-final object that summarizes the display information of this
	// node for menu interfaces. Refreshed on-edit as options/children
	// are added and removed.
	private MenuStage stage;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param underlay	The underlay this node is a part of. Not
	 * 					<code>null</code>
	 * @param title		The title of this node. Not <code>null</code>.
	 * 	
	 * @throws IllegalArgumentException	on a <code>null</code> parameter.
	 */
	public DirectedGraphNode(DirectedGraphUnderlay underlay, 
			MenuElement title) throws IllegalArgumentException {
		
		
		if(underlay == null || title == null)
			throw new IllegalArgumentException("params cannot be null");
		
		this.underlay = underlay;
		this.title = title;
		
		this.optQueue = new LinkedBlockingQueue<MenuOption>();
		this.children = new HashMap<MenuOption, DirectedGraphNode>();
		this.leaves = new HashSet<MenuOption>();
		
		updateStage();
	}
	
	
	//--------
	//PUBLIC
	//--------
	
	/**
	 * Adds a child to the node along with the option that, when selected,
	 * will yield the path to that child or an end of the graph. Both nodes
	 * must be in the same underlay.
	 * <p>
	 * Accepts an option without a node to add an option that yields 
	 * nothing (a leaf node).
	 * <p>
	 * Overwrites the previous state and position of the option if it was 
	 * already present.
	 * 
	 * @param selection	The menu option that will return this child as a
	 * 					result of being selected.
	 * 					Not <code>null</code>.
	 * @param result	What node the result of selecting the option will 
	 * 					be.	Can be <code>null</code> to create a leaf node 
	 * 					(the end of a path through the graph). 
	 * 
	 * @throws NullPointerException		if the option is <code>null</code>.
	 * @throws IllegalArgumentException	if the result node is not in the
	 * 									same underlay as this node.
	 */
	public final void addChild(MenuOption selection, DirectedGraphNode result) 
			throws NullPointerException,IllegalArgumentException {
		
		if(selection == null) 
			throw new NullPointerException("option cannot be null");
		
		if(result != null && !underlay.equals(result.getUnderlay()))
			throw new IllegalArgumentException("nodes need to be part of"
					+ " the same underlay to become parent and child");
		
		//removes any old instances of the option to overwrite its
		// previous state.
		if(leaves.remove(selection) || children.remove(selection) != null) 
			optQueue.remove(selection);
		
		if(result == null)
			leaves.add(selection);
		
		else {
			children.put(selection, result);
		}
		
		optQueue.add(selection);
		
		updateStage();
	}
	
	/**
	 * Sets or removes the child of an option that is already a part of 
	 * this node, such that the option retains its position in the ordering 
	 * of the node's options rather than removing/readding it, which would 
	 * put it at the end.
	 * <p>
	 * If the option is NOT currently in the graph, it is added at the
	 * end of the set of options.
	 * 
	 * @param option	The menu option whose child should be overwritten,
	 * 					or added if it had no child previously.
	 * 					Not <code>null</code>.
	 * @param result	What node the result of selecting the option will 
	 * 					be.	Can be <code>null</code> to create a leaf node 
	 * 					(the end of a path through the graph). 
	 * 
	 * @throws NullPointerException		if the option is <code>null</code>.
	 * @throws IllegalArgumentException	if the result node is not in the
	 * 									same underlay as this node.
	 */
	public final void setChild(MenuOption option, DirectedGraphNode result)
			throws NullPointerException,IllegalArgumentException {

		if(option == null) 
			throw new NullPointerException("option cannot be null");
		
		if(result != null && !underlay.equals(result.getUnderlay()))
			throw new IllegalArgumentException("nodes need to be part of"
					+ " the same underlay to become parent and child");
		
		//if the option was not in the queue, just adds it normally and
		// returns
		if(!optQueue.contains(option)) {
			addChild(option, result);
			return;
		}
		
		//else is overwriting a previous state, and either sets it as
		// as a part of the children map or as a leaf with no child
		if(result == null) {
			children.remove(option);
			leaves.add(option);
			
		} else {
			leaves.remove(option);
			children.put(option, result);
		}
		
		updateStage();
	}
	
	/**
	 * Removes an option from the node as well as the child it led to,
	 * if any.
	 * 
	 * @param option	The option to remove.
	 * @return			<code>true</code> if the node contained the element.
	 */
	public final boolean removeOption(MenuOption option) {
		if(option == null) 
			return false;
		
		if(leaves.remove(option)) {
			optQueue.remove(option);
			updateStage();
			return true;
		}

		//removes the child from the children map of this node.
		DirectedGraphNode child = children.remove(option);
		if(child != null) {
			optQueue.remove(option);
			updateStage();
			return true;
		}
		
		else return false;
	}

	@Override
	public final MenuStage getMenuStage(Menu menu) {
		return this.stage;
	}

	@Override
	public final UnderlayNode getNext(Menu menu, MenuOption selection)
			throws IllegalArgumentException {
		
		if(selection == null)
			throw new IllegalArgumentException("invalid selection");
			
		DirectedGraphNode child = children.get(selection);
		
		if(child == null && !leaves.contains(selection))
			throw new IllegalArgumentException("invalid selection");
		
		//returns the child; returns null if the selection is part of
		// this node, but represents a leaf with no child.
		return child;
	}

	/**
	 * Gets the menu underlay of this node.
	 * 
	 * @return	This node's menu underlay.
	 */
	public final DirectedGraphUnderlay getUnderlay() {
		return this.underlay;
	}
	
	
	//--------
	//PRIVATE
	//--------
	
	/**
	 * Private helper method that updates the menu stage contained in this
	 * node. Should be called whenever the elements of the node are edited.
	 */
	private void updateStage() {
		List<MenuOption> options = new ArrayList<MenuOption>(optQueue);
		this.stage = new MenuStage(title, options);
	}


}
