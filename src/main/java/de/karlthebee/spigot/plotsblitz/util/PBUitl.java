package de.karlthebee.spigot.plotsblitz.util;

import java.util.List;

import org.bukkit.Bukkit;

public class PBUitl {
	
	private PBUitl() {
		
	}

	/**
	 * @return true if worldedit is installed
	 */
	public static boolean isWEInstalled() {
		return Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
	}

	/**
	 * @param list
	 *            the list to set
	 * @return null if the list is null or empty, the first element if the list
	 *         has one
	 */
	public static <T> T getListElementOrNull(List<T> list) {
		return (list == null) ? null : (list.size() == 0) ? null : list.get(0);
	}

	/**
	 * @param e the enum to format
	 * @return the enum but formatted for an text an player sees
	 */
	public static String formatBukkitEnum(Object e) {
		return e.toString().toLowerCase().replaceAll("_", " ");
	}

}
