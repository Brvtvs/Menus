package net.minedcontrol.bukkit.menus.underlays.graphs.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.bukkit.menus.basis.MenuElement;
import net.minedcontrol.bukkit.menus.basis.MenuOption;
import net.minedcontrol.bukkit.menus.underlays.graphs.DirectedGraphNode;
import net.minedcontrol.bukkit.menus.underlays.graphs.DirectedGraphUnderlay;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.plugins.config.Configuration;
import net.minedcontrol.zamalib.plugins.config.YAMLConfigAccessor;
import net.minedcontrol.zamalib.runtime.master.Zama;

/**
 * A configuration for directed graph menu underlays, using a file in the 
 * the subdirectory of the plugin's jar and its data folder, as well as 
 * referencing the plugin's configured multilingual messages.
 * <p>
 * Includes heavy and detailed error throwing for incorrect config 
 * formatting.
 * <p>
 * Date Created: Jan 21, 2014
 * 
 * @author Brutus
 *
 */
public class DirectedGraphConfiguration extends Configuration {

	private static final String FILE_NAME = "directedgraphs.yml";
	//stores it directly inside the plugin's data folder.
	private static final String SUBDIRECTORY = "underlays";

	private MenusPlugin plugin;

	//name of the current graph, for use in error messages
	private String currentGraphId;

	//the graphs loaded from the config
	private Map<String, DirectedGraphUnderlay> graphs;


	/**
	 * Class constructor.
	 * 
	 * @throws IllegalStateException	if the config file is incorrectly
	 * 									configured/formatted.
	 */
	public DirectedGraphConfiguration() throws IllegalStateException {

		super(new YAMLConfigAccessor(Menus.getPlugin(), FILE_NAME, 
				SUBDIRECTORY));
	}

	@Override
	protected void onLoad() throws IllegalStateException {
		if(plugin == null) plugin = Menus.getPlugin();
		this.graphs = new HashMap<String, DirectedGraphUnderlay>();

		FileConfiguration config = getConfig();

		ConfigurationSection graphsSec = config.getConfigurationSection("graphs");
		if(graphsSec == null)
			throw new IllegalStateException("Error while loading " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ ": could not find the 'graphs' section");

		Set<String> graphIds = graphsSec.getKeys(false);
		if(graphIds == null || graphIds.isEmpty())
			return;

		for(String graphId : graphIds) {
			if(graphId == null || graphId.equals("")) continue;

			currentGraphId = graphId;

			DirectedGraphNode graphStart = parseGraph(graphsSec.getConfigurationSection(graphId), 
					null, null, null);

			if(graphStart != null)
				graphs.put(graphId, graphStart.getUnderlay());

		}
	}


	

	/**
	 * A method that recursively parses a graph, node by node.
	 * 
	 * @param sec			The configuration section of the node to parse.
	 * @param underlay		The underlay the node should be a part of.
	 * 						<code>null</code> to create a new underlay.
	 * @param namedNodes	A map of the nodes within the graph underlay 
	 * 						that have a defined unique id.
	 * 						Keyed by the unique id with the node object
	 * 						as the value.
	 * 						Should be <code>null</code> when 
	 * 						<code>underlay</code> is <code>null</code>.
	 * @param references	A set of attempted references within the graph
	 * 						underlay.
	 * 						Should be <code>null</code> when 
	 * 						<code>underlay</code> is <code>null</code>.
	 * @return				A node parsed from the config section.
	 * 
	 * @throws NullPointerException		if the config section is <code>null</code>.
	 * @throws IllegalStateException	if the config is incorrectly 
	 * 									formatted or one of its references
	 * 									cannot be resolved.
	 */
	private DirectedGraphNode parseGraph(ConfigurationSection sec, 
			DirectedGraphUnderlay underlay, 
			Map<String, DirectedGraphNode> namedNodes,
			Set<ReferenceAttempt> references) 
					throws NullPointerException,IllegalStateException {
		
		//This uses very specific code closely reliant on a specific 
		// formatting of the configuration file.

		if(sec == null)
			throw new NullPointerException("section cannot be null");

		
		//--gets the unique id for the node, if any--
		String id = sec.getString("uniqueid");

		
		//--gets the title for the node--
		String titleNameId = sec.getString("title.name");
		if(titleNameId == null || titleNameId.equals(""))
			throw new IllegalStateException("Error while loading a graph node in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ "no title name id found");

		String titleDescId = sec.getString("title.description");
		if(titleDescId == null || titleDescId.equals(""))
			throw new IllegalStateException("Error while loading a graph node in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ ": no title description id found");

		MenuElement title = getTitleElement(titleNameId, titleDescId);
		
		
		
		//--creates the node, and the underlay if necessary--
		
		DirectedGraphNode node = null;
		boolean headNode = false; // whether this is the head node
	
		//if this is the first node and the underlay has not been 
		// created yet
		if(underlay == null) {
			namedNodes = new HashMap<String, DirectedGraphNode>();
			references = new HashSet<ReferenceAttempt>();
			
			underlay = new DirectedGraphUnderlay(currentGraphId, title);
			node = underlay.getStart(null);
			headNode = true;

		//else makes this part of the existing underlay
		} else {
			node = new DirectedGraphNode(underlay, title);
		}

		//adds the new node to the map of named nodes, if a unique id was
		// defined. Throws an error if the id is a duplicate (not unique).
		if(id != null && !id.equals("")) {
			if(namedNodes.containsKey(id))
				throw new IllegalStateException("Error while loading a graph node in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
						+ " in graph " + currentGraphId + ": " 
						+ "a duplicated 'uniqueid' field was encountered");
			
			namedNodes.put(id, node);
		}
		
		

		//--gets the options for the node--

		ConfigurationSection optionsSec = sec.getConfigurationSection("options");
		if(optionsSec == null) 
			throw new IllegalStateException("Error while loading a graph node in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ "no options found");
		
		//the set option keys are arbitrary. The suggested names are '1', 
		// '2', etc., but they are unused except to navigate the config tree.
		Set<String> optionKeys = optionsSec.getKeys(false);
		if(optionKeys == null || optionKeys.isEmpty())
			throw new IllegalStateException("Error while loading a graph node in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ "no options found");

		
		
		
		//--for each option, gets the child of the option, if any, and
		// adds the option to the node.--
		for(String optionKey : optionKeys) {

			//debug
			Zama.debug(Menus.getPlugin(), null, "Graph loading: "
					+ "key: " + optionKey);
			
			ConfigurationSection optionSec = optionsSec.getConfigurationSection(optionKey);
			
			//gets the option from its defined id
			String optionId = optionSec.getString("option");
			if(optionId == null || optionId.equals(""))
				throw new IllegalStateException("Error while loading a menu option in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
						+ " in graph " + currentGraphId + ": " 
						+ "no option id found");
				
			MenuOption option = Menus.getManager().getOption(optionId);
			if(option == null)
				throw new IllegalStateException("Error while loading a menu option in " 
						+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
						+ " in graph " + currentGraphId + ": " 
						+ "no option for the id " + optionId + " was found");

			
			
			//if a reference to another node in the graph is being 
			// attempted, adds the option with no child, and then adds the 
			// attempt to the unresolved reference set.
			String reference = optionSec.getString("child.goto");
			if(reference != null && !reference.equals("")) { // base case 1
				
				node.addChild(option, null);
				references.add(new ReferenceAttempt(node, option, reference));

				//debug
				Zama.debug(Menus.getPlugin(), null, "Graph loading: "
						+ "encountered a reference to another node in " 
						+ currentGraphId);
			} else { 
			//else if there is a child defined, but it is not a reference, 
			// treat the child as its own node definition and make a 
			// recursive call on it to parse it into a node.

				ConfigurationSection childSec = optionSec.getConfigurationSection("child");
				if(childSec != null) {
					DirectedGraphNode child = parseGraph(childSec, underlay, 
							namedNodes, references);

					//debug
					Zama.debug(Menus.getPlugin(), null, "Graph loading: "
							+ "encountered a child node in " + currentGraphId);
					
					//adds the result of the recursive call to the node as a child.
					node.addChild(option, child);

				//else no child is defined, treat the option as a leaf option
				// that ends the menu dialogue when selected.
				} else { // base case 2

					//debug
					Zama.debug(Menus.getPlugin(), null, "Graph loading: "
							+ "encountered a leaf node in " + currentGraphId);
					
					node.addChild(option, null);

				}
			}
		}

		//if this is the head node, and the underlay as a whole is about to
		// be returned to the non-recursive client.
		if(headNode) {
			
			//attempts to resolve any node references made.
			for(ReferenceAttempt attempt : references) {
				if(attempt == null) continue;
				
				DirectedGraphNode referenced = namedNodes.get(attempt.getReferencedId());
				if(referenced == null)
					throw new IllegalStateException("Error while referencing a node in " 
							+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
							+ " in graph " + currentGraphId + ": " 
							+ "no node found in graph " + currentGraphId 
							+ " with the id " + attempt.getReferencedId());
				
				//makes the reference
				attempt.getReferencer().setChild(attempt.getOption(), referenced);
					
			}
			
		}
		
		//returns the node after it has been successfully defined and each
		// of its options and any of their children have been associated
		// with it.
		return node;
	}
	
	
	/**
	 * Gets a title menu element from the ids of its component multilingual
	 * messages.
	 * 
	 * @param nameId	The string id of the multilingual message for the
	 * 					title's name. Not <code>null</code>.
	 * @param descId	The string id of the multilingual message for the
	 * 					title's description. Not <code>null</code>.
	 * @return			A title element with the defined elements, if found.
	 * 
	 * @throws NullPointerException		on a <code>null</code> parameter.
	 * @throws IllegalStateException	if the defined messages are not 
	 * 									found in the language configuration.
	 */
	private MenuElement getTitleElement(String nameId, String descId) 
			throws NullPointerException,IllegalStateException {

		if(nameId == null || descId == null)
			throw new NullPointerException("ids cannot be null");

		MultilingualMessage name = plugin.getLanguageSettings().getMultilingualMessage(nameId);
		if(name == null)
			throw new IllegalStateException("Error while loading a title name in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ "no message with the name '" + nameId 
					+ "' was found in Menus' language configuration");


		MultilingualMessage desc = plugin.getLanguageSettings().getMultilingualMessage(descId);
		if(desc == null)
			throw new IllegalStateException("Error while loading a title description in " 
					+ ((SUBDIRECTORY != null ? "/" + SUBDIRECTORY + "/" : "") + FILE_NAME) 
					+ " in graph " + currentGraphId + ": " 
					+ "no message with the name '" + descId 
					+ "' was found in Menus' language configuration");

		//TODO add ability to define color and/or item for display
		return new MenuElement(name, desc, null, null);
	}


}
