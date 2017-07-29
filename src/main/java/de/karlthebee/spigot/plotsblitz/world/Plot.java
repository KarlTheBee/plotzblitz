package de.karlthebee.spigot.plotsblitz.world;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.PlotData;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.AdminMode;

/**
 * A class containing world- and plotdata Used for combining those data and
 * calculate ...stuff
 *
 */
public class Plot {

	private WorldData world;
	private PlotData plot;

	/**
	 * Creates an plot object with it's world and plot
	 * 
	 * @param world
	 *            the world of the plot
	 * @param plot
	 *            the plot itself
	 */
	public Plot(WorldData world, PlotData plot) {
		super();
		this.world = Objects.requireNonNull(world, "World data is null");
		this.plot = Objects.requireNonNull(plot, "Plot data is null");
	}

	public WorldData getWorldData() {
		return world;
	}

	public PlotData getPlot() {
		return plot;
	}

	public World getWorld() {
		return Bukkit.getWorld(getWorldData().getWorld());
	}

	public int getCenterX() {
		return (getLowerX() + getHigherX()) / 2;
	}

	public int getCenterZ() {
		return (getLowerZ() + getHigherZ()) / 2;
	}

	public int getHigherX() {
		return BukkitDatabaseConnector.getMaxLocationFromPlot(getWorldData(), plot.getxChunk())
				+ getSize(BlockFace.EAST);
	}

	public int getHigherZ() {
		return BukkitDatabaseConnector.getMaxLocationFromPlot(getWorldData(), plot.getzChunk())
				+ getSize(BlockFace.SOUTH);
	}

	public int getLowerX() {
		return BukkitDatabaseConnector.getMinLocationFromPlot(getWorldData(), plot.getxChunk())
				+ getSize(BlockFace.WEST);
	}

	public int getLowerZ() {
		return BukkitDatabaseConnector.getMinLocationFromPlot(getWorldData(), plot.getzChunk())
				+ getSize(BlockFace.NORTH);
	}

	public String getOwner() {
		if (getPlot().isSystem()) {
			return "System";
		}
		return Bukkit.getOfflinePlayer(getPlot().getOwner()).getName();
	}

	private int getSize(BlockFace face) {
		switch (face) {
		case EAST:
			return -2;
		case NORTH:
			return +2;
		case SOUTH:
			return -2;
		case WEST:
			return +2;
		default:
			throw new IllegalArgumentException(face + " is not a member of north,east,south,west");
		}
	}

	public Plot getPlotRelative(BlockFace face) {
		int x = plot.getxChunk();
		int z = plot.getzChunk();

		x += face.getModX();
		z += face.getModZ();

		PlotData plot = Plotsblitz.database().getPlotData(world, x, z);
		if (plot == null) {
			return null;
		}
		return new Plot(getWorldData(), plot);
	}

	/**
	 * @param p
	 *            the player
	 * @return true if the player can build at his location
	 */
	public boolean canBuild(Player p) {
		return canBuild(p, p.getLocation());
	}

	/**
	 * @param p
	 *            the player
	 * @param l
	 *            the location to check
	 * @return true if the player can build at the given location
	 */
	public boolean canBuild(Player p, Location l) {
		return canBuildPermission(p) && canBuildLocation(p, l);
	}

	/**
	 * @param l
	 *            the location to check
	 * @return true if one can build at that location assuming "one" can be floating
	 *         water or a growing tree
	 */
	public boolean canBuildLocation(Location l) {
		return canBuildLocation(null, l);
	}

	/**
	 * @param p
	 *            the player to check (can be null if there is no player and you
	 *            want to check an block update)
	 * @param l
	 *            the location to check
	 * @return true if the player can build at that location at the plot - a
	 *         subroutine of canBuild(...)
	 */
	public boolean canBuildLocation(@Nullable Player p, Location l) {
		int x = l.getBlockX();
		int z = l.getBlockZ();

		if (x >= getLowerX() && x <= getHigherX() && z >= getLowerZ() && z <= getHigherZ())
			return true;

		return false;
		/*
		 * int plotX = l.getBlockX() % world.getSizeBlocks(); int plotZ = l.getBlockZ()
		 * % world.getSizeBlocks();
		 * 
		 * if (l.getBlockX() < 0) plotX = -plotX; if (l.getBlockZ() < 0) plotZ = -plotZ;
		 * 
		 * // ignoring the first and last 2 blocks at each plot int min = 2; int max =
		 * world.getSizeBlocks() - min;
		 * 
		 * if (plotX < min || plotZ < min || plotX > max || plotZ > max) { return false;
		 * 
		 * } return true;
		 */
	}

	/**
	 * @param p
	 *            the player to check
	 * @return true if the player has permission to build. Is a subroutine of
	 *         canBuild(...)
	 */
	public boolean canBuildPermission(Player p) {
		if (AdminMode.isAdmin(p)) {
			return true;
		}
		if (plot.isSystem()) {
			if (!p.hasPermission("plotsblitz.system")) {
				// system and not op
				return false;
			} else {
				// system and op
				return true;
			}
		}

		if (plot.getOwner().equals(p.getUniqueId())) {
			// is a owner
			return true;
		}

		for (int n = 0; n < plot.getFriends().length(); ++n) {
			if (plot.getFriends().getString(n).equals(p.getUniqueId().toString())) {
				// is a friend
				return true;
			}
		}
		// none of the above
		return false;
	}

	/**
	 * @return the center of the plot. The y value is the highest block at the
	 *         center
	 */
	public Location getCenter() {
		return getWorld().getHighestBlockAt(getCenterX(), getCenterZ()).getLocation();
	}

	public Set<Chunk> getAllChunks() {
		Set<Chunk> chunks = new HashSet<>();
		for (int x = getLowerX(); x <= getHigherX(); x += 16) {
			for (int z = getLowerZ(); z <= getHigherZ(); z += 16) {
				Chunk chunk = getWorld().getChunkAt(new Location(getWorld(), x, 0, z));
				chunks.add(chunk);
			}
		}
		return chunks;
	}

	/**
	 * Teleports an player to the center of the plot, 2 blocks above the highest
	 * block
	 * 
	 * @param p
	 *            the player to teleport
	 */
	public void teleportTo(Player p) {
		p.teleport(getCenter().add(0, 2, 0));
	}

	/**
	 * @param p
	 *            the player to check
	 * @return true if the player is the owner OR is a virtual owner (admin!)
	 */
	public boolean isOwner(Player p) {
		return getPlot().getOwner().equals(p.getUniqueId()) || AdminMode.isAdmin(p);
	}

	public boolean isSystem() {
		return getPlot().isSystem();
	}

	/**
	 * deletes a plot in database and removing all items in it
	 */
	public void delete() {
		for (Chunk chunk : getAllChunks()) {
			getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
		}
		Plotsblitz.database().deletePlot(getPlot());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plot == null) ? 0 : plot.hashCode());
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plot other = (Plot) obj;
		return (other.getWorldData().getId() == this.getWorldData().getId()
				&& other.getPlot().getId() == this.getPlot().getId());
	}

}
