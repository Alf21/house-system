package me.alf21.housesystem;

import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.SampObject;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class Commands {
	private PlayerData playerLifecycle;
	
	@Command
	@CommandHelp("To create an house")
	public boolean createhouse(Player player, int modelId){
		if(HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast bereits ein Haus! Du kannst es mit /destroyHouse löschen");
		} else {
			if(HouseModel.initialize(player.getName(), modelId)){	
				HouseSystem.getInstance().getMysqlConnection().createHouse(player.getName(), modelId, player.getLocation().x, player.getLocation().y, player.getLocation().z);
				HouseSystem.getInstance().getPlayerManager().initHouse(player.getName());
				HouseModel.createModel(player.getName(), new Location(player.getLocation().x, player.getLocation().y, player.getLocation().z), modelId);
				if (HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getSpawnLocation() != null) 
					player.setLocation(HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getSpawnLocation());
				player.sendMessage("Du hast ein Haus gebaut! Es gehoert nun dir.");
			} else {
				player.sendMessage(Color.RED, "Invalid modelId! ('" + modelId + "' does not exist)");
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To destroy an house")
	public boolean destroyhouse(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseModel.destroyModel(player.getName());
			HouseSystem.getInstance().getPlayerManager().uninitHouse(player.getName());
			HouseSystem.getInstance().getMysqlConnection().deleteHouse(player.getName());
			player.sendMessage("Du hast dein Haus zerstoert!");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To open/close your house")
	public boolean openhouse(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			houseData.setOpen(houseData.isOpen()?false:true);
			HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
			player.sendMessage(houseData.isOpen()?"Du hast dein Haus geoeffnet!":"Du hast dein Haus geschlossen!");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To open/close a door")
	public boolean opendoor(Player player, int doorId){ //TODO: Auch einbrechen können, wenn nicht abgeschlossen ist!
		if (!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())) {
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			if (houseData.isOpen()) {
				if (doorId <= 0 || doorId > houseData.getDoors().size()) {
					player.sendMessage(Color.YELLOW, "Die Tür " + doorId + " gibt es nicht! Gebe /getDoors ein, um die Türen aufgelistet zu bekommen.");
				} else if (houseData.getDoors().size() > 0) {
					SampObject door = houseData.getDoors().get(doorId-1);
					door.move(door.getLocation().x, door.getLocation().y, door.getLocation().z+2, 2000);
					player.sendMessage(Color.YELLOW, "Die Tür " + doorId + " wird geöffnet!");
				} else {
					player.sendMessage(Color.RED, "Du hast keine Türen in deinem Haus!");
				}
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To open/close a door")
	public boolean getdoors(Player player){
		if (!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())) {
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			if (houseData.getDoors() == null || houseData.getDoors().size() < 1) {
				player.sendMessage(Color.RED, "Du besitzt keine Türen!");
			} else {
				player.sendMessage(Color.YELLOW, "------ Türen ------");
				for (int i = 1; i <= houseData.getDoors().size(); i++) {
					player.sendMessage(Color.YELLOW, "Tür " + i);
				}
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To get all house objects")
	public boolean getobjects(Player player){
		if (!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())) {
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			player.sendMessage(Color.YELLOW, "------ Objects ------");
			for (SampObject object : houseData.getObjects()) {
				player.sendMessage(Color.YELLOW, object.toString());
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To spawn (not) in your house")
	public boolean changehousespawn(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
			playerLifecycle.setHouseSpawn(playerLifecycle.isHouseSpawn()?false:true);
			player.sendMessage(playerLifecycle.isHouseSpawn()?"Du spawnst jetzt in deinem Haus!":"Du spawnst jetzt nicht mehr in deinem Haus!");
		}
		return true;
	}
}
