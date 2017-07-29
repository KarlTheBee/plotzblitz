package de.karlthebee.spigot.plotsblitz.world;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.ChunkGenerator;

import com.google.common.collect.Lists;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;

/**
 * the world generator
 * @author karlthebee
 *
 */
public class PlotWorldGenerator extends ChunkGenerator {

	public static final int HEIGHT = 65;

	public static final int CROSSING_HALF = 3;

	public static final Material FENCE = Material.DARK_OAK_FENCE;
	public static final Material FLOOR = Material.QUARTZ_BLOCK;

	private int plotsize = 1;
	private int crossing = 0;

	public PlotWorldGenerator(int plotsize, int crossing) {
		this.plotsize = plotsize;
		this.crossing = crossing;
		getLogger().info("generating new world with plotsize " + plotsize + " and crossing " + crossing);
	}

	public PlotType getChunkType(int x, int z) {
		if (crossing == 0) {
			return PlotType.PLOT;
		}

		int mod = plotsize * crossing + 1;

		x = x % mod;
		z = z % mod;

		if (x < 0)
			x += mod;
		if (z < 0)
			z += mod;

		if (x == mod - 1 && z == mod - 1)
			return PlotType.CROSSING;
		if (x == mod - 1)
			return PlotType.EASTLINE;
		if (z == mod - 1)
			return PlotType.NORTHLINE;

		return PlotType.PLOT;
	}

	/**
	 * @return a set containing of north, east, south, west if nessesary
	 */
	public Set<BlockFace> getDirections(int x, int z) {
		Set<BlockFace> faces = new HashSet<>();

		int mod;
		if (crossing != 0) {
			mod = plotsize * crossing + 1;
		} else {
			mod = plotsize;
		}

		x = x % mod;
		z = z % mod;

		if (x < 0)
			x += mod;
		if (z < 0)
			z += mod;
		if (crossing == 0)
			mod += 1;

		x %= plotsize;
		z %= plotsize;

		getLogger().info("x:=" + x + ",z:=" + z + ",mod:=" + mod + ";crossing:=" + crossing);
		if (x == 0)
			faces.add(BlockFace.WEST);
		if (z == 0)
			faces.add(BlockFace.NORTH);
		if (x == (plotsize - 1))
			faces.add(BlockFace.EAST);
		if (z == (plotsize - 1))
			faces.add(BlockFace.SOUTH);
		return faces;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
		ChunkData data = this.createChunkData(world);

		PlotType type = getChunkType(x, z);
		getLogger().info("render " + type.name() + " at " + x + "," + z);

		buildGround(data, random);

		switch (type) {
		case CROSSING:
			buildFlowers(data, random, 0.4);
			buildEastLine(data,true);
			buildNorthLine(data,true);
			break;
		case EASTLINE:
			buildFlowers(data, random, 0.2);
			buildEastLine(data,false);
			break;
		case NORTHLINE:
			buildFlowers(data, random, 0.2);
			buildNorthLine(data,false);
			break;
		case PLOT:
			buildFlowers(data, random, 0.05);
			getLogger().info("building faces " + getDirections(x, z).toString());
			getDirections(x, z).forEach(dir -> buildFences(data, dir, getDirections(x, z)));
			break;
		default:
			// NOTHING?!
			break;
		}

		return data;
	}

	@SuppressWarnings("deprecation")
	private void buildNorthLine(ChunkData data, boolean crossing) {
		data.setRegion(0, HEIGHT - 1, 8 - CROSSING_HALF, 16, HEIGHT, 8 + CROSSING_HALF, Material.QUARTZ_BLOCK);

		data.setRegion(0, HEIGHT, 1, 16, HEIGHT + 1, 2, Material.WOOD.getNewData((byte) 1));
		data.setRegion(0, HEIGHT + 1, 1, 16, HEIGHT + 2, 2, Material.LEAVES.getNewData((byte) 1));

		data.setRegion(0, HEIGHT, 14, 16, HEIGHT + 1, 15, Material.WOOD.getNewData((byte) 1));
		data.setRegion(0, HEIGHT + 1, 14, 16, HEIGHT + 2, 15, Material.LEAVES.getNewData((byte) 1));

		data.setRegion(0, HEIGHT, 8 - CROSSING_HALF, 16, HEIGHT + 2, 8 + CROSSING_HALF, Material.AIR);

		if (crossing) {
			data.setRegion(2, HEIGHT, 1, 14, HEIGHT + 2, 2, Material.AIR);
			data.setRegion(2, HEIGHT, 14, 14, HEIGHT + 2, 15, Material.AIR);
		}
	}

	@SuppressWarnings("deprecation")
	private void buildEastLine(ChunkData data, boolean crossing) {
		data.setRegion(8 - CROSSING_HALF, HEIGHT - 1, 0, 8 + CROSSING_HALF, HEIGHT, 16, Material.QUARTZ_BLOCK);

		data.setRegion(1, HEIGHT, 0, 2, HEIGHT + 1, 16, Material.WOOD.getNewData((byte) 1));
		data.setRegion(1, HEIGHT + 1, 0, 2, HEIGHT + 2, 16, Material.LEAVES.getNewData((byte) 1));

		data.setRegion(14, HEIGHT, 0, 15, HEIGHT + 1, 16, Material.WOOD.getNewData((byte) 1));
		data.setRegion(14, HEIGHT + 1, 0, 15, HEIGHT + 2, 16, Material.LEAVES.getNewData((byte) 1));

		data.setRegion(8 - CROSSING_HALF, HEIGHT, 0, 8 + CROSSING_HALF, HEIGHT + 2, 16, Material.AIR);

		if (crossing) {
			data.setRegion(1, HEIGHT, 2, 2, HEIGHT + 2, 14, Material.AIR);
			data.setRegion(14, HEIGHT, 2, 15, HEIGHT + 2, 14, Material.AIR);
		}

	}

	private void buildGround(ChunkData data, Random r) {
		List<Material> mats = Lists.newArrayList(Material.COAL_ORE, Material.DIAMOND_ORE, Material.GOLD_ORE,
				Material.EMERALD_ORE);
		data.setRegion(0, 0, 0, 16, 40, 16, Material.STONE);
		data.setRegion(0, 40, 0, 16, HEIGHT, 16, Material.GRASS);
		for (int n = 0; n < 256; ++n) {
			data.setBlock(r.nextInt(16), r.nextInt(40), r.nextInt(16), mats.get(r.nextInt(mats.size())));
		}
	}

	@SuppressWarnings("deprecation")
	private void buildFlowers(ChunkData data, Random r, double percentage) {
		for (int x = 1; x < 15; ++x) {
			for (int z = 1; z < 15; ++z) {
				double d = r.nextDouble();
				if (d > (1 - percentage))
					data.setBlock(x, HEIGHT, z, Material.RED_ROSE.getNewData((byte) r.nextInt(9)));
			}
		}
	}

	/**
	 * builds the fences and let mexico pay for it
	 */
	private void buildFences(ChunkData data, BlockFace face, Set<BlockFace> faces) {
		// set 1 for smaller border, 0 to do nothing
		int subtr = 1;

		switch (face) {
		case NORTH:
			data.setRegion(0 + ((faces.contains(BlockFace.WEST)) ? subtr : 0), HEIGHT - 1, 0 + subtr,
					16 - ((faces.contains(BlockFace.EAST)) ? subtr : 0), HEIGHT, 2, Material.QUARTZ_BLOCK);
			data.setRegion(0 + ((faces.contains(BlockFace.WEST)) ? subtr : 0), HEIGHT, 0 + subtr,
					16 - ((faces.contains(BlockFace.EAST)) ? subtr : 0), HEIGHT + 1, 2, Material.AIR);
			break;
		case EAST:
			data.setRegion(14, HEIGHT - 1, 0 + ((faces.contains(BlockFace.NORTH)) ? subtr : 0), 16 - subtr, HEIGHT,
					16 - ((faces.contains(BlockFace.SOUTH)) ? subtr : 0), Material.QUARTZ_BLOCK);
			data.setRegion(14, HEIGHT, 0 + ((faces.contains(BlockFace.NORTH)) ? subtr : 0), 16 - subtr, HEIGHT + 1,
					16 - ((faces.contains(BlockFace.SOUTH)) ? subtr : 0), Material.AIR);
			break;
		case SOUTH:
			data.setRegion(0 + ((faces.contains(BlockFace.WEST)) ? subtr : 0), HEIGHT - 1, 14,
					16 - ((faces.contains(BlockFace.EAST)) ? subtr : 0), HEIGHT, 16 - subtr, Material.QUARTZ_BLOCK);
			data.setRegion(0 + ((faces.contains(BlockFace.WEST)) ? subtr : 0), HEIGHT, 14,
					16 - ((faces.contains(BlockFace.EAST)) ? subtr : 0), HEIGHT + 1, 16 - subtr, Material.AIR);
			break;
		case WEST:
			data.setRegion(0 + subtr, HEIGHT - 1, 0 + ((faces.contains(BlockFace.NORTH)) ? subtr : 0), 2, HEIGHT,
					16 - ((faces.contains(BlockFace.SOUTH)) ? subtr : 0), Material.QUARTZ_BLOCK);
			data.setRegion(0 + subtr, HEIGHT, 0 + ((faces.contains(BlockFace.NORTH)) ? subtr : 0), 2, HEIGHT + 1,
					16 - ((faces.contains(BlockFace.SOUTH)) ? subtr : 0), Material.AIR);
			break;
		default:
			break;
		}
	}

	private Logger getLogger() {
		Plotsblitz pb = Plotsblitz.getInstance();
		if (pb != null) {
			Logger l = pb.getLogger();
			if (l != null)
				return l;
		}
		return Logger.getLogger("Generator");
	}

	static enum PlotType {
		PLOT, NORTHLINE, EASTLINE, CROSSING
	}

}
