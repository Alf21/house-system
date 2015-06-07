package me.alf21.housesystem;

import java.util.ArrayList;
import java.util.HashMap;

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
	private int model; //TODO doubled?
	private int level;
	private Location location;
	private Vector3D spawnLocation;
	private ArrayList<SampObject> objects;
	private ArrayList<SampObject> doors;
	private HashMap<Integer, Boolean> door;
	private float minX, maxX, minY, maxY, minZ, maxZ;
	private boolean initialized;

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

	ArrayList<SampObject> getObjects() {
		if (objects == null) objects = new ArrayList<SampObject>();
		return objects;
	}
	void setObjects(ArrayList<SampObject> objects) {
		if (this.objects == null) this.objects = new ArrayList<SampObject>();
		this.objects = objects;
	}
	
	ArrayList<SampObject> getDoors() {
		if (doors == null) doors = new ArrayList<SampObject>();
		return doors;
	}
	void addDoors(SampObject door){
		if (doors == null) doors = new ArrayList<SampObject>();
		doors.add(door);
	}
	
	void setMinX(float minX) {
		this.minX = minX;
	}
	void setMaxX(float maxX) {
		this.maxX = maxX;
	}
	void setMinY(float minY) {
		this.minY = minY;
	}
	void setMaxY(float maxY) {
		this.maxY = maxY;
	}
	void setMinZ(float minZ) {
		this.minZ = minZ;
	}
	void setMaxZ(float maxZ) {
		this.maxZ = maxZ;
	}
	
	float getMinX() {
		return minX;
	}
	float getMaxX() {
		return maxX;
	}
	float getMinY() {
		return minY;
	}
	float getMaxY() {
		return maxY;
	}
	float getMinZ() {
		return minZ;
	}
	float getMaxZ() {
		return maxZ;
	}
	
	boolean isDoorOpen(int val) {
		if (door == null) {
			door = new HashMap<Integer, Boolean>();
		}
		if (!door.containsKey(val)) door.put(val, false);
		return door.get(val);
	}
	void setDoorStatus(int val, boolean bool) {
		if (door == null) door = new HashMap<Integer, Boolean>();
		door.put(val, bool);
	}
	
	boolean isInitialized() {
		return initialized;
	}
	void setInitialized(boolean initialized) {
		this.initialized = initialized;
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
		initialized = false;
		maxX = minX = maxY = minY = maxZ = minZ = 0.0f;
	}


	@Override
	public boolean isDestroyed() {
		return true;
	}
}
