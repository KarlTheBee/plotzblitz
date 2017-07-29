package de.karlthebee.spigot.plotsblitz.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.util.PBUitl;

/**
 * TODO cache PlotData and WorldData to improve speed It's likely that if anyone
 * build on this server (and there are not that much user online) that 80% of
 * all requests refer to the same plot and world within a few minutes. THe cache
 * should be deleted if a plot or world is removed or added
 *
 */
public class Database {

	private static final String PLOT_TABLE = "CREATE TABLE IF NOT EXISTS pb_plots (" + "  id int(11) NOT NULL,"
			+ "  worldId int(11) NOT NULL," + "  owner tinytext COLLATE utf8mb4_unicode_ci NOT NULL,"
			+ "  system tinyint(1) NOT NULL DEFAULT '0'," + "  bought timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
			+ "  friends text COLLATE utf8mb4_unicode_ci NOT NULL," + "  xPos int(11) NOT NULL,"
			+ "  zPos int(11) NOT NULL" + ")";

	private static final String PLOT_WORLDS = "CREATE TABLE IF NOT EXISTS pb_worlds (" + "  id int(11) NOT NULL,"
			+ "  name varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL," + "  size int(11) NOT NULL,"
			+ "  plotcost double NOT NULL," + "  crossings int(11) NOT NULL,"
			+ "  settings varchar(1024) COLLATE uft8_mb4_unicode_ci NOT NULL DEFAULT '{}') ";

	private Logger logger = Plotsblitz.logger();

	private Connection con;

	public Database() {

	}

	public void connect(String url, String username, String password, String database) throws SQLException {
		logger.info("Connecting to database");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Could not find database driver, please try updating this plugin... :(", e);
			throw new IllegalStateException();
		}

		url = url + "/" + database + "?useSSL=true&useUnicode=true&characterEncoding=utf-8";

		con = DriverManager.getConnection(url, username, password);

		logger.info("Connection aquired");

		try {
			con.createStatement().execute(PLOT_WORLDS);
			con.createStatement().execute(PLOT_TABLE);
		} catch (SQLException e) {
			logger.severe("Could not execute table creations of pb_plots and pb_world");
			logger.log(Level.SEVERE, "Please check that you have write permission on that database", e);
		}
	}

	/*
	 * WORLD DATA
	 */

	/**
	 * @return a list of all worlds
	 */
	public List<WorldData> getWorldData() {
		return getWorlds("");
	}

	/**
	 * @param world
	 *            the name of the world to search
	 * @return the world with that name or null
	 */
	public WorldData getWorldData(String world) {
		return PBUitl.getListElementOrNull(getWorlds("WHERE name='" + world + "' LIMIT 1"));
	}

	/**
	 * @param id
	 *            the unique world id
	 * @return the world with that id or null
	 */
	public WorldData getWorldData(int id) {
		return PBUitl.getListElementOrNull(getWorlds("WHERE id=" + id + " LIMIT 1"));
	}

	private List<WorldData> getWorlds(String additionalQuery) {
		List<WorldData> list = new ArrayList<>();
		try {
			ResultSet set = con.createStatement()
					.executeQuery("SELECT id,name,size,plotcost,crossings,settings FROM pb_worlds " + additionalQuery);
			while (set.next()) {
				int id = set.getInt(1);
				String name = set.getString(2);
				int size = set.getInt(3);
				double plotcost = set.getDouble(4);
				int crossings = set.getInt(5);
				JSONObject settings = new JSONObject(set.getString(6));
				list.add(new WorldData(id, name, size, plotcost, crossings, settings));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return list;
		}
	}

	/**
	 * PLOTS
	 **/

	/**
	 * @param world
	 *            the world of that plot
	 * @param posX
	 *            x chunk
	 * @param posZ
	 *            z chunk
	 * @return the plot or null
	 */
	public PlotData getPlotData(WorldData world, int posX, int posZ) {
		return getPlotData(world.getId(), posX, posZ);
	}

	/**
	 * @param world
	 *            the world name. It's better to use getPlotData(WorldData,int,int)
	 *            if you already have the WorldData object
	 * @param posX
	 *            the x chunk
	 * @param posZ
	 *            the z chunk
	 * @return the plot or null
	 */
	public PlotData getPlotData(String world, int posX, int posZ) {
		return getPlotData(getWorldIdFromName(world), posX, posZ);
	}

	/**
	 * @param worldId
	 *            the world id
	 * @param posX
	 *            the x chunk
	 * @param posZ
	 *            the z chunk
	 * @return the plot or null
	 */
	public PlotData getPlotData(int worldId, int posX, int posZ) {
		return PBUitl.getListElementOrNull(
				getPlotInternal("WHERE worldId=" + worldId + " AND xPos=" + posX + " AND zPos=" + posZ));
	}
	/*
	 * 
	 */

	public List<PlotData> getPlotData(String world, UUID uniqueId, boolean friendPlots) {
		return getPlotData(getWorldIdFromName(world), uniqueId, friendPlots);
	}

	public List<PlotData> getPlotData(int world, UUID uniqueId, boolean friendPlots) {
		return getPlotInternal("WHERE worldId=" + world);
	}

	public int getWorldIdFromName(String name) {
		return getWorldData(name).getId();
	}

	private List<PlotData> getPlotInternal(String query) {
		List<PlotData> data = new ArrayList<>();
		try {
			ResultSet set = con.createStatement()
					.executeQuery("SELECT id,worldId,system,owner,bought,friends,xPos,zPos FROM pb_plots " + query);
			while (set.next()) {
				int id = set.getInt(1);
				int worldId = set.getInt(2);
				boolean system = set.getBoolean(3);
				UUID owner = UUID.fromString(set.getString(4));
				Date bought = set.getDate(5);
				JSONArray friends = new JSONArray(set.getString(6));
				int xChunk = set.getInt(7);
				int zChunk = set.getInt(8);

				data.add(new PlotData(id, worldId, system, owner, friends, bought, xChunk, zChunk));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	public boolean createWorld(WorldData data) {
		try {
			PreparedStatement statement = con
					.prepareStatement("INSERT INTO pb_worlds (name,size,plotcost,crossings) VALUES (?,?,?,?)");
			statement.setString(1, data.getWorld());
			statement.setInt(2, data.getSizeChunks());
			statement.setDouble(3, data.getPlotCost());
			statement.setInt(4, data.getCrossings());
			statement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createPlot(PlotData data) {
		try {
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO pb_plots (system,owner,friends,xPos,zPos,worldId) VALUES (?,?,?,?,?,?)");
			statement.setBoolean(1, data.isSystem());
			statement.setString(2, data.getOwner().toString());
			statement.setString(3, data.getFriends().toString());
			statement.setInt(4, data.getxChunk());
			statement.setInt(5, data.getzChunk());
			statement.setInt(6, data.getWorldId());

			statement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean updatePlot(PlotData data) {
		try {
			PreparedStatement prep = con.prepareStatement(
					"UPDATE pb_plots SET worldId=?, owner=?, system=?, bought=?, friends=?, xPos=?, zPos=? WHERE id=?");
			prep.setInt(1, data.getWorldId());
			prep.setString(2, data.getOwner().toString());
			prep.setBoolean(3, data.isSystem());
			prep.setDate(4, data.getBought());
			prep.setString(5, data.getFriends().toString());
			prep.setInt(6, data.getxChunk());
			prep.setInt(7, data.getzChunk());

			prep.setInt(8, data.getId());

			prep.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateWorld(WorldData world) {
		try {
			PreparedStatement prep = con.prepareStatement(
					"UPDATE pb_worlds SET name=?, size=?, plotcost=?, crossings=?, settings=? WHERE id=?");
			prep.setString(1, world.getWorld());
			prep.setInt(2, world.getSizeChunks());
			prep.setDouble(3, world.getPlotCost());
			prep.setInt(4, world.getCrossings());
			prep.setString(5, world.getSettings().toString());

			prep.setInt(6, world.getId());

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteWorld(WorldData data) {
		return deleteWorld(data.getId());
	}

	public boolean deleteWorld(int id) {
		if (id < 1)
			throw new IllegalStateException("worldId have to be >0 but is " + id);

		try {
			PreparedStatement prep = con.prepareStatement("DELETE FROM pb_worlds WHERE id=" + id);
			// remove all worlds
			PreparedStatement prep2 = con.prepareStatement("DELETE FROM pb_plots WHERE worldId=" + id);
			prep2.execute();
			return (prep.executeUpdate() != 0);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deletePlot(PlotData data) {
		return deletePlot(data.getId());
	}

	public boolean deletePlot(int id) {
		if (id < 1)
			throw new IllegalStateException("plotId have to be >0 but is " + id);

		try {
			PreparedStatement prep = con.prepareStatement("DELETE FROM pb_plots WHERE id=" + id);
			return (prep.executeUpdate() != 0);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
