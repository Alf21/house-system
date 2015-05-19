package me.alf21.housesystem;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Timer;

/**
 * Created by Alf21 on 28.04.2015 in project weapon_system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

class PlayerData extends PlayerLifecycleObject {
    private Player player;
    private int money;
	private Timer playerTimer;
	private int houseId;
	private boolean houseSpawn;

    public PlayerData(EventManager eventManager, Player player) {
        super(eventManager, player);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    int getMoney() {
        return money;
    }

    void setMoney(int money) {
        this.money = money;
    }
    
    Timer getPlayerTimer() {
    	if(playerTimer == null){
    		playerTimer = new Timer();
    		return playerTimer;
    	}
    	else return playerTimer;
	}
    
    void setPlayerTimer(Timer playerTimer) {
		this.playerTimer = playerTimer;
	}
    
    int getHouseId() {
		return houseId;
	}
    
    void setHouseId(int houseId) {
		this.houseId = houseId;
	}
    
    boolean isHouseSpawn() {
		return houseSpawn;
	}
    
    void setHouseSpawn(boolean houseSpawn) {
		this.houseSpawn = houseSpawn;
	}
    

    @Override
    protected void onInit() {
    
    }

    @Override
    protected void onDestroy() {
    	
    }
}
