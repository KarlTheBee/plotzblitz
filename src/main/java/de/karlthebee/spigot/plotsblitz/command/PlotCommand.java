package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import de.karlthebee.spigot.plotsblitz.world.Plot;
import net.md_5.bungee.api.chat.ClickEvent;

public class PlotCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return true;
		}

		Player p = (Player) sender;

		p.sendMessage(Color.INFO + "--- Suggestions ---");

		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(p);

		} catch (NoPlotWorldException e) {
			if (p.hasPermission("plotzblitz.world")) {
				new SimpleTextBuilder().append(Color.ITEM).append("Create plotzblitz world", Color.INTERACTIVE,
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-world-create")).send(p);
			}else {
			p.sendMessage(Color.ERROR + "This is not an plotzblitz world");
			}
			return true;
		} catch (NoPlotException e) {
			new SimpleTextBuilder().append(Color.ITEM).append("Buy this plot", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-buy")).send(p);
			;
			return true;
		}

		new SimpleTextBuilder().append(Color.ITEM).append("Get information about this plot", Color.INTERACTIVE,
				new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-info")).send(p);
		

		if (plot.isOwner(p)) {
			new SimpleTextBuilder().append(Color.ITEM).append("Build together with an friend", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-friend-add")).send(p);

			if (plot.getPlot().getFriends().length() != 0) {
				new SimpleTextBuilder().append(Color.ITEM).append("Remove an friend", Color.INTERACTIVE,
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-friend-remove")).send(p);
			}

			new SimpleTextBuilder().append(Color.ITEM).append("Delete your plot", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-delete")).send(p);
		}
		new SimpleTextBuilder().append(Color.ITEM).append("List your plots", Color.INTERACTIVE,
				new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-home"));

		if (p.hasPermission("plotzblitz.adminmode")) {
			new SimpleTextBuilder().append(Color.ITEM).append("Switch admin mode", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-admin"));
		}
		
		return true;
	}

}
