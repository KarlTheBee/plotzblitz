package de.karlthebee.spigot.plotsblitz.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.karlthebee.spigot.plotsblitz.Plotsblitz;
import de.karlthebee.spigot.plotsblitz.database.WorldData;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class WorldCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		List<WorldData> datas = Plotsblitz.database().getWorldData();
		if (datas.size() == 0) {
			sender.sendMessage(Color.ERROR + "No world found");
		}
		sender.sendMessage(
				printWithSpace("World", 10) + "|" + printWithSpace("Size", 4) + "|" + printWithSpace("Cost", 5));
		for (WorldData data : datas) {
			prettyPrint(sender, data.getWorld(), data.getSizeChunks(), data.getPlotCost());
		}
		return true;
	}

	private void prettyPrint(CommandSender sender, String world, int size, double cost) {
		new SimpleTextBuilder()
				.append(printWithSpace(world, 10) + " ", Color.INTERACTIVE,
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mvtp " + world),
						new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new SimpleTextBuilder().append("Teleport").buildArray()))
				.append(printWithSpace(size, 4) + " ").append(printWithSpace(cost, 5) + " ")
				.append("[Delete]", Color.INTERACTIVE,
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot-world-delete " + world),
						new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new SimpleTextBuilder().append("Delete the world and all it's plots").buildArray()))
				.send(sender);
	}

	private String printWithSpace(Object o, int length) {
		String s = o.toString();
		while (s.length() < length) {
			s = s + " ";
		}
		if (s.length() > length) {
			s = s.substring(0, length);
		}
		return s;
	}

}
