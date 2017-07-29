package de.karlthebee.spigot.plotsblitz.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import de.karlthebee.spigot.plotsblitz.world.Plot;
import net.md_5.bungee.api.chat.ClickEvent;

public class InfoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return true;
		}
		Player p = (Player) sender;

		try {
			Plot plot = BukkitDatabaseConnector.getPlot(p);
			p.sendMessage("--- Plot info ---");
			if (plot.isSystem()) {
				p.sendMessage("Owner : " + Color.SYSTEM + "System");
			} else {
				OfflinePlayer player = Bukkit.getOfflinePlayer(plot.getPlot().getOwner());
				p.sendMessage("Owner : " + player.getName());

			}
			JSONArray friends = plot.getPlot().getFriends();
			if (friends.length() != 0) {
				p.sendMessage("Friends : ");
				for (int n = 0; n < friends.length(); ++n) {
					String name = Bukkit.getOfflinePlayer(UUID.fromString(friends.getString(n))).getName();
					SimpleTextBuilder s = new SimpleTextBuilder().append("--> ").append(name);
					if (plot.isOwner(p))
						s.append(" ").append("[Delete]", Color.INTERACTIVE,
								new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-friend-delete"), null);
					p.spigot().sendMessage(s.build());
				}
			}
		} catch (NoPlotWorldException e) {
			p.sendMessage("This world has no plots");
		} catch (NoPlotException e) {
			p.sendMessage("There's no plot on where you are standing");
		}
		return true;
	}

}
