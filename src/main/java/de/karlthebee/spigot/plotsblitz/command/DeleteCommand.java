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
import de.karlthebee.spigot.plotsblitz.world.Plot;

public class DeleteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return true;
		}
		Player p = (Player) sender;

		Plot plot = null;
		try {
			plot = BukkitDatabaseConnector.getPlot(p);
			if (!plot.isOwner(p))
				throw new NoPlotException();
		} catch (NoPlotWorldException | NoPlotException e) {
			p.sendMessage(Color.ERROR + "You do not own the plot");
			return true;
		}

		plot.delete();
		p.sendMessage(Color.INFO + "Your plot was deleted");

		return true;
	}

}
