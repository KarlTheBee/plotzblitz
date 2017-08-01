package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.world.Plot;

public class FriendAddCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return true;
		}
		if (args.length != 1) {
			sender.sendMessage(CommandFactory.messageNoArgs("friend"));
			return false;
		}
		
		String name = args[0];
		Player p = (Player) sender;

		//Player friend = Bukkit.getPlayer(name);
		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getOfflinePlayer(name);
		if (friend == null) {
			p.sendMessage(Color.ERROR + "The player \"" + Color.INFO + name + Color.ERROR
					+ "\" was not found. Is he online?");
			return true;
		}

		Plot plot=null;
		try {
			plot = BukkitDatabaseConnector.getPlot(p);
			if (!plot.isOwner(p)) {
				p.sendMessage(Color.ERROR + "You can only add friends to your own plot");
				return true;
			}
		} catch (NoPlotWorldException | NoPlotException e) {
			p.sendMessage(Color.ERROR + "You can only add friends to your own plot");
			return true;
		}
		
		JSONArray friends = plot.getPlot().getFriends();
		friends.put(friend.getUniqueId());
		
		boolean success = Plotsblitz.database().updatePlot(plot.getPlot());
		if (!success) {
			p.sendMessage(Color.ERROR  + "Could not update plot");
		}else {
			p.sendMessage(Color.INFO + "Added " + friend.getName() + " to your plot");
		}

		return true;
	}

}
