package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.CommandFactory;

public class WorldDeleteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			CommandFactory.messageNoArgs("world");
			return false;
		}

		String name = args[0];
		
		WorldData data = Plotsblitz.database().getWorldData(name);
		if (data == null) {
			sender.sendMessage(CommandFactory.worldIsNull(name));
			return false;
		}
		sender.sendMessage(Color.INFO+"Deleting world... This may take a while");
		boolean deleted = Plotsblitz.database().deleteWorld(data);
		if (deleted) {
			sender.sendMessage(Color.INFO + "World \"" + name + "\" was deleted");
		} else {
			sender.sendMessage(Color.ERROR + "Could not delete world \"" + Color.INFO +name + Color.ERROR + "\"");
		}
		return true;
	}

}
