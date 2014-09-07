package net.minedcontrol.bukkit.menus.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minedcontrol.bukkit.menus.Menus;
import net.minedcontrol.bukkit.menus.MenusPlugin;
import net.minedcontrol.bukkit.menus.basis.Menu;
import net.minedcontrol.bukkit.menus.basis.MenuUnderlay;
import net.minedcontrol.bukkit.menus.basis.responses.MenuResponse;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui.ButtonInteractionInterface;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui.ButtonInteractionStructure;
import net.minedcontrol.bukkit.menus.uis.blockstructures.dynamic.buttonui.ButtonStructureConfiguration;
import net.minedcontrol.zamalib.bukkit.util.blocks.BlockLocation;
import net.minedcontrol.zamalib.messaging.messages.framework.MultilingualMessage;
import net.minedcontrol.zamalib.players.PlayerUID;
import net.minedcontrol.zamalib.players.ZamaPlayer;
import net.minedcontrol.zamalib.runtime.master.Zama;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener to handle player interaction with button menus.
 * <p>
 * Date Created: Jan 16, 2014
 * 
 * @author Brutus
 *
 */
public class ButtonMenuListener implements Listener {

	/*
	 * TODO This is a testing implementation that does not represent the
	 * ultimate behavior of the various elements that create the behavior
	 * of a button interaction menu.
	 * 
	 * Structures will be moved into their own library
	 * 
	 * Menu-activations (such as buttons and entering lobbies)
	 * will be formally implemented
	 * 
	 * Packet control will become much more advanced and be moved into its
	 * own library/engine.
	 */

	private MenusPlugin plugin;

	private Map<PlayerUID, Set<ButtonInteractionInterface>> menus;

	private ButtonStructureConfiguration structureConfig;

	//key: location of the on button
	//value: string id of the configured structure to turn on
	private Map<BlockLocation, String> onButtons;

	//key: string id of the configured structure
	//value: the menu underlay to open on the structure
	private Map<String, MenuUnderlay> dialogues;


	//TODO finish implementation, figure out the "opening" of menus.

	public ButtonMenuListener(MenusPlugin plugin) {
		this.plugin = plugin;


		try {
			structureConfig = new ButtonStructureConfiguration();
		}
		catch(Exception e) {
			String message = "Could not load the button structure "
					+ "configuration, returning null.";
			
			Zama.debug(Menus.getPlugin(), message, message);
			Zama.debug(Menus.getPlugin(), e, e);
		}

		menus = new HashMap<PlayerUID, Set<ButtonInteractionInterface>>();

		//TESTING IMPLEMENTATION, MAUALLY ADD EACH ON BUTTON TO THE MAP
		// WITH THE KEY OF THE STRUCTURE IT SHOULD ACTIVATE AS THE VALUE
		onButtons = new HashMap<BlockLocation, String>();

		onButtons.put(new BlockLocation("hub", 133, 62, -511), "teststructure");
		onButtons.put(new BlockLocation("hub", 121, 62, -523), "showystructure");

		//TESTING IMPLEMENTATION, MAUALLY ADD EACH STRUCTURE TO THE MAP
		// WITH THE KEY OF THE UNDERLAY IT SHOULD OPEN A MENU FOR WHEN
		// ACTIVATED
		dialogues = new HashMap<String, MenuUnderlay>();

		dialogues.put("teststructure",  Menus.getManager().getUnderlay("testgraph"));
		dialogues.put("showystructure", Menus.getManager().getUnderlay("showygraph"));
	}

	
	
	//TODO test other blocks as the 'real' versions of buttons and signs
	// (signs, cobwebs, open fence gates)
	
	
	
	
	
	
	//TODO implement arrow-interaction

	//TODO ?use ZamaPlayerEvents
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		//returns if not right or left clicking a block
		Action act = event.getAction();
		if(act != Action.RIGHT_CLICK_BLOCK && act != Action.LEFT_CLICK_BLOCK)
			return;

		//returns if the clicked block is not a button
		Block clicked = event.getClickedBlock();
		/*Material mat = clicked.getType();
		if(mat != Material.STONE_BUTTON && mat != Material.WOOD_BUTTON
				&& mat != Material.SIGN && mat != Material.WALL_SIGN)
			return;*/

		//return if the player is not valid
		Player p = event.getPlayer();
		if(p == null || !p.isOnline() || p.isDead())
			return;

		//returns if no zama player is found.
		ZamaPlayer zp = Zama.getZamaPlayer(p);
		if(zp == null)
			return;

		//returns if the player has no valid uid
		PlayerUID uid = zp.getUID();
		if(uid == null)
			return;


		/*if(mat == Material.STONE_BUTTON || mat == Material.WOOD_BUTTON) {
			onButtonInteract(zp, clicked);
			return;
		}

		if(mat == Material.SIGN || mat == Material.WALL_SIGN) {
			onSignInteract(zp, clicked);
			return;
		}*/
		
		//TODO does not utilize block types to narrow down interactions before
		// attempting them on the player's signs (inefficient, but useful
		// for testing different types of materials)
		onButtonInteract(zp, clicked);
		onSignInteract(zp, clicked);
	}
	
	@EventHandler
	public void onPlayerInteractAir(PlayerInteractEvent event) {
		//returns if not right or left clicking air
		Action act = event.getAction();
		if(act != Action.RIGHT_CLICK_AIR && act != Action.LEFT_CLICK_AIR)
			return;
		
		Player p = event.getPlayer();
		if(p == null || !p.isOnline() || p.isDead())
			return;
		
		if(event.getPlayer().getItemInHand().getType() != Material.ARROW)
			return;
		
		Arrow arr = p.launchProjectile(Arrow.class);
		arr.setVelocity(arr.getVelocity().multiply(3.0));
		
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if(event.getEntityType() != EntityType.ARROW)
			return;
		
		Projectile proj = event.getEntity();
		LivingEntity shooter = proj.getShooter();
		
		if(shooter.getType() != EntityType.PLAYER)
			return;
		
		Location loc = proj.getLocation();
		Block hit = loc.getBlock();
		Material mat = hit.getType();
		
		proj.remove();
		
		/*if(mat != Material.STONE_BUTTON && mat != Material.WOOD_BUTTON
				&& mat != Material.SIGN && mat != Material.WALL_SIGN)
			return;*/
		
		Player p = (Player)shooter;
		
		//returns if no zama player is found.
		ZamaPlayer zp = Zama.getZamaPlayer(p);
		if(zp == null)
			return;

		//returns if the player has no valid uid
		PlayerUID uid = zp.getUID();
		if(uid == null)
			return;
		
		/*if(mat == Material.STONE_BUTTON || mat == Material.WOOD_BUTTON) {
			onButtonInteract(zp, hit);
			return;
		}

		if(mat == Material.SIGN || mat == Material.WALL_SIGN) {
			onSignInteract(zp, hit);
			return;
		}*/

		//TODO does not utilize block types to narrow down interactions before
		// attempting them on the player's signs (inefficient, but useful
		// for testing different types of materials)
		onButtonInteract(zp, hit);
		onSignInteract(zp, hit);
	}

	public void onSignInteract(ZamaPlayer player, Block clicked) {
		BlockLocation bl = new BlockLocation(clicked.getLocation());

		Set<ButtonInteractionInterface> uis = menus.get(player.getUID());
		if(uis == null)
			return;

		for(ButtonInteractionInterface ui : uis) {
			MultilingualMessage msg = ui.signInteract(bl);
			if(msg != null)
				player.sendMessage(msg.getMessage());
		}
	}

	public void onButtonInteract(ZamaPlayer player, Block clicked) {
		BlockLocation bl = new BlockLocation(clicked.getLocation());
		openMenu(player, bl);

		Set<ButtonInteractionInterface> uis = menus.get(player.getUID());
		if(uis == null)
			return;

		for(ButtonInteractionInterface ui : uis) {
			MenuResponse response = ui.buttonInteract(bl);
			if(response == null) continue;

			//player.sendMessage(Menus.getManager().getDefaultResponse(response.getResponseType()).getMessage());
		}
	}

	/**
	 * Opens a menu for a given 'open button', if the location is an open
	 * button. Else does nothing. 
	 * 
	 * @param player	The player who clicked the button.
	 * @param loc		The location of the button.
	 */
	public void openMenu(ZamaPlayer player, BlockLocation loc) {
		String structureId = onButtons.get(loc);
		if(structureId == null)
			return;

		ButtonInteractionStructure struct = structureConfig.getStructure(structureId);
		if(struct == null)
			return;

		MenuUnderlay underlay = dialogues.get(structureId);
		if(underlay == null)
			return;
		
		//debug 
		Zama.debug(Menus.getPlugin(), null, "opening a menu for player " + player.getName());

		Menu menu = new Menu(underlay, player);
		ButtonInteractionInterface ui = new ButtonInteractionInterface(struct);
		menu.setInterface(ui);

		Set<ButtonInteractionInterface> uis = menus.get(player.getUID());
		if(uis == null)
			menus.put(player.getUID(), uis = new HashSet<ButtonInteractionInterface>());



		uis.add(ui);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		
		final ButtonInteractionStructure mainStruct = structureConfig.getStructure("mainstructure");
		final MenuUnderlay mainUnderlay = Menus.getManager().getUnderlay("maingraph");
		

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {

				Location pLoc = event.getPlayer().getLocation();
				if(!pLoc.getWorld().equals(mainStruct.getLocation().getWorld()) || 
						pLoc.distance(mainStruct.getLocation()) > 50)
					event.getPlayer().teleport(mainStruct.getLocation());
				
				//event.getPlayer().getInventory().clear();
				//event.getPlayer().getInventory().setItem(0, new ItemStack(Material.ARROW));
			}
		}, 5L);
		

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {

				ZamaPlayer player = Zama.getZamaPlayer(event.getPlayer());
				Menu menu = new Menu(mainUnderlay, player);
				
				ButtonInteractionInterface ui = new ButtonInteractionInterface(mainStruct);
				menu.setInterface(ui);

				//debug 
				Zama.debug(Menus.getPlugin(), null, "opening main menu for player " + player.getName());

				Set<ButtonInteractionInterface> uis = menus.get(player.getUID());
				if(uis == null)
					menus.put(player.getUID(), uis = new HashSet<ButtonInteractionInterface>());

				uis.add(ui);
			}
		}, 10L);
		
		for(final String structureId : onButtons.values()) {
			
			final ButtonInteractionStructure structure = structureConfig.getStructure(structureId);

			if(structure != null) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					public void run() {

						structure.hide(Zama.getZamaPlayer(event.getPlayer()));
					}
				}, 20L);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ZamaPlayer zp = Zama.getZamaPlayer(event.getPlayer());
		if(zp == null)
			return;

		menus.remove(zp.getUID());
	}
}
