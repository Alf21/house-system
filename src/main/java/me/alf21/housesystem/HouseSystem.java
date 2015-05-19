package me.alf21.housesystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alf21 on 28.04.2015 in project weapon_system.
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
    public HashMap<Integer, Location> houseLocations;

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
				HouseModel.initialize(houseSet.getString("player"), houseSet.getInt("house_model"));
				HouseModel.createModel(null, new Location(houseSet.getFloat("house_X"), houseSet.getFloat("house_Y"), houseSet.getFloat("house_Z")), houseSet.getInt("house_model"));
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR! - ");
			e.printStackTrace();
		}
    }
}
