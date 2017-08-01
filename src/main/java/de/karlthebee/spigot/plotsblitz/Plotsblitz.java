package de.karlthebee.spigot.plotsblitz;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import de.karlthebee.spigot.plotsblitz.command.AdminCommand;
import de.karlthebee.spigot.plotsblitz.command.BuyCommand;
import de.karlthebee.spigot.plotsblitz.command.DeleteCommand;
import de.karlthebee.spigot.plotsblitz.command.FriendAddCommand;
import de.karlthebee.spigot.plotsblitz.command.FriendDeleteCommand;
import de.karlthebee.spigot.plotsblitz.command.HelpCommand;
import de.karlthebee.spigot.plotsblitz.command.HomeCommand;
import de.karlthebee.spigot.plotsblitz.command.InfoCommand;
import de.karlthebee.spigot.plotsblitz.command.PlotCommand;
import de.karlthebee.spigot.plotsblitz.command.WorldCommand;
import de.karlthebee.spigot.plotsblitz.command.WorldCreateCommand;
import de.karlthebee.spigot.plotsblitz.command.WorldDeleteCommand;
import de.karlthebee.spigot.plotsblitz.command.WorldGenerateCommand;
import de.karlthebee.spigot.plotsblitz.database.Database;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.AdminMode;
import de.karlthebee.spigot.plotsblitz.util.AutoUpdate;
import de.karlthebee.spigot.plotsblitz.util.PBUitl;
import de.karlthebee.spigot.plotsblitz.world.PlotEvents;
import de.karlthebee.spigot.plotsblitz.world.PlotWorldGenerator;
import de.karlthebee.spigot.plotsblitz.world.WorldEditConnector;
import net.milkbowl.vault.economy.Economy;

/**
 * Plotzblitz by karlthebee An spigot plugin for automatically creating plots
 * 
 * @author karlthebee
 *
 */
public class Plotsblitz extends JavaPlugin {

	private static Plotsblitz pb;

	private Database database;

	private Economy econ;

	@Override
	public void onDisable() {
		// clear all those variables just in case...
		this.database = null;
		this.econ = null;
		pb = null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		int plotsize = 2;
		int crossing = 8;
		if (id != null && id != "") {
			JSONObject o = new JSONObject(id);
			plotsize = o.getInt("plotsize");
			crossing = o.getInt("crossing");
		}
		return new PlotWorldGenerator(plotsize, crossing);
	}

	@Override
	public void onEnable() {
		pb = this;
		logger().info("Welcome to Plotsblitz. Initialising....");

		AutoUpdate.getInstance().checkForUpdates();

		try {
			initDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}

		logger().info("Listing all worlds for testing purposes...");
		for (WorldData data : database().getWorldData()) {
			logger().info("    -> " + data.toString());
		}

		logger().info("Registering Worldedit event connector");
		initWorldEdit();

		logger().info("Registering economy mode");
		initEncomony();

		logger().info("Registering events");
		getServer().getPluginManager().registerEvents(new PlotEvents(), this);
		getServer().getPluginManager().registerEvents(new AdminMode(), this);

		logger().info("Registering commands");
		getCommand("plot-buy").setExecutor(new BuyCommand());
		getCommand("plot-delete").setExecutor(new DeleteCommand());
		getCommand("plot-world").setExecutor(new WorldCommand());
		getCommand("plot-world-create").setExecutor(new WorldCreateCommand());
		getCommand("plot-world-delete").setExecutor(new WorldDeleteCommand());
		getCommand("plot-admin").setExecutor(new AdminCommand());
		getCommand("plot-friend-add").setExecutor(new FriendAddCommand());
		getCommand("plot-friend-delete").setExecutor(new FriendDeleteCommand());
		getCommand("plot-info").setExecutor(new InfoCommand());
		getCommand("plot-world-generate").setExecutor(new WorldGenerateCommand());
		getCommand("plot-home").setExecutor(new HomeCommand());
		getCommand("plot").setExecutor(new PlotCommand());
		getCommand("plot-help").setExecutor(new HelpCommand());

		logger().info(":)");
	}

	private void initWorldEdit() {
		if (!PBUitl.isWEInstalled()) {
			logger().warning(
					"   -> WorldEdit seems not to be installed. Plotsblitz will only show it's full potential when players can build via worldedit");
			return;
		}
		logger().info("Hooking into WorldEdit");
		WorldEditConnector.registerConnector();
	}

	private void initDatabase() throws SQLException {
		database = new Database();
		logger().info("Opening database connection...");

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);
		config.addDefault("database.url", "jdbc:mysql://127.0.0.1");
		config.addDefault("database.username", "minecraft");
		config.addDefault("database.password", "123456");
		config.addDefault("database.database", "minecraft");

		saveConfig();

		database.connect(config.getString("database.url"), config.getString("database.username"),
				config.getString("database.password"), config.getString("database.database"));
	}

	private void initEncomony() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().severe("Vault is not installed. Please install Vault!");
			throw new IllegalStateException("Could not find the vault plugin!");
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			throw new IllegalStateException("Could not get Economy service manager");
		}
		econ = rsp.getProvider();
		if (econ == null) {
			throw new IllegalStateException("Money provider is not available");
		}
	}

	/**
	 * @return the actual instance of Plotzblitz
	 */
	public static Plotsblitz getInstance() {
		return pb;
	}

	public static Logger logger() {
		return pb.getLogger();
	}

	public static Database database() {
		return pb.database;
	}

	public static Economy getEconomy() {
		return pb.econ;
	}

}
