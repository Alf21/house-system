package me.alf21.housesystem;

import net.gtaun.shoebill.object.Player;

import java.io.IOException;

public class HouseShop {
	public PlayerData playerLifecycle;
	
	public void shop(Player player) throws IOException {
		playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		//TODO
	}
}
