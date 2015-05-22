package me.alf21.housesystem;

import java.util.ArrayList;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.SampObject;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
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
	private ArrayList<SampObject> objects;
	private ArrayList<SampObject> doors;

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
		if (spawnLocation == null) {
			spawnLocation = new Vector3D();
			if (HouseSystem.getInstance().spawnLocations.get(houseId) == null) HouseModel.initialize(playerName, houseId);
			else spawnLocation = HouseSystem.getInstance().spawnLocations.get(houseId);
		}
		return spawnLocation;
	}
	void setSpawnLocation(Vector3D vector3d) {
		if (spawnLocation == null) spawnLocation = new Vector3D();
		this.spawnLocation = vector3d;
	}

	public ArrayList<SampObject> getObjects() {
		if (objects == null) objects = new ArrayList<SampObject>();
		return objects;
	}
	public void setObjects(ArrayList<SampObject> objects) {
		if (this.objects == null) this.objects = new ArrayList<SampObject>();
		this.objects = objects;
	}
	
	public ArrayList<SampObject> getDoors() {
		if (doors == null) doors = new ArrayList<SampObject>();
		return doors;
	}
	public void addDoors(SampObject door){
		if (doors == null) doors = new ArrayList<SampObject>();
		doors.add(door);
	}

	@Override
	public void destroy() {
		playerName = null;
		houseId = 0;
		open = false;
		model = 0;
		level = 0;
		location = null;
		spawnLocation = null;
		objects.clear();
		objects = new ArrayList<SampObject>();
		doors.clear();
		doors = new ArrayList<SampObject>();
	}


	@Override
	public boolean isDestroyed() {
		return true;
	}
}
