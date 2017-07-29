package de.karlthebee.spigot.plotsblitz.database;

import java.sql.Date;
import java.util.UUID;

import org.json.JSONArray;

public class PlotData {
	private int id;
	private int worldId;
	private boolean system;
	private UUID owner;
	private JSONArray friends;
	private Date bought;
	private int xPos;
	private int zPos;

	public PlotData(int worldId, boolean system, UUID owner, JSONArray friends, int xChunk, int zChunk) {
		super();
		this.worldId = worldId;
		this.system = system;
		this.owner = owner;
		this.friends = friends;
		this.xPos = xChunk;
		this.zPos = zChunk;
	}

	public PlotData(int id, int worldId,boolean system, UUID owner, JSONArray friends, Date bought, int xPos, int zPos) {
		super();
		this.id = id;
		this.worldId = worldId;
		this.system = system;
		this.owner = owner;
		this.friends = friends;
		this.bought = bought;
		this.xPos = xPos;
		this.zPos = zPos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public JSONArray getFriends() {
		return friends;
	}

	public void setFriends(JSONArray friends) {
		this.friends = friends;
	}

	public Date getBought() {
		return bought;
	}

	public void setBought(Date bought) {
		this.bought = bought;
	}

	public int getxChunk() {
		return xPos;
	}

	public void setxChunk(int xChunk) {
		this.xPos = xChunk;
	}

	public int getzChunk() {
		return zPos;
	}

	public void setzChunk(int zChunk) {
		this.zPos = zChunk;
	}

	@Override
	public String toString() {
		return "PlotData [id=" + id + ", system=" + system + ", owner=" + owner + ", friends=" + friends + ", bought="
				+ bought + ", xPos=" + xPos + ", zPos=" + zPos + "]";
	}

}