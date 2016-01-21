package me.alf21.housesystem;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.ObjectEditResponse;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.object.*;
import net.gtaun.util.event.HandlerPriority;

import java.util.HashMap;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class PlayerManager implements Destroyable {
	public PlayerData playerLifecycle;
	HashMap<String, HouseData> houseDataMap;
	public PlayerCommandManager commandManager;

	public PlayerManager()
	{
		this.houseDataMap = new HashMap<String, HouseData>();
		
		commandManager = new PlayerCommandManager(HouseSystem.getInstance().getEventManagerInstance());
	    commandManager.registerCommands(new Commands());
	    
		commandManager.installCommandHandler(HandlerPriority.NORMAL);

//PlayerConnectEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerConnectEvent.class, (e) -> {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(e.getPlayer(), PlayerData.class);
			initHouse(e.getPlayer().getName());
		});
		
//PlayerWeaponShotEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerWeaponShotEvent.class, (e) -> {
			
		});
		
//PlayerGiveDamageEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerGiveDamageEvent.class, (e) -> {
			
		});
		
//PlayerUpdateEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerUpdateEvent.class, (e) -> {
			if (e.getPlayer().getUpdateCount() % 5 == 0)
			{
				playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(e.getPlayer(), PlayerData.class);
				HouseData houseData = HouseModel.getHouse(e.getPlayer().getLocation());
				if(!playerLifecycle.isInHouse() && houseData != null) {
					playerLifecycle.setInHouse(true);
					e.getPlayer().sendMessage("Du befindest dich nun im Haus von '" + houseData.getPlayerName() + "'!");
				} else if (playerLifecycle.isInHouse() && houseData == null) {
					playerLifecycle.setInHouse(false);
					e.getPlayer().sendMessage("Du hast das Haus verlassen!");
				}
			}
		});

//PlayerDisconnectEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerDisconnectEvent.class, (e) -> {
			if(hasHouseData(e.getPlayer().getName())){
				saveHouse(e.getPlayer().getName());
			}
		});

//PlayerSpawnEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerSpawnEvent.class, (e) -> {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(e.getPlayer(), PlayerData.class);
			if(hasHouseData(e.getPlayer().getName())){
				if(playerLifecycle.isHouseSpawn()){
					HouseData houseData = getHouseData(e.getPlayer().getName());
					if (houseData.getSpawnLocation() != null){
					/*	if(houseData.getSpawnLocation().x != 0.0f
						|| houseData.getSpawnLocation().y != 0.0f
						|| houseData.getSpawnLocation().z != 0.0f){*/
							Shoebill.get().runOnSampThread(() -> e.getPlayer().setLocation(houseData.isInitialized()?getHouseData(e.getPlayer().getName()).getSpawnLocation():new Location(
									houseData.getSpawnLocation().x,
									houseData.getSpawnLocation().y,
									houseData.getSpawnLocation().z+1000.0f
							)));
					//	}
					}
				}
			}
		});

//PlayerDeathEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerDeathEvent.class, (e) -> {
			
		});
		
//PlayerKeyStateChangeEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerKeyStateChangeEvent.class, (e) -> {
			
		});
		
//PlayerEditObjectEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerEditObjectEvent.class, (e) -> {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(e.getPlayer(), PlayerData.class);
			/*if (playerLifecycle.getEditObject() == e.getObject()
			&& e.getEditResponse() == ObjectEditResponse.UPDATE) {
				HouseModel.moveModel(playerLifecycle.getEditHouseOwner(), e.getNewLocation(), getHouseData(playerLifecycle.getEditHouseOwner()).getHouseId());
			}*/
			if(playerLifecycle.getEditObject() == e.getObject()
			&& e.getEditResponse() == ObjectEditResponse.FINAL) {
				HouseModel.moveModel(playerLifecycle.getEditHouseOwner(), new Location(e.getNewLocation().getX(), e.getNewLocation().getY(), e.getNewLocation().getZ()-0.85f), getHouseData(playerLifecycle.getEditHouseOwner()).getHouseId());
				HouseModel.finishEditModel(e.getPlayer(), playerLifecycle.getEditHouseOwner());
			}
			if(playerLifecycle.getEditObject() == e.getObject()
			&& e.getEditResponse() == ObjectEditResponse.CANCEL) {
				if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(playerLifecycle.getEditHouseOwner())){
					e.getPlayer().sendMessage(playerLifecycle.getEditHouseOwner() + " hat gar kein Haus!");
				} else {
					HouseSystem.getInstance().getMysqlConnection().updateSpawnLocation(playerLifecycle.getEditHouseOwner(), new Vector3D(0,0,0));
					HouseModel.destroyModel(playerLifecycle.getEditHouseOwner());
					HouseSystem.getInstance().getPlayerManager().uninitHouse(playerLifecycle.getEditHouseOwner());
					HouseSystem.getInstance().getMysqlConnection().deleteHouse(playerLifecycle.getEditHouseOwner());
					e.getPlayer().sendMessage("Du hast das Haus von '"+playerLifecycle.getEditHouseOwner()+"' zerstoert!");
				}
				e.getObject().destroy();
			}
		});
	}
	
	void initHouse(String playerName){
		if(Player.get(playerName) != null){
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(Player.get(playerName), PlayerData.class);
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName)){
				playerLifecycle.setHouseSpawn(HouseSystem.getInstance().getMysqlConnection().isHouseSpawn(playerName));
				HouseData houseData;
				if(!hasHouseData(playerName)) houseData = new HouseData(playerName, HouseSystem.getInstance().getMysqlConnection().getHouseId(playerName));
				else houseData = getHouseData(playerName);
				houseData.setLevel(HouseSystem.getInstance().getMysqlConnection().getHouseLevel(playerName));
				houseData.setLocation(HouseSystem.getInstance().getMysqlConnection().getHouseLocation(playerName));
				houseData.setRotation(HouseSystem.getInstance().getMysqlConnection().getHouseRotation(playerName));
				houseData.setModel(HouseSystem.getInstance().getMysqlConnection().getHouseModel(playerName));
				houseData.setOpen(false);
			//	houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
				addHouseData(playerName, houseData);
			}
			else {
				playerLifecycle.setHouseSpawn(false);
			}
		} else {
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName)){
				HouseData houseData;
				if(!hasHouseData(playerName)) houseData = new HouseData(playerName, HouseSystem.getInstance().getMysqlConnection().getHouseId(playerName));
				else houseData = getHouseData(playerName);
				houseData.setLevel(HouseSystem.getInstance().getMysqlConnection().getHouseLevel(playerName));
				houseData.setLocation(HouseSystem.getInstance().getMysqlConnection().getHouseLocation(playerName));
				houseData.setRotation(HouseSystem.getInstance().getMysqlConnection().getHouseRotation(playerName));
				houseData.setModel(HouseSystem.getInstance().getMysqlConnection().getHouseModel(playerName));
				houseData.setOpen(false);
			//	houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
				addHouseData(playerName, houseData);
			}
		}
	}

	void uninitHouse(String playerName){
		if(Player.get(playerName) != null){
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(Player.get(playerName), PlayerData.class);
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName) && hasHouseData(playerName)){
				playerLifecycle.setHouseSpawn(false);
				deleteHouseData(playerName);
			}
		} else {
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName) && hasHouseData(playerName)){
				deleteHouseData(playerName);
			}
		}
	}
	
	void saveHouse(String playerName){
		HouseData houseData = getHouseData(playerName);
		HouseSystem.getInstance().getMysqlConnection().updateHouse(playerName, "house_model", houseData.getModel());
		HouseSystem.getInstance().getMysqlConnection().updateHouse(playerName, "house_level", houseData.getLevel());
		HouseSystem.getInstance().getMysqlConnection().updateHouseLocation(playerName, houseData.getLocation());
		HouseSystem.getInstance().getMysqlConnection().updateHouseRotation(playerName, houseData.getRotation());
		HouseSystem.getInstance().getMysqlConnection().updateHouse(playerName, "house_spawn", playerLifecycle.isHouseSpawn()?1:0);
	}

    public void addHouseData(String playerName, HouseData houseData){
		houseDataMap.put(playerName, houseData);
    }

    public HouseData getHouseData(String playerName){
        return houseDataMap.get(playerName);
    }

	public boolean hasHouseData(String playerName){
    	if(!houseDataMap.containsKey(playerName)) return false;
		else return true;
	}

	public void deleteHouseData(String playerName){
    	if(houseDataMap.containsKey(playerName)) 
    		while(hasHouseData(playerName))
    			houseDataMap.remove(playerName);
	}

	public void uninitialize()
	{
		
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public boolean isDestroyed() {
		return true;
	}
}
