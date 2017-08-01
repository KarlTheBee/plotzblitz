package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.BukkitDatabaseConnector;
import de.karlthebee.spigot.plotsblitz.database.NoPlotException;
import de.karlthebee.spigot.plotsblitz.database.NoPlotWorldException;
import de.karlthebee.spigot.plotsblitz.database.PlotData;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.world.Plot;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class BuyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return false;
		}
		Player p = (Player) sender;

		try {
			Plot plot = BukkitDatabaseConnector.getPlot(p);
			p.sendMessage(Color.ERROR + "This plot is already bought by " + plot.getOwner());
			return true;
		} catch (NoPlotWorldException e) {
			p.sendMessage(Color.ERROR + "You can't buy a plot in this world");
			return true;
		} catch (NoPlotException e) {
		}

		WorldData wData = Plotsblitz.database().getWorldData(p.getWorld().getName());

		Economy econ = Plotsblitz.getEconomy();

		double cost = wData.getPlotCost();
		double balance = econ.getBalance(p);

		if (cost > balance) {
			p.sendMessage(Color.ERROR + "You don't have enough money");
			p.sendMessage(Color.ERROR + "You need " + Plotsblitz.getEconomy().format(cost) + " but got only "
					+ Plotsblitz.getEconomy().format(balance));
			return true;
		}

		if (econ.withdrawPlayer(p, cost).type != EconomyResponse.ResponseType.SUCCESS) {
			p.sendMessage(Color.ERROR + "An error in the payment process happened");
			p.sendMessage(Color.ERROR + "The error was logged");
			Plotsblitz.logger()
					.severe("Could not withdraw " + cost + ", player has " + balance + ". player is " + p.toString());
			return true;
		}

		int x = BukkitDatabaseConnector.getPlotFromLocation(wData, p.getLocation().getBlockX());
		int z = BukkitDatabaseConnector.getPlotFromLocation(wData, p.getLocation().getBlockZ());
		PlotData data = new PlotData(wData.getId(), false, p.getUniqueId(), new JSONArray(), x, z);
		boolean success = Plotsblitz.database().createPlot(data);
		if (!success) {
			Plotsblitz.logger().severe("Could not buy database");
			p.sendMessage(Color.ERROR + "An technical error appeared. Your money has been transfered back");
			Plotsblitz.getEconomy().depositPlayer(p, cost);
			return true;
		}
		p.sendMessage(Color.INFO + "Your plot is created");

		return true;
	}

}
