package me.alf21.housesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.SampObject;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class HouseModel {
	public static void moveModel(String playerName, Location location, int modelId){
		//TODO: save Rotation
		if (location != null) {
			if (HouseSystem.getInstance().getPlayerManager().hasHouseData(playerName)) {
				HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(playerName);
				if (!houseData.isInitialized()) {
					float minX = 0.0f, maxX = 0.0f, minY = 0.0f, maxY = 0.0f, minZ = 0.0f, maxZ = 0.0f;
					boolean ready = true;
					for (SampObject object : HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects()) {
						float x = object.getLocation().getX()-HouseSystem.getInstance().houseLocations.get(modelId).getX()+location.getX();
						float y = object.getLocation().getY()-HouseSystem.getInstance().houseLocations.get(modelId).getY()+location.getY();
						float z = object.getLocation().getZ()-HouseSystem.getInstance().houseLocations.get(modelId).getZ()+location.getZ();
						object.setLocation(new Location(x, y, z+1000.0f+0.85f));
					//	object.setLocation(new Location(x, y, z+1000.0f));
						
						if (ready) {
							ready = false;
							minX = maxX = x;
							minY = maxY = y;
							minZ = maxZ = z;
						}
						if (minX > x) minX = x;
						if (maxX < x) maxX = x;
						if (minY > y) minY = y;
						if (maxY < y) maxY = y;
						if (minZ > z) minZ = z;
						if (maxZ < z) maxZ = z;
					}
					houseData.setMinX(minX);
					houseData.setMaxX(maxX);
					houseData.setMinY(minY);
					houseData.setMaxY(maxY);
					houseData.setMinZ(minZ+1000.0f+0.85f);
					houseData.setMaxZ(maxZ+1000.0f+0.85f);
				//	houseData.setMinZ(minZ+1000.0f);
				//	houseData.setMaxZ(maxZ+1000.0f);
				} else {
					float minX = 0.0f, maxX = 0.0f, minY = 0.0f, maxY = 0.0f, minZ = 0.0f, maxZ = 0.0f;
					boolean ready = true;
					//Abstand zwischen altem und neuem Mittelpunkt zu jedem Objekt dazurechnen
					float normalX = 0.0f;
					float normalY = 0.0f;
					float normalZ = 0.0f;
					int i = 0;
					for(SampObject object : houseData.getObjects()){
						normalX += object.getLocation().getX();
						normalY += object.getLocation().getY();
						normalZ += object.getLocation().getZ();
						i++;
					}
					normalX = normalX / i;
					normalY = normalY / i;
					normalZ = normalZ / i;
					
					for (SampObject object : houseData.getObjects()) {
						float x = object.getLocation().getX()+(location.getX() - normalX);
						float y = object.getLocation().getY()+(location.getY() - normalY);
						float z = object.getLocation().getZ()+(location.getZ() - normalZ);
						object.setLocation(new Location(x, y, z));
						
						if (ready) {
							ready = false;
							minX = maxX = x;
							minY = maxY = y;
							minZ = maxZ = z;
						}
						if (minX > x) minX = x;
						if (maxX < x) maxX = x;
						if (minY > y) minY = y;
						if (maxY < y) maxY = y;
						if (minZ > z) minZ = z;
						if (maxZ < z) maxZ = z;
					}
					houseData.setMinX(minX);
					houseData.setMaxX(maxX);
					houseData.setMinY(minY);
					houseData.setMaxY(maxY);
					houseData.setMinZ(minZ);
					houseData.setMaxZ(maxZ);
				}
				houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
				houseData.setInitialized(true);
				HouseSystem.getInstance().getPlayerManager().addHouseData(playerName, houseData);
				
			//TODO: set Rotation
			/*	
			 	for(SampObject object : houseData.getObjects()){
					
				}
			*/
			}
		}
	}
	
	public static void destroyModel(String playerName){
		if(HouseSystem.getInstance().getPlayerManager().hasHouseData(playerName)){
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(playerName);
			destroyObjects(houseData.getObjects());
			houseData.setObjects(new ArrayList<SampObject>());
			HouseSystem.getInstance().getPlayerManager().addHouseData(playerName, houseData);
		}
	}

	public static ArrayList<SampObject> loadObjectData(String filename) {
		String str, lineText = "";
		ArrayList<SampObject> objects = new ArrayList<>();
		try {
			File fl = new File(filename);
	        if (fl.exists()) {
	            BufferedReader br = new BufferedReader(new FileReader(fl));

	    	    while((lineText = br.readLine()) != null)
	    	    {
    	    		str = lineText.trim();
    	    		
    	    		if (str.matches("\\bCreateObject\\b.*")) {
	    	    		str = str.replaceAll("CreateObject\\(", "");
	    	    		str = str.replaceAll("\\)\\;", "");
	    	    		
	    	    	    str = str.split("\\/\\/")[0];
	    	    		
	    	    		String[] parts = str.split("[,]");
	                    if (parts.length == 7 || parts.length == 8) {
	                    	int modelId = Integer.parseInt(parts[0].trim());
	                        float x = Float.parseFloat(parts[1].trim());
	                        float y = Float.parseFloat(parts[2].trim());
	                        float z = Float.parseFloat(parts[3].trim())-1000.0f;
	                        float rX = Float.parseFloat(parts[4].trim());
	                        float rY = Float.parseFloat(parts[5].trim());
	                        float rZ = Float.parseFloat(parts[6].trim());
	                        if (parts.length == 7) objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ)));
	                        else {
	                            float drawDistance = Float.parseFloat(parts[7].trim());
	                            objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ), drawDistance));
	                        }
	                    }
    	    		} else if(str.matches("\\bCreateDynamicObject\\b.*")) {
    	    			str = str.replaceAll("CreateDynamicObject\\(", ""); //TODO: Include with Matcher whether use Icognitos Streamer Plugin or none   
	    	    		str = str.replaceAll("\\)\\;", "");
    	    			
    	    			str = str.split("\\/\\/")[0];
	    	    		
	    	    		String[] parts = str.split("[,]");
	                    if (parts.length == 7 || parts.length == 8) {
	                    	int modelId = Integer.parseInt(parts[0].trim());
	                        float x = Float.parseFloat(parts[1].trim());
	                        float y = Float.parseFloat(parts[2].trim());
	                        float z = Float.parseFloat(parts[3].trim())-1000.0f;
	                        float rX = Float.parseFloat(parts[4].trim());
	                        float rY = Float.parseFloat(parts[5].trim());
	                        float rZ = Float.parseFloat(parts[6].trim());
	                        if (parts.length == 7) objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ)));
	                        else {
	                            float drawDistance = Float.parseFloat(parts[7].trim());
	                            objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ), drawDistance));
	                        }
	                    }
    	    		} else if (str.matches("\\bSetPlayerSpawn\\b.*")) {
	    	    		str = str.replaceAll("SetPlayerSpawn\\(", "");
	    	    		str = str.replaceAll("\\)\\;", "");

    	    			str = str.split("\\/\\/")[0];
	    	    		
	    	    		String[] parts = str.split("[,]");
	                    if (parts.length == 3) { //TODO: ANGEL
	                        float x = Float.parseFloat(parts[0].trim());
	                        float y = Float.parseFloat(parts[1].trim());
	                        float z = Float.parseFloat(parts[2].trim());
	                        /*
	                        float rX = Float.parseFloat(parts[4].trim());
	                        float rY = Float.parseFloat(parts[5].trim());
	                        float rZ = Float.parseFloat(parts[6].trim());
	                        */
	                        int houseId = Integer.parseInt(fl.getName().split("_")[0]);
	                        HouseSystem.getInstance().spawnLocations.put(houseId, new Vector3D(x, y, z));
	                    }
    	    		} else {
    	    			continue;
    	    		}
    	    		//TODO: addGate() 
    	    		//TODO: addDoor() 
    	    		//TODO: removeGate() 
    	    		//TODO: removeDoor()
    	    		//TODO: setAngle()
	    	    }

	    	    br.close();
	        }/* else {
    	    	System.out.println("File '" + filename + "' ("+fl.getAbsolutePath()+"\\...) does not exist...");
	        }*/
	    } catch (Exception ex) {
	    	System.out.println("[Fehler] Verbindung zur Datei '"+filename+"' konnte nicht hergestellt werden!");
	    	ex.printStackTrace();
	    }
		return objects;
	}
	
	public static Location loadLocationData(ArrayList<SampObject> objects){
		float normalX = 0.0f;
		float normalY = 0.0f;
		float normalZ = 0.0f;
		int i = 0;
		for(SampObject object : objects){
			normalX += object.getLocation().getX();
			normalY += object.getLocation().getY();
			normalZ += object.getLocation().getZ()+1000.0f;
			i++;
		}
		normalX = normalX / (float) i;
		normalY = normalY / (float) i;
		normalZ = normalZ / (float) i;
		
		return new Location(normalX, normalY, normalZ);
	}
	
	public static void destroyObjects(ArrayList<SampObject> objects){
		/*for(SampObject object : objects){
			object.destroy();
			if(!object.isDestroyed()){
				System.out.println("ERROR: " + object);
			}
		}*/
		objects.forEach((object) -> object.destroy());
	}
	
	public static boolean initialize(String playerName, int modelId){
		try {
			String filename = "";
			File dir = new File(HouseSystem.getInstance().getDataDir(), HouseSystem.folderName);
			if(!dir.isDirectory()) throw new IllegalStateException("Error in initialization - No folder '" + HouseSystem.folderName + "' in DataDir !");
			for(File file : dir.listFiles(new RegexFileFilter(modelId + "_.*\\.txt"))) {
				filename = file.getAbsolutePath();
			}
			
			ArrayList<SampObject> objects = loadObjectData(filename);
			if (objects.size() != 0 && objects != null) {
		//    int houseId = Integer.parseInt(filename.split("_")[0]);
				Location location = loadLocationData(objects);
			    HouseSystem.getInstance().houseLocations.put(modelId, location);
			    HouseData houseData;
			    if (!HouseSystem.getInstance().getPlayerManager().hasHouseData(playerName)) houseData = new HouseData(playerName, modelId);
			    else houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(playerName);
			    houseData.setObjects(objects);
			    houseData.setModel(modelId);
		    //To initialize the doors
			    for (SampObject object : objects) {
			    	if (isDoor(object.getModelId())) {
			    		houseData.addDoors(object);
			    	}
			    	else if (isGate(object.getModelId())) {
			    		houseData.addGates(object);
			    	}
			    }
			    houseData.setInitialized(false);
			    HouseSystem.getInstance().getPlayerManager().addHouseData(playerName, houseData);
                return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
	
	public static Vector3D loadSpawnLocation(HouseData houseData) {
		if (HouseSystem.getInstance().spawnLocations.containsKey(houseData.getHouseId())) {
			if(HouseSystem.getInstance().getMysqlConnection().check("samp_housesystem", "spawn_X", "0")
			&& HouseSystem.getInstance().getMysqlConnection().check("samp_housesystem", "spawn_Y", "0")
			&& HouseSystem.getInstance().getMysqlConnection().check("samp_housesystem", "spawn_Z", "0")) {
				Vector3D spawn = HouseSystem.getInstance().spawnLocations.get(houseData.getHouseId());
				if(spawn != null) {
					float normalX = 0.0f;
					float normalY = 0.0f;
					float normalZ = 0.0f;
					int i = 0;
					for(SampObject object : houseData.getObjects()){
						object = SampObject.get(object.getId());
						normalX += object.getLocation().getX();
						normalY += object.getLocation().getY();
						normalZ += object.getLocation().getZ();
						i++;
					}
					normalX = normalX / (float) i;
					normalY = normalY / (float) i;
					normalZ = normalZ / (float) i;

					float x = spawn.getX() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getX() + normalX;
					float y = spawn.getY() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getY() + normalY;
					float z = spawn.getZ() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getZ() + normalZ;
					
					spawn = new Vector3D(x, y, z-1000.0f);
					return spawn;
				}
				else {
					float normalX = 0.0f;
					float normalY = 0.0f;
					float normalZ = 0.0f;
					int i = 0;
					for(SampObject object : houseData.getObjects()){
						object = SampObject.get(object.getId());
						normalX += object.getLocation().getX();
						normalY += object.getLocation().getY();
						normalZ += object.getLocation().getZ();
						i++;
					}
					normalX = normalX / (float) i;
					normalY = normalY / (float) i;
					normalZ = normalZ / (float) i;
					
					return new Vector3D(houseData.getMaxX() + 5, normalY, houseData.getMinZ());
				}
			}
			else {
				float 	x = HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_X"),
						y = HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_Y"),
						z = HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_Z");
				if(x != 0 && y != 0 && z != 0) {
					return new Vector3D(x, y, z);
				}
			}
		}
		else {
			float normalX = 0.0f;
			float normalY = 0.0f;
			float normalZ = 0.0f;
			int i = 0;
			for(SampObject object : houseData.getObjects()){
				object = SampObject.get(object.getId());
				normalX += object.getLocation().getX();
				normalY += object.getLocation().getY();
				normalZ += object.getLocation().getZ();
				i++;
			}
			normalX = normalX / (float) i;
			normalY = normalY / (float) i;
			normalZ = normalZ / (float) i;
			
			return new Vector3D(houseData.getMaxX() + 5, normalY, houseData.getMinZ());
		}
		return new Vector3D();
	}
	
	public static boolean isDoor(int modelId){
		int doors[] = new int[]{
			977,
			1491,
			1492,
			1493,
			1494,
			1495,
			1496,
			1497,
			1498,
			1499,
			1500,
			1501,
			1502,
			1503,
			1504,
			1505,
			1506,
			1507,
			1518,
			1522,
			1523,
			1532,
			1533,
			1534,
			1535,
			1536,
			1537,
			1538,
			1555,
			1556,
			1557,
			1560,
			1561,
			1566,
			1567,
			1569,
			1965,
			1967,
			2004,
			2664,
			2634,
			3061,
			3278,
			13360,
			14483,
			14638
		};
		for (int i = 0; i < doors.length; i++){
			if(doors[i] == modelId) return true;
		}
		return false;
	}
	
	public static boolean isGate(int modelId) {
		int gates[] = new int[] {
			1966,
			1980,
			3352,
			3294,
			3354,
			4084,
			5422,
			5043,
			5302,
			5340,
			5779,
			5856,
			6400,
			7927,
			7930,
			7931,
			8378,
			8948,
			9093,
			9099,
			9625,
			9823,
			10149,
			10154,
			10182,
			10246,
			10575,
			11102,
			11313,
			11319,
			11359,
			11360,
			11416,
			13028,
			13187,
			13188,
			13817
		};
		for (int i = 0; i < gates.length; i++){
			if(gates[i] == modelId) return true;
		}
		return false;
	}
	
	public static boolean isInHouse(Player player){
		if(HouseSystem.getInstance().getPlayerManager().hasHouseData(player.getName())){
			float x = player.getLocation().getX();
			float y = player.getLocation().getY();
			float z = player.getLocation().getZ();
			HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(player.getName());
			if (x > houseData.getMaxX() || x < houseData.getMinX()) return false;
			if (y > houseData.getMaxY() || y < houseData.getMinY()) return false;
			if (z > houseData.getMaxZ() || z < houseData.getMinZ()) return false;
			return true;
		}
		return false;
	}
	
	public static boolean isInHouse(Location location, HouseData houseData){
		if(houseData != null){
			float x = location.getX();
			float y = location.getY();
			float z = location.getZ();
			if (x > houseData.getMaxX() || x < houseData.getMinX()) return false;
			if (y > houseData.getMaxY() || y < houseData.getMinY()) return false;
			if (z > houseData.getMaxZ() || z < houseData.getMinZ()) return false;
			return true;
		}
		return false;
	}
	
	public static HouseData getHouse(Location location){
		for (HouseData houseData : HouseSystem.getInstance().getPlayerManager().houseDataMap.values()) {
			if(isInHouse(location, houseData)){
				return houseData;
			}
		}
		return null;
	}

	public static void editModel(Player player, String houseOwner) {
		HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(houseOwner);
		float x, y, z; x = y = z = 0.0f;
		int i = 0;
		for (SampObject object : houseData.getObjects()) {
			x += object.getLocation().getX();
			y += object.getLocation().getY();
			z += object.getLocation().getZ();
			i++;
		}
		
		final float normalX = x/(float) i;
		final float normalY = y/(float) i;
		final float normalZ = z/(float) i;
		
		SampObject object = SampObject.create(1253, new Location(player.getLocation().getX() + 10.0f, player.getLocation().getY(), player.getLocation().getZ() + 0.85f), new Vector3D(0.0f, 0.0f, 0.0f));
		houseData.getObjects().forEach((houseDataObject) -> {
			float x1 = houseDataObject.getLocation().getX()-normalX;
			float y1 = houseDataObject.getLocation().getY()-normalY;
			float z1 = houseDataObject.getLocation().getZ()-normalZ;
			float rX1 = houseDataObject.getRotation().getX();
			float rY1 = houseDataObject.getRotation().getY(); 
			float rZ1 = houseDataObject.getRotation().getZ();
		/*
			player.sendMessage("------------------------------ (ID: '"+houseDataObject.getModelId()+"')");
			player.sendMessage(houseDataObject.getRotation().getX() + " -> " + rX1);
			player.sendMessage(houseDataObject.getRotation().getY() + " -> " + rY1);
			player.sendMessage(houseDataObject.getRotation().getZ() + " -> " + rZ1);
			player.sendMessage("--------------------------------------------");
			player.sendMessage("");
		*/
			houseDataObject.attach(object, new Location(x1, y1, z1), new Vector3D(rX1, rY1, rZ1), true);
		});
		
		PlayerData playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		playerLifecycle.setEditObject(object);
		playerLifecycle.setEditHouseOwner(houseOwner);
		player.editObject(object);
	}
	
	public static void finishEditModel(Player player, String houseOwner) {
		HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(houseOwner);
		PlayerData playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		houseData.setRotation(playerLifecycle.getEditObject().getRotation());
		playerLifecycle.getEditObject().destroy();
		playerLifecycle.setEditObject(null);
		if (houseData.getSpawnLocation() != null)
			if(houseData.getSpawnLocation().getX() != 0.0f
			&& houseData.getSpawnLocation().getY() != 0.0f
			&& houseData.getSpawnLocation().getZ() != 0.0f) 
				player.setLocation(houseData.getSpawnLocation());
		HouseSystem.getInstance().getPlayerManager().addHouseData(player.getName(), houseData);
		player.sendMessage("Du hast das Haus platziert!");
	}
	
//-----------------------------------------------------------------------------------------------
	static Vector3D EDIT_FloatGetRemainder(float rot_x, float rot_y, float rot_z)
	{
	    rot_x = EDIT_FloatRemainder(rot_x, 360.0f);
	    rot_y = EDIT_FloatRemainder(rot_y, 360.0f);
	    rot_z = EDIT_FloatRemainder(rot_z, 360.0f);
	    return new Vector3D(rot_x, rot_y, rot_z);
	}
	 
	static float EDIT_FloatRemainder(float remainder, float value)
	{
	    if(remainder >= value) while(remainder >= value) remainder -= value;
	    else if(remainder < 0.0) while(remainder < 0.0) remainder += value;
	    return remainder;
	}
	
	static boolean floatcmp(float a, float b) {
        final float epsilon = 0.0000001f;
		
		final float absA = Math.abs(a);
		final float absB = Math.abs(b);
		final float diff = Math.abs(a - b);

		if (a == b) return true;
		else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) return diff < (epsilon * Float.MIN_NORMAL);
		else return diff / Math.min((absA + absB), Float.MAX_VALUE) < epsilon;
	}
}
