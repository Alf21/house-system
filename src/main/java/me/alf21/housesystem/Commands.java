package me.alf21.housesystem;

import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

public class Commands {
	private PlayerData playerLifecycle;
	
	@Command
	@CommandHelp("To create an house")
	public boolean createHouse(Player player, int modelId){
		if(HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast bereits ein Haus! Du kannst es mit /deleteHouse löschen");
		} else {
			HouseSystem.getInstance().getMysqlConnection().createHouse(player.getName(), modelId, player.getLocation().x, player.getLocation().y, player.getLocation().z);
			HouseSystem.getInstance().getPlayerManager().initHouse(player.getName());
			if(modelId == 1){
				HouseModel.createModel(player.getName(), HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getLocation(), modelId);
			}
			player.setLocation(HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getSpawnLocation());
			player.sendMessage("Du hast ein Haus gebaut! Es gehoert nun dir.");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To delete an house")
	public boolean deleteHouse(Player player){
		if(!HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			player.sendMessage("Du hast gar kein Haus! Du kannst eines mit /createHouse erstellen");
		} else {
			HouseModel.destroyModel(player.getName(), HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getLocation(), HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName()).getModel());
			HouseSystem.getInstance().getPlayerManager().uninitHouse(player.getName());
			HouseSystem.getInstance().getMysqlConnection().deleteHouse(player.getName());
			player.sendMessage("Du hast dein Haus zerstoert!");
		}
		return true;
	}
	
	@Command
	@CommandHelp("To open/close your house")
	public boolean openHouse(Player player){
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
	@CommandHelp("To spawn (not) in your house")
	public boolean changeHouseSpawn(Player player){
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
