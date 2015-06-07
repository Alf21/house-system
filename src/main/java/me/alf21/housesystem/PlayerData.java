package me.alf21.housesystem;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.SampObject;
import net.gtaun.util.event.EventManager;

import java.util.Timer;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

class PlayerData extends PlayerLifecycleObject {
    private Player player;
    private int money;
	private Timer playerTimer;
	private boolean houseSpawn;
	private SampObject editObject;
	private String editHouseOwner;
	private boolean inHouse;

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
    
    boolean isHouseSpawn() {
		return houseSpawn;
	}
    
    void setHouseSpawn(boolean houseSpawn) {
		this.houseSpawn = houseSpawn;
	}
    
    SampObject getEditObject() {
		return editObject;
	}
    void setEditObject(SampObject editObject) {
		this.editObject = editObject;
	}
    
    String getEditHouseOwner() {
		return editHouseOwner;
	}
    void setEditHouseOwner(String editHouseOwner) {
		this.editHouseOwner = editHouseOwner;
	}
    
    boolean isInHouse() {
		return inHouse;
	}
    void setInHouse(boolean inHouse) {
		this.inHouse = inHouse;
	}
    

    @Override
    protected void onInit() {
    
    }

    @Override
    protected void onDestroy() {
    	
    }
}
