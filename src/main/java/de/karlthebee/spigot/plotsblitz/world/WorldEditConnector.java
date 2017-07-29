package de.karlthebee.spigot.plotsblitz.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.util.AdminMode;

/**
 * The connector for worldedit to let user build inside their own plots
 * 
 * @author karlthebee
 *
 */
public class WorldEditConnector extends AbstractDelegateExtent {

	private Actor actor;

	protected WorldEditConnector(Actor actor, Extent extent) {
		super(extent);
		this.actor = actor;
	}

	public static void registerConnector() {
		WorldEdit.getInstance().getEventBus().register(new Object() {
			@Subscribe
			public void blockLogger(final EditSessionEvent event) {
				Actor actor = event.getActor();
				Extent extent = event.getExtent();

				event.setExtent(new WorldEditConnector(actor, extent));
			}
		});
	}

	public Actor getActor() {
		return actor;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(getActor().getUniqueId());
	}

	/*
	 * returns true if a block should be set So we use it to detect if a player
	 * shouldn't build
	 * 
	 * @see com.sk89q.worldedit.extent.AbstractDelegateExtent#setBlock(com.sk89q.
	 * worldedit.Vector, com.sk89q.worldedit.blocks.BaseBlock)
	 */
	@Override
	public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
		Player p = getPlayer();
		Location l = BukkitUtil.toLocation(null, location);

		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(l);
		} catch (NoPlotWorldException e) {
			return super.setBlock(location, block);
		} catch (NoPlotException e) {
			return AdminMode.isAdmin(p);
		}

		if (!AdminMode.isAdmin(p)) {
			if (!plot.canBuild(p, l))
				return false;
		}
		return super.setBlock(location, block);
	}

}