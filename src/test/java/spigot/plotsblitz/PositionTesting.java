package spigot.plotsblitz;

import org.junit.Assert;
import org.junit.Test;

import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.PlotData;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.world.Plot;

public class PositionTesting {

	@Test
	public void test() {
		WorldData data = new WorldData(-1, null, 2, 0, 2);
		Assert.assertEquals("X is wrong", 0, BukkitDatabaseConnector.getPlotFromLocation(data, 16));
	}

	@Test
	public void test2() {
		WorldData data = new WorldData(-1, null, 2, 0, 2);
		for (int n = 0; n <= 96; n += 8) {
			System.out.println(n + " :" + BukkitDatabaseConnector.getPlotFromLocation(data, n));
		}

	}

	@Test
	public void testPlot() {
		WorldData wd = new WorldData(-1, null, 2, 0, 2);
		PlotData pd = new PlotData(-1, false, null, null, 1, 1);
		Plot plot = new Plot(wd, pd);
		Assert.assertEquals("Min x is wrong", 32 + 2, plot.getLowerX());
		Assert.assertEquals("Max x is wrong", 63 - 2, plot.getHigherX());
		Assert.assertEquals("Min z is wrong", 32 + 2, plot.getLowerZ());
		Assert.assertEquals("Max z is wrong", 63 - 2, plot.getHigherZ());
	}

	@Test
	public void testPlot2() {
		WorldData wd = new WorldData(-1, null, 2, 0, 2);
		PlotData pd = new PlotData(-1, false, null, null, 5, 2);
		Plot plot = new Plot(wd, pd);
		Assert.assertEquals("Min x is wrong", 12 * 16 + 2, plot.getLowerX());
		Assert.assertEquals("Max x is wrong", 14 * 16 - 2 - 1, plot.getHigherX());
		Assert.assertEquals("Min z is wrong", 5 * 16 + 2, plot.getLowerZ());
		Assert.assertEquals("Max z is wrong", 7 * 16 - 2 - 1, plot.getHigherZ());
	}

	/*
	 * @Test public void testAnothers() { int[] values = { 1, 2, 3, 4, 5, 6, 7, 8,
	 * 9, 10, 11, 12, 13, 14 }; int[] results = { 0, 0, 1, 1, 2, 2, 2, 3, 3, 4, 4,
	 * 4, 5, 5 };
	 * 
	 * WorldData data = new WorldData(-1, null, 2, 0, 2);
	 * 
	 * for (int n = 0; n < values.length; ++n) { int v =
	 * BukkitDatabaseConnector.getPlotFromLocation(data, values[n] * 16 - 1);
	 * 
	 * Assert.assertEquals("X is wrong on " + values[n], results[n], v);
	 * 
	 * int min = BukkitDatabaseConnector.getMinLocationFromPlot(data, v); int max =
	 * BukkitDatabaseConnector.getMaxLocationFromPlot(data, v); int backMin =
	 * BukkitDatabaseConnector.getPlotFromLocation(data, min); int backMax =
	 * BukkitDatabaseConnector.getPlotFromLocation(data, max);
	 * 
	 * System.out.println("location " + (values[n] * 16 - 1));
	 * System.out.println("    plot " + v); System.out.println("     min " + min +
	 * "  , max " + max); System.out.println("    back " + backMin + "  , max " +
	 * backMax); System.out.println();
	 * 
	 * Assert.assertEquals("Could not convert " + values[n], v, backMin); } }
	 */

}
