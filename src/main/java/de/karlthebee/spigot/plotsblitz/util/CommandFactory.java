package de.karlthebee.spigot.plotsblitz.util;

public class CommandFactory {

	public static String messageNoArgs(String... args) {
		StringBuilder b = new StringBuilder();
		b.append(Color.ERROR);
		b.append("You have to enter the following arguments :");
		for (String arg : args) {
			b.append("<");
			b.append(arg);
			b.append(">");
		}
		return b.toString();
	}

	public static String cannotConvertArg(String arg) {
		StringBuilder b = new StringBuilder();
		b.append(Color.ERROR);
		b.append("Cannot convert <" + arg + "> to an number");
		return b.toString();
	}

	public static String worldIsNull(String world) {
		StringBuilder b = new StringBuilder();
		b.append(Color.ERROR);
		b.append("Cannot find a world with name \"");
		b.append(Color.INFO);
		b.append(world);
		b.append(Color.ERROR);
		b.append("\"");
		return b.toString();
	}

	public static String noConsole() {
		return Color.ERROR + "You must be an player";
	}

}
