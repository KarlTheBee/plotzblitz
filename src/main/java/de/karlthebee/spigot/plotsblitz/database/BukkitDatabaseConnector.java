package de.karlthebee.spigot.plotsblitz.database;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.world.Plot;

/**
 * A util class for exchanging bukkit values (location, player,block,...) with
 * database objects (plotdata,worlddata)
 */
public class BukkitDatabaseConnector {

	public static Plot getPlot(Player p) throws NoPlotWorldException, NoPlotException {
		return getPlot(p.getLocation());
	}

	public static Plot getPlot(Block block) throws NoPlotWorldException, NoPlotException {
		return getPlot(block.getLocation());
	}

	public static Plot getPlot(Location location) throws NoPlotWorldException, NoPlotException {
		World w = location.getWorld();

		WorldData wData = Plotsblitz.database().getWorldData(w.getName());
		if (wData == null)
			throw new NoPlotWorldException();

		int x = getPlotFromLocation(wData, location.getBlockX());
		int z = getPlotFromLocation(wData, location.getBlockZ());

		PlotData pData = Plotsblitz.database().getPlotData(wData, x, z);
		if (pData == null) {
			throw new NoPlotException();
		}
		Plot plot = new Plot(wData, pData);
		return plot;
	}

	/**
	 * @param loc
	 *            the player location
	 * @return database plot location
	 */
	public static int getPlotFromLocation(WorldData data, int loc) {
		int ps = data.getSizeChunks();
		int c = data.getCrossings();

		double chk = loc / 16d;

		// calculate how much space to "remove"
		int space = (int) Math.floor(chk / (ps * c + 1));

		// remove space, divide
		int plot = (int) Math.floor((chk - space) / ps);

		return plot;
	}

	/**
	 * @param plot
	 * @return
	 */
	public static int getChunkFromPlot(WorldData data, int plot) {
		int ps = data.getSizeChunks();
		int c = data.getCrossings();

		double loc = 1d * plot * (ps * c + 1) / ps;

		return (int) Math.floor(loc);
	}

	/**
	 * @param plot
	 *            database plot location
	 * @return location at beginning of chunk
	 */
	public static int getMinLocationFromPlot(WorldData data, int plotChnk) {
		return getChunkFromPlot(data, plotChnk) * 16;
	}

	public static int getMaxLocationFromPlot(WorldData data, int plotChnk) {
		return getMinLocationFromPlot(data, plotChnk) + data.getSizeBlocks() - 1;
	}

}
