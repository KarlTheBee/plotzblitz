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

public class FriendDeleteCommand implements CommandExecutor {

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

		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getOfflinePlayer(name);
		if (friend == null) {
			p.sendMessage(Color.ERROR + "The player \"" + Color.INFO + name + Color.ERROR
					+ "\" was not found.");
			return true;
		}

		Plot plot=null;
		try {
			plot = BukkitDatabaseConnector.getPlot(p);
			if (!plot.isOwner(p)) {
				p.sendMessage(Color.ERROR + "You can only delete friends from your own plot");
				return true;
			}
		} catch (NoPlotWorldException | NoPlotException e) {
			p.sendMessage(Color.ERROR + "You can only delete friends from your own plot");
			return true;
		}
		
		JSONArray friends = plot.getPlot().getFriends();
		
		int found=-1;
		for(int n=0;n<friends.length();++n) {
			if (friends.getString(n).equals(friend.getUniqueId().toString())){
				found=n;
			}
		}
		if (found==-1) {
			p.sendMessage(Color.ERROR + "Could not find friend");
			return true;
		}
		friends.remove(found);
		
		boolean success = Plotsblitz.database().updatePlot(plot.getPlot());
		if (!success) {
			p.sendMessage(Color.ERROR  + "Could not update plot");
		}else {
			p.sendMessage(Color.INFO + "Removed " + friend.getName() + " from your plot");
		}

		return true;
	}

}
