package me.alf21.housesystem;

import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Vector3D;
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
			//	HouseModel.moveModel(player.getName(), new Location(player.getLocation().x+10.0f, player.getLocation().y, player.getLocation().z), modelId);
				player.sendMessage("Du hast dein Haus gebaut! Platziere es nun an der richtigen Stelle...");
				HouseModel.editModel(player, player.getName());
			} else {
				player.sendMessage(Color.RED, "Invalid modelId! ('" + modelId + "' does not exist)");
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To create a buyable house")
	public boolean acreatehouse(Player player, int modelId){
		if(!player.isAdmin()){
			player.sendMessage("Du bist dazu nicht berechtigt!");
		} else {
			String randomName = HouseSystem.getInstance().getMysqlConnection().aCreateHouse("buyable_", modelId, player.getLocation().x, player.getLocation().y, player.getLocation().z);
			if(HouseModel.initialize(randomName, modelId)){	
				HouseSystem.getInstance().getMysqlConnection().createHouse(randomName, modelId, player.getLocation().x, player.getLocation().y, player.getLocation().z);
				HouseSystem.getInstance().getPlayerManager().initHouse(randomName);
			//	HouseModel.moveModel(randomName, new Location(player.getLocation().x, player.getLocation().y, player.getLocation().z), modelId);
				player.sendMessage("Du hast ein Haus gebaut! Platziere es nun an der richtigen Stelle...");
				HouseModel.editModel(player, randomName);
			} else {
				player.sendMessage(Color.RED, "Invalid modelId! ('" + modelId + "' does not exist)");
			}
		}
		return true;
	}

	//getHouses (über Dialog regeln !)
	
	@Command
	@CommandHelp("To destroy an house")
	public boolean adestroyhouse(Player player, int houseId){ //bezogen auf die HouseId (in MySQL Tabelle)
		if(!player.isAdmin()){
			player.sendMessage("Du bist dazu nicht berechtigt!");
		} else {
			//check if house is buyable, else alert that house has an owner
			String houseOwner = HouseSystem.getInstance().getMysqlConnection().getHouseOwner(houseId);
			if(HouseSystem.getInstance().getPlayerManager().hasHouseData(houseOwner)){
				HouseModel.destroyModel(houseOwner);
				HouseSystem.getInstance().getPlayerManager().uninitHouse(houseOwner);
				HouseSystem.getInstance().getMysqlConnection().deleteHouse(houseOwner);
				player.sendMessage("Du hast dein Haus zerstoert!");
			} else {
				player.sendMessage("Falsche houseId ('" + houseId + "')!");
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To destroy an house")
	public boolean adestroyhouse(Player player){ //bezogen auf die Location (Haus in dem man steht)
		if(!player.isAdmin()){
			player.sendMessage("Du bist dazu nicht berechtigt!");
		} else {
			HouseData houseData = HouseModel.getHouse(player.getLocation());
			if(houseData != null) {
				HouseModel.destroyModel(houseData.getPlayerName());
				HouseSystem.getInstance().getPlayerManager().uninitHouse(houseData.getPlayerName());
				HouseSystem.getInstance().getMysqlConnection().deleteHouse(houseData.getPlayerName());
				player.sendMessage("Du hast das Haus von '"+houseData.getPlayerName()+"' zerstoert!");
			} else {
				player.sendMessage("Du befindest dich in keinem Haus!");
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
			HouseSystem.getInstance().getMysqlConnection().updateSpawnLocation(player.getName(), new Vector3D(0,0,0));
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
	@CommandHelp("To open/close a door") //TODO: Wenn man schnell hintereinander /door eingibt und das objekt noch nicht fertig gemoved ist vllt error mit den datas, ob tür geöffnet ist usw.
	public boolean door(Player player, int doorId){ //TODO: Auch einbrechen können, wenn nicht abgeschlossen ist!
		if (!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())) {
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			if (houseData.isOpen()) {
				if (doorId <= 0 || doorId > houseData.getDoors().size()) {
					player.sendMessage(Color.YELLOW, "Die Tür " + doorId + " gibt es nicht! Gebe /getDoors ein, um die Türen aufgelistet zu bekommen.");
				} else if (houseData.getDoors().size() > 0) {
					if(!houseData.isDoorOpen(doorId)){
						SampObject door = houseData.getDoors().get(doorId-1);
						door.move(door.getLocation().x, door.getLocation().y, door.getLocation().z+2, 2000);
						player.sendMessage(Color.YELLOW, "Die Tür " + doorId + " wird geöffnet!");
						houseData.setDoorStatus(doorId, true);
						HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
					} else {
						SampObject door = houseData.getDoors().get(doorId-1);
						door.move(door.getLocation().x, door.getLocation().y, door.getLocation().z-2, 2000);
						player.sendMessage(Color.YELLOW, "Die Tür " + doorId + " wird geschlossen!");
						houseData.setDoorStatus(doorId, false);
						HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
					}
				} else {
					player.sendMessage(Color.RED, "Du hast keine Türen in deinem Haus!");
				}
			} else {
				player.sendMessage(Color.YELLOW, "Dein Haus ist verschlossen, öffne es mit /openhouse !");
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
	public boolean housespawn(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
			playerLifecycle.setHouseSpawn(playerLifecycle.isHouseSpawn()?false:true);
			player.sendMessage(playerLifecycle.isHouseSpawn()?"Du spawnst jetzt in deinem Haus!":"Du spawnst jetzt nicht mehr in deinem Haus!");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To set own spawn location")
	public boolean changespawn(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			if (!HouseModel.isInHouse(player)) player.sendMessage(Color.YELLOW, "Du bist nicht in deinem Haus!");
			else {
				HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
				houseData.setSpawnLocation(player.getLocation());
				HouseSystem.getInstance().getMysqlConnection().updateSpawnLocation(player.getName(), houseData.getSpawnLocation());
				HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
				playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
				playerLifecycle.setHouseSpawn(true);
				player.sendMessage(Color.YELLOW, "Du spawnst nun an deiner eigenen gewählten Stelle im Haus!");
			}
		}
		return true;
	}
	
	@Command
	@CommandHelp("To remove own spawn location")
	public boolean removespawn(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseSystem.getInstance().getMysqlConnection().updateSpawnLocation(player.getName(), new Vector3D(0,0,0));
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
			HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
			player.sendMessage(Color.YELLOW, "Du spawnst nun an der normalen Stelle im Haus!");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To remove own spawn location")
	public boolean househelp(Player player){
		player.sendMessage("/aCreateHouse /changeSpawn /createHouse");
		player.sendMessage("/destroyHouse /door /getDoors /houseSpawn");
		player.sendMessage("/openHouse /removeSpawn /aDestroyHouse");
		return true;
	}
}
