package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class WorldCreateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(CommandFactory.messageNoArgs("world", "size", "plotcost", "<crossings>"));
			return false;
		}

		World world;
		int size;
		double plotCost;
		int crossings = 8;

		world = Bukkit.getWorld(args[0]);
		if (world == null) {
			sender.sendMessage(CommandFactory.worldIsNull(args[0]));
			return false;
		}

		try {
			size = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(CommandFactory.cannotConvertArg("size"));
			return false;
		}
		try {
			plotCost = Double.valueOf(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(CommandFactory.cannotConvertArg("plotCost"));
			return false;
		}

		if (Plotsblitz.database().getWorldData(world.getName()) != null) {
			sender.sendMessage(Color.ERROR + "This world already exists in database");
			return true;
		}
		if (size <= 0) {
			sender.sendMessage(Color.ERROR + "Plot size can't be 0 or lower");
			return true;
		}
		if (plotCost < 0) {
			sender.sendMessage(Color.ERROR + "Plot cost can't be below 0");
			return true;
		}

		if (args.length == 4) {
			try {
				crossings = Integer.valueOf(args[3]);
				if (crossings < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(Color.ERROR + "Crossing mus be a integer with 0 (=disabled) or higher");
				return true;
			}
		}

		WorldData w = new WorldData(-1, world.getName(), size, plotCost, crossings);
		boolean created = Plotsblitz.database().createWorld(w);

		if (!created) {
			sender.sendMessage(Color.ERROR + "Could not create world");
		} else {
			// TODO detect if MVTP is installed
			sender.spigot()
					.sendMessage(
							new SimpleTextBuilder().append("World was created! ", Color.INFO)
									.append("[Goto]", Color.INTERACTIVE,
											new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mvtp " + world.getName()),
											new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new SimpleTextBuilder().append("teleport via mvtp").buildArray()))
									.build());
		}
		return true;
	}

}
