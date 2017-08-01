package spigot.plotsblitz;

import org.junit.Ignore;
import org.junit.Test;

import de.karlthebee.spigot.plotsblitz.util.AutoUpdate;

public class AutoUpdateTest {
	
	/**
	 * tests update getting
	 * Ignores because Plotzblitz.logger() is null without bukkit
	 */
	@Test
	@Ignore
	public void simpleConnectionTest() {
		AutoUpdate.getInstance().checkForUpdates();
		System.out.println("actual version is " + AutoUpdate.getInstance().getUpdateVersion());
	}

}
