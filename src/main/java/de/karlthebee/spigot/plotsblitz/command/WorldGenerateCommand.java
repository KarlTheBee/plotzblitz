package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;

public class WorldGenerateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 3) {
			sender.sendMessage(CommandFactory.messageNoArgs("world", "plotsize", "crossing"));
			return true;
		}
		World w = Bukkit.getWorld(args[0]);
		if (w != null) {
			sender.sendMessage(Color.ERROR + "This world already exists");
			return true;
		}

		int plotsize;
		int crossing;
		try {
			plotsize = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(CommandFactory.cannotConvertArg("plotsize"));
			return true;
		}
		try {
			crossing = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(CommandFactory.cannotConvertArg("crossing"));
			return true;
		}

		Bukkit.getServer().createWorld(new WorldCreator(args[0])
				.generator(Plotsblitz.getInstance().getDefaultWorldGenerator(args[0], new JSONObject() {
					{
						put("plotsize", plotsize);
						put("crossing", crossing);
					}
				}.toString())));

		sender.sendMessage(Color.INFO + "World is generated");
		return true;
	}

}
