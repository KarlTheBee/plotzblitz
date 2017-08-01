package de.karlthebee.spigot.plotsblitz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;

public class AutoUpdate {

	// get the newest plugin.yml file from githubs master branch
	private static final String CONTENT = "https://raw.githubusercontent.com/karlthebee/plotzblitz/master/src/main/resources/plugin.yml";

	private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	private static final AutoUpdate autoUpdate = new AutoUpdate();

	static {
		service.scheduleAtFixedRate(() -> getInstance().checkForUpdates(), 6, 6, TimeUnit.HOURS);
	}

	public static AutoUpdate getInstance() {
		return autoUpdate;
	}

	private String updateVersion = null;

	private AutoUpdate() {

	}

	public synchronized void checkForUpdates() {
		Plotsblitz.logger().info("checking for updates...");
		YamlConfiguration config = null;
		try {
			URL url = new URL(CONTENT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			config = YamlConfiguration.loadConfiguration(reader);
		} catch (IOException e) {
			Plotsblitz.logger().log(Level.SEVERE,
					"Could not get read update config file from " + CONTENT + "\nIs there no internet connection?", e);
			updateVersion = "ERROR_NO_CONNECTION";
			return;
		}

		if (!config.contains("version")) {
			Plotsblitz.logger().severe("could not read update config file from " + CONTENT);
			updateVersion = "ERROR_WRONG_CONTENT";
			return;
		}
		updateVersion = config.getString("version");

		if (isUpdateAvailable()) {
			Plotsblitz.logger().info("An update is available");
			Plotsblitz.logger().info(Color.ITEM + "Actual version : " + getActualVersion());
			Plotsblitz.logger().info(Color.ITEM + "   New version : " + getUpdateVersion());
			Plotsblitz.logger().info("You can get it on https://github.com/karlthebee/plotzblitz");
		} else {
			Plotsblitz.logger().info("The latest version (" + getActualVersion() + ") of Plotzblitz is installed");
		}
	}

	/**
	 * @return true if a new version (actual version != update version) is available
	 */
	public boolean isUpdateAvailable() {
		return (!getActualVersion().equals(getUpdateVersion()));
	}

	/**
	 * @return
	 */
	public String getActualVersion() {
		return Plotsblitz.getInstance().getDescription().getVersion();
	}

	public String getUpdateVersion() {
		return updateVersion;
	}

}
