package de.karlthebee.spigot.plotsblitz.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AdminMode implements Listener {

	private static final Set<UUID> uuids = new HashSet<>();

	public static boolean isAdmin(Player p) {
		if (p == null)
			return false;
		return isAdmin(p.getUniqueId());
	}

	public static boolean isAdmin(UUID uid) {
		return uuids.contains(uid);
	}

	public static void addAdmin(Player p) {
		uuids.add(p.getUniqueId());
	}

	public static void removeAdmin(Player p) {
		uuids.remove(p.getUniqueId());
	}

	@EventHandler
	protected void onLeave(PlayerQuitEvent event) {
		removeAdmin(event.getPlayer());
	}

}
