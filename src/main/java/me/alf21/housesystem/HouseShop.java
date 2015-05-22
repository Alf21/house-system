package me.alf21.housesystem;

import java.io.IOException;

import net.gtaun.shoebill.object.Player;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class HouseShop {
	public PlayerData playerLifecycle;
	
	public void shop(Player player) throws IOException {
		playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		//TODO
	}
}
