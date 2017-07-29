package de.karlthebee.spigot.plotsblitz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.karlthebee.spigot.plotsblitz.util.AdminMode;
import de.karlthebee.spigot.plotsblitz.util.Color;
import de.karlthebee.spigot.plotsblitz.util.SimpleTextBuilder;
import net.md_5.bungee.api.chat.ClickEvent;

public class AdminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.ERROR + "You are not a player");
			return false;
		}
		Player p = (Player) sender;

		if (AdminMode.isAdmin(p)) {
			AdminMode.removeAdmin(p);
			new SimpleTextBuilder().append("Admin mode deactivated  ").append("[Activate]", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot-admin"), null).send(p);
		} else {
			AdminMode.addAdmin(p);
			new SimpleTextBuilder().append("Admin mode activated  ").append("[Deactivate]", Color.INTERACTIVE,
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot-admin"), null).send(p);
		}

		return true;
	}

}
