package de.karlthebee.spigot.plotsblitz.util;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SimpleTextBuilder {

	private BaseComponent component;

	public SimpleTextBuilder append(String text) {
		return append(text, null, false, false);
	}

	public SimpleTextBuilder append(String text, ChatColor color) {
		return append(text, color, false, false);
	}

	public SimpleTextBuilder append(String text, ChatColor color, boolean bold, boolean italic) {
		return append(text, color, null, null, bold, italic);
	}

	public SimpleTextBuilder append(String text, ChatColor color, ClickEvent cEvent) {
		return append(text, color, cEvent, null);
	}

	public SimpleTextBuilder append(String text, ChatColor color, ClickEvent cEvent, HoverEvent hEvent) {
		return append(text, color, cEvent, hEvent, false, false);
	}

	public SimpleTextBuilder append(String text, HoverEvent hEvent) {
		return append(text, null, null, hEvent, false, false);
	}

	public SimpleTextBuilder append(String text, ChatColor color, ClickEvent cEvent, HoverEvent hEvent, boolean bold,
			boolean italic) {
		TextComponent c = new TextComponent(text);
		c.setBold(bold);
		c.setItalic(italic);
		if (color != null)
			c.setColor(color);
		if (cEvent != null)
			c.setClickEvent(cEvent);
		if (hEvent != null)
			c.setHoverEvent(hEvent);

		if (this.component == null) {
			this.component = c;
		} else {
			this.component.addExtra(c);
		}

		return this;
	}

	public BaseComponent build() {
		return this.component;
	}

	public BaseComponent[] buildArray() {
		BaseComponent[] cp = { this.component };
		return cp;
	}

	public void send(CommandSender... commandSenders) {
		for (CommandSender p : commandSenders) {
			send(p);
		}
	}

	public void send(CommandSender p) {
		p.spigot().sendMessage(this.component);
	}

}
