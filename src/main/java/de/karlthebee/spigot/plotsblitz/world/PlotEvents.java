package de.karlthebee.spigot.plotsblitz.world;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.util.AdminMode;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.PBUitl;

/**
 * An class for all events that could happen in that world and how to catch them
 * 
 * @author karlthebee
 *
 */
public class PlotEvents implements Listener {

	static final List<Material> blacklist = Arrays.asList(Material.FIRE, Material.FIREBALL, Material.LAVA,
			Material.LAVA_BUCKET, Material.ENDER_PORTAL, Material.STATIONARY_LAVA, Material.ENDER_PORTAL_FRAME,
			// Material.PISTON_BASE, Material.PISTON_EXTENSION,
			// Material.PISTON_MOVING_PIECE, Material.PISTON_STICKY_BASE,
			Material.LINGERING_POTION, Material.SPLASH_POTION);

	// private EventDatabaseConnector manager = new EventDatabaseConnector();

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();

		if (AdminMode.isAdmin(p))
			return;

		try {
			Plot plot = BukkitDatabaseConnector.getPlot(p);
			if (!plot.canBuild(p))
				event.setCancelled(true);
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void Blockbreakkvent(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();

		if (AdminMode.isAdmin(p))
			return;

		try {
			Plot plot = BukkitDatabaseConnector.getPlot(b.getLocation());
			if (!plot.canBuild(p, b.getLocation()))
				throw new NoPlotException();
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlockPlaced();

		if (AdminMode.isAdmin(p))
			return;

		Plot plot;
		try {
			plot = BukkitDatabaseConnector.getPlot(b.getLocation());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			p.sendMessage(Color.ERROR + "You can't place blocks here");
			event.setCancelled(true);
			return;
		}
		if (!plot.canBuild(p, b.getLocation())) {
			p.sendMessage(Color.ERROR + "You can't place blocks here");
			event.setCancelled(true);
			return;
		}
		if (isForbidenBlock(p, b)) {
			return;
		}
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		Entity e = event.getEntity();
		try {
			BukkitDatabaseConnector.getPlot(e.getLocation());
			if (!(e instanceof LivingEntity))
				throw new NoPlotException();
			((LivingEntity) e).setAI(false);
		} catch (NoPlotWorldException e1) {
			return;
		} catch (NoPlotException e1) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();

		if (AdminMode.isAdmin(p))
			return;

		// physical things
		if (event.getAction() == Action.PHYSICAL)
			return;
		if (event.getItem() == null) {
			return;
		}
		if (event.getItem().getType().isBlock()) {
			return;
		}

		Plot plot = null;
		try {
			Location l = (b != null) ? b.getLocation() : p.getLocation();
			plot = BukkitDatabaseConnector.getPlot(l);
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			// plot is null, should be enough
		}

		// Fixing bug that water can be placed towards ground fences and are out
		// of fence
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getItem().getType() == Material.LAVA_BUCKET
				|| event.getItem().getType() == Material.WATER_BUCKET)) {
			if (plot != null && !plot.canBuild(p, b.getLocation())) {
				event.setCancelled(true);
			}
		}

	}

	private boolean isForbidenBlock(Player p, Block b) {
		for (Material m : blacklist) {
			if (b.getType().equals(m)) {
				p.sendMessage(ChatColor.RED + "It's forbidden to use " + Color.INFO + PBUitl.formatBukkitEnum(m)
						+ ChatColor.RED + "");
				return false;
			}
		}
		return true;
	}

	@EventHandler
	public void onWaterFlow(BlockFromToEvent event) {
		// Block from = event.getBlock();
		Block to = event.getToBlock();
		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(to.getLocation());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			event.setCancelled(true);
			return;
		}
		// DEPRECATED CODE ?
		// if (b.getType() == Material.WATER || b.getType() ==
		// Material.STATIONARY_WATER) {
		// event.setCancelled(true);
		// }
		if (!plot.canBuildLocation(to.getLocation())) {
			event.setCancelled(true);
			return;
		}
	}

	/**
	 * Tree grow events
	 */
	@EventHandler
	public void onTreeGrow(StructureGrowEvent event) {
		List<BlockState> blocks = event.getBlocks();
		Iterator<BlockState> it = blocks.iterator();
		// player can be null if tree growed automatically
		Player p = event.getPlayer();

		if (AdminMode.isAdmin(p))
			return;

		// TODO test if this is the trees origin plot
		Plot originPlot = null;
		try {
			originPlot = BukkitDatabaseConnector.getPlot(event.getLocation());
		} catch (NoPlotWorldException | NoPlotException e1) {
			// ignore
		}

		while (it.hasNext()) {
			BlockState state = it.next();
			Block block = state.getBlock();

			try {
				Plot plot = BukkitDatabaseConnector.getPlot(block.getLocation());
				// remove if the player hasn't got the right to grow or the
				// tree plants over his own plot
				if (!plot.canBuild(p, block.getLocation()) || !plot.equals(originPlot)) {
					it.remove();
				}
			} catch (NoPlotWorldException | NoPlotException e) {
				//
				it.remove();
			}

		}
	}

	/**
	 * Dispenser events
	 */
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent event) {
		Block b = event.getBlock();

		try {
			BukkitDatabaseConnector.getPlot(b.getLocation());
		} catch (NoPlotWorldException | NoPlotException e) {
			return;
		}

		event.setVelocity(new Vector(0, 0, 0));
		// TODO cancel dispense event?
	}

	/*
	 * Disable explode on
	 */
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		try {
			BukkitDatabaseConnector.getPlot(event.getLocation());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
		}
		event.setCancelled(true);
	}

	/**
	 * disbable every event
	 */
	@EventHandler
	public void onArrowEvent(EntityShootBowEvent event) {
		// TODO cancel?
		// event.setCancelled(true);
	}

	@EventHandler
	public void onAnimalHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity victim = event.getEntity();

		if (damager instanceof Player && AdminMode.isAdmin((Player) damager))
			return;

		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(victim.getLocation());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
		}
		if (damager.getType() != EntityType.PLAYER) {
			event.setCancelled(true);
		} else {
			if (plot != null && !plot.canBuild((Player) damager)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		Vehicle vehicle = event.getVehicle();

		try {
			BukkitDatabaseConnector.getPlot(event.getTo());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
		}

		/*
		 * for (Entity e : vehicle.getPassenger()) {
		 * 
		 * if (e.getType() == EntityType.PLAYER) return; }
		 */
		for (Entity e : vehicle.getPassengers()) {
			if (e.getType() == EntityType.PLAYER)
				return;
		}
		vehicle.remove();
	}

	/**
	 * Potion event
	 */
	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion e = event.getEntity();
		ProjectileSource shooter = e.getShooter();

		if (shooter instanceof Player && AdminMode.isAdmin((Player) shooter))
			return;

		Plot plot = null;

		try {
			plot = BukkitDatabaseConnector.getPlot(e.getLocation());
		} catch (NoPlotWorldException e1) {
			return;
		} catch (NoPlotException e1) {
		}
		if (shooter instanceof Player) {
			if (plot != null && plot.canBuild((Player) shooter, e.getLocation()))
				return;
			((Player) shooter).sendMessage(Color.ERROR + "You can't throw potions");
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onExpBottle(ExpBottleEvent event) {
		ThrownExpBottle bottle = event.getEntity();

		try {
			BukkitDatabaseConnector.getPlot(bottle.getLocation());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {

		}
		event.setExperience(0);
		event.setShowEffect(false);
	}

	@EventHandler
	public void onPistonPush(BlockPistonExtendEvent event) {

		Block block = event.getBlock();

		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(block);
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			event.setCancelled(true);
			return;
		}

		List<Block> blocks = event.getBlocks();

		for (Block b : blocks) {
			try {
				plot = BukkitDatabaseConnector.getPlot(b);
				if (!plot.canBuildLocation(b.getLocation())) {
					event.setCancelled(true);
					return;
				}
			} catch (NoPlotWorldException e) {
				Plotsblitz.logger()
						.severe("got an piston push event which is in an qb world, but one block moved isn't ("
								+ b.toString() + ")");
			} catch (NoPlotException e) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPistonShrink(BlockPistonRetractEvent event) {
		try {
			BukkitDatabaseConnector.getPlot(event.getBlock());
		} catch (NoPlotWorldException | NoPlotException e) {
			return;
		}
		Iterator<Block> blocks = event.getBlocks().iterator();

		while (blocks.hasNext()) {
			Block b = blocks.next();
			Plot p;
			try {
				p = BukkitDatabaseConnector.getPlot(b);
				if (!p.canBuild(null, b.getLocation())) {
					event.setCancelled(true);
					return;
				}
			} catch (NoPlotWorldException e) {
				// should not happen, because above there's a return statement
				// if no world's there
				e.printStackTrace();
			} catch (NoPlotException e) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onBlockFall(EntityChangeBlockEvent event) {
		try {
			BukkitDatabaseConnector.getPlot(event.getBlock());
		} catch (NoPlotWorldException e) {
			return;
		} catch (NoPlotException e) {
			event.setCancelled(true);
			return;
		}

		if ((event.getEntityType() == EntityType.FALLING_BLOCK)) {
			event.getBlock().setType(Material.AIR);
			event.setCancelled(true);
		}
	}
}
