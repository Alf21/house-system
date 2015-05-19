package me.alf21.housesystem;

import java.util.ArrayList;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.SampObject;

/**
 * Created by Alf21 on 28.04.2015 in project weapon_system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class HouseData implements Destroyable{
	private String playerName;
	private Integer houseId;
	private boolean open;
	private int model;
	private int level;
	private Location location;
	private Vector3D spawnLocation;
	private static ArrayList<SampObject> objects;

	public HouseData(String playerName, Integer houseId) {
		this.playerName = playerName;
		this.houseId = houseId;
	}


	public Integer getHouseId() {
		return houseId;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	boolean isOpen() {
		return open;
	}
	void setOpen(boolean open) {
		this.open = open;
	}
	
	int getModel() {
		return model;
	}
	void setModel(int model) {
		this.model = model;
	}
	
	int getLevel() {
		return level;
	}
	void setLevel(int level) {
		this.level = level;
	}
	
	Location getLocation() {
		return location;
	}
	void setLocation(Location location) {
		this.location = location;
	}
	
	Vector3D getSpawnLocation() {
		return spawnLocation;
	}
	void setSpawnLocation(Vector3D vector3d) {
		this.spawnLocation = vector3d;
	}

	public ArrayList<SampObject> getObjects() {
		return objects;
	}
	public void setObjects(ArrayList<SampObject> objects) {
		HouseData.objects = objects;
	}

	@Override
	public void destroy() {
		playerName = "";
		houseId = 0;
		open = false;
		model = 0;
		level = 0;
		location = null;
		spawnLocation = null;
		objects.clear();
	}


	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		return true;
	}
}
