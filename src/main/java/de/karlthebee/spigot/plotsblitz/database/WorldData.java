package de.karlthebee.spigot.plotsblitz.database;

import org.json.JSONObject;

public class WorldData {
	private int id;
	private String world;
	private int size;
	private double plotCost;
	private int crossings;
	private JSONObject settings;
	
	public WorldData(int id, String world, int size, double plotCost, int crossings) {
		super();
		this.id = id;
		this.world = world;
		this.size = size;
		this.plotCost = plotCost;
		this.crossings = crossings;
	}

	public WorldData(int id, String world, int size, double plotCost, int crossings, JSONObject settings) {
		super();
		this.id = id;
		this.world = world;
		this.size = size;
		this.plotCost = plotCost;
		this.crossings = crossings;
		this.settings = settings;
	}
	

	public int getCrossings() {
		return crossings;
	}


	public void setCrossings(int crossings) {
		this.crossings = crossings;
	}


	public JSONObject getSettings() {
		return settings;
	}


	public void setSettings(JSONObject settings) {
		this.settings = settings;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSizeChunks() {
		return size;
	}

	public int getSizeBlocks() {
		return size * 16;
	}

	public void setSizeChunks(int size) {
		this.size = size;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public double getPlotCost() {
		return plotCost;
	}

	public void setPlotCost(double plotCost) {
		this.plotCost = plotCost;
	}

	@Override
	public String toString() {
		return "WorldData [id=" + id + ", world=" + world + ", size=" + size + ", plotCost=" + plotCost + "]";
	}

}