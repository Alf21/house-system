package me.alf21.housesystem;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.object.*;
import net.gtaun.util.event.HandlerPriority;

import java.util.HashMap;

//import net.gtaun.shoebill.constant.SpecialAction;
//import net.gtaun.shoebill.object.Timer;

/**
 * Created by Alf21 on 28.04.2015 in project weapon-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

/*
 * TODO:
 *         
 * - Wenn man durch eine Explosion getoetet wurde, welche durch einen Schuss eines Spielers ausgeloest wurde:
 *   Lsg.: Bekommen der Position der Explosion
 *         Bekommen des Spielers, der dorthin bzw. in die Naehe geschossen hat
 *         Ihn als Killer setzen
 *         
 * - Animation, wenn man Weaponshop betritt, zB hinhocken und in ner Tasche rumkramen usw...
 */

public class PlayerManager implements Destroyable {
	public PlayerData playerLifecycle;
	private HashMap<String, HouseData> houseDataMap;
	public PlayerCommandManager commandManager;

	public PlayerManager()
	{
		this.houseDataMap = new HashMap<>();
		
		commandManager = new PlayerCommandManager(HouseSystem.getInstance().getEventManagerInstance());
	    commandManager.registerCommands(new Commands());
	    
		commandManager.installCommandHandler(HandlerPriority.NORMAL);

//PlayerConnectEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerConnectEvent.class, (e) -> {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(e.getPlayer(), PlayerData.class);
			initHouse(e.getPlayer().getName());
			
			if(hasHouseData(e.getPlayer().getName())){
				if(playerLifecycle.isHouseSpawn()){
					HouseData houseData = getHouseData(e.getPlayer().getName());
					Shoebill.get().runOnSampThread(() -> e.getPlayer().setSpawnInfo(houseData.getLocation().x, houseData.getLocation().y, houseData.getLocation().z, 
							0, 0, 0.0f, 0, 0, WeaponModel.get(0), 0, WeaponModel.get(0), 0, WeaponModel.get(0), 0));
				}
			}
		});
		
//PlayerWeaponShotEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerWeaponShotEvent.class, (e) -> {
			
		});
		
//PlayerGiveDamageEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerGiveDamageEvent.class, (e) -> {
			
		});
		
//PlayerUpdateEvent
		HouseSystem.getInstance().getEventManagerInstance().registerHandler(PlayerUpdateEvent.class, (e) -> {
			
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
					if(getHouseData(e.getPlayer().getName()).getSpawnLocation() != e.getPlayer().getLocation()){
						e.getPlayer().setLocation(getHouseData(e.getPlayer().getName()).getSpawnLocation());
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
	}
	
	void initHouse(String playerName){
		if(Player.get(playerName) != null){
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(Player.get(playerName), PlayerData.class);
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName)){
				playerLifecycle.setHouseId(HouseSystem.getInstance().getMysqlConnection().getHouseId(playerName));
				playerLifecycle.setHouseSpawn(HouseSystem.getInstance().getMysqlConnection().isHouseSpawn(playerName));
				HouseData houseData = new HouseData(playerName, HouseSystem.getInstance().getMysqlConnection().getHouseId(playerName));
				houseData.setLevel(HouseSystem.getInstance().getMysqlConnection().getHouseLevel(playerName));
				houseData.setLocation(HouseSystem.getInstance().getMysqlConnection().getHouseLocation(playerName));
				houseData.setModel(HouseSystem.getInstance().getMysqlConnection().getHouseModel(playerName));
				houseData.setOpen(false);
				houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData.getModel()));
				addHouseData(playerName, houseData);
			}
			else {
				playerLifecycle.setHouseSpawn(false);
			}
		} else {
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName)){
				HouseData houseData = new HouseData(playerName, HouseSystem.getInstance().getMysqlConnection().getHouseId(playerName));
				houseData.setLevel(HouseSystem.getInstance().getMysqlConnection().getHouseLevel(playerName));
				houseData.setLocation(HouseSystem.getInstance().getMysqlConnection().getHouseLocation(playerName));
				houseData.setModel(HouseSystem.getInstance().getMysqlConnection().getHouseModel(playerName));
				houseData.setOpen(false);
				houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData.getModel()));
				addHouseData(playerName, houseData);
			}
		}
	}

	void uninitHouse(String playerName){
		if(Player.get(playerName) != null){
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(Player.get(playerName), PlayerData.class);
			if(HouseSystem.getInstance().getMysqlConnection().exist(playerName) && hasHouseData(playerName)){
				playerLifecycle.setHouseId(0);
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
    	if(houseDataMap.containsKey(playerName)) houseDataMap.remove(playerName);
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