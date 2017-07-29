package de.karlthebee.spigot.plotsblitz.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import net.md_5.bungee.api.chat.ClickEvent;

public class HelpCommand implements CommandExecutor {

	private static final Map<String, String> commands = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4639141303243644457L;

		{
			put("plot-help", "Opens the help page");
			put("plot-buy", "Lets you buy the plot you're standing on");
			put("plot-delete", "Lets you delete the plot you're standing on");
			put("plot-friend-add <player>", "Adds an friend to your plot who also can buy");
			put("plot-friend-delete <player>", "Deletes an friend from your friend list");
			put("plot-info", "Display information about this plot");
			put("plot-home", "Lists all plots you're owning");
			put("plot-world-generate <world> <plotsize> <crossing>", "Generates a new world with an world generator");
			put("plot-world-create <world> <plotize> <cost> <crossing>",
					"Creates a plotzblitz world from an existing world");
			put("plot-world", "Lists all worlds");
			put("plot-world-delete <world>", "Deletes an world an all it's plots");
			put("plot-admin", "Switches to admin mode and allows you editing every plot");
		}
	};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(Color.INFO + "--- Help ---");
		for (String cmd : commands.keySet()) {
			new SimpleTextBuilder()
					.append(cmd, Color.INTERACTIVE, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+cmd))
					.send(sender);
			new SimpleTextBuilder().append("  -> ").append(commands.get(cmd), Color.INFO).send(sender);
		}

		return true;
	}

}
