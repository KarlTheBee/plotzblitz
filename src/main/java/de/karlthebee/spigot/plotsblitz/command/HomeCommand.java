package de.karlthebee.spigot.plotsblitz.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.PlotData;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import de.karlthebee.spigot.plotsblitz.world.Plot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class HomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandFactory.noConsole());
			return true;
		}

		Player p = (Player) sender;
		UUID uid = p.getUniqueId();

		WorldData world = Plotsblitz.database().getWorldData(p.getWorld().getName());
		if (world == null) {
			p.sendMessage(Color.ERROR + "You are not in a plot world");
			return true;
		}
		if (args.length == 2) {
			try {
				int cX = Integer.parseInt(args[0]);
				int cZ = Integer.parseInt(args[1]);

				PlotData data = Plotsblitz.database().getPlotData(world, cX, cZ);
				new Plot(world, data).teleportTo(p);
			} catch (NumberFormatException e) {
				p.sendMessage(Color.ERROR + "Could not teleport you");
				return true;
			}
		}

		List<PlotData> plots = Plotsblitz.database().getPlotData(world.getId(), p.getUniqueId(), true);

		p.sendMessage(Color.INFO + "Own plots");
		plots.stream().filter(plot -> plot.getOwner().equals(uid)).forEach(plot -> sendPlot(p, plot));

		p.sendMessage(Color.INFO + "Friend plots");
		plots.stream().filter(plot -> !plot.getOwner().equals(uid)).forEach(plot -> sendPlot(p, plot));

		return true;
	}

	public void sendPlot(Player p, PlotData plot) {
		new SimpleTextBuilder().append("--> ").append("Plot at", Color.INFO).append(" [")
				.append("" + plot.getxChunk(), Color.INFO).append(",").append("" + plot.getzChunk(), Color.INFO)
				.append("] ")
				.append("[goto]", Color.INTERACTIVE,
						new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/plot-home " + plot.getxChunk() + " " + plot.getzChunk()),
						new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new SimpleTextBuilder().append("Teleport").buildArray()))
				.send(p);
	}

}
