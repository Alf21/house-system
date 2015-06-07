package me.alf21.housesystem;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alf21 on 20.05.2015 in project weapon_system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 * http://forum.sa-mp.de/index.php?page=VCard&userID=34293
 * 							or
 * search for Alf21 in http://sa-mp.de || Breadfish
 * 
 * My website:
 * 				http://gsk.bplaced.net
 **/

public class HouseSystem extends Plugin {

	public static final Logger LOGGER = LoggerFactory.getLogger(HouseSystem.class);
	private static HouseSystem instance;
	private PlayerManager playerManager;
	private EventManager eventManager;
    private PlayerLifecycleHolder playerLifecycleHolder;
    private EventManagerNode eventManagerNode;
    private MysqlConnection mysqlConnection;
    public HashMap<Integer, Location> houseLocations = new HashMap<Integer, Location>();
    public HashMap<Integer, Vector3D> spawnLocations = new HashMap<Integer, Vector3D>();
    public final static String folderName = "objects";

	public static HouseSystem getInstance() {
		if (instance == null)
			instance = new HouseSystem();
		return instance;
	}
	
	@Override
	protected void onDisable() throws Throwable {
		playerLifecycleHolder.destroy();
        eventManagerNode.destroy();
		playerManager.uninitialize();
		playerManager.destroy();
		playerManager = null;
        mysqlConnection.closeConnection();
	}

	@Override
	protected void onEnable() throws Throwable {
		instance = this;
		eventManager = getEventManager();
        eventManagerNode = eventManager.createChildNode();
        playerLifecycleHolder = new PlayerLifecycleHolder(eventManager);
        playerLifecycleHolder.registerClass(PlayerData.class);
		playerManager = new PlayerManager();
        mysqlConnection = new MysqlConnection();
        mysqlConnection.initConnection();
        mysqlConnection.makeDatabase();
        
        File dir = new File(this.getDataDir(), folderName);
        if(!dir.exists()) dir.mkdir();
        
        createHouses();
	}

	Logger getLoggerInstance() {
        return LOGGER;
    }

    EventManager getEventManagerInstance() {
        return eventManagerNode;
    }
    
    PlayerLifecycleHolder getPlayerLifecycleHolder() {
        return playerLifecycleHolder;
    }
    
    MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
//------------------------------------
    private void createHouses() {
    	ResultSet houseSet = mysqlConnection.executeQuery("SELECT * FROM samp_housesystem");
        try {
			while(houseSet.next()) {
				if(!HouseModel.initialize(houseSet.getString("player"), houseSet.getInt("house_model"))) System.out.println("Invalid modelId ('" + houseSet.getInt("house_model") + "') in column of " + houseSet.getString("player"));
				HouseModel.moveModel(houseSet.getString("player"), new Location(houseSet.getFloat("house_X"), houseSet.getFloat("house_Y"), houseSet.getFloat("house_Z")), houseSet.getInt("house_model"));
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR! - ");
			e.printStackTrace();
		}
    }
    
//TODO: extern functions
}
