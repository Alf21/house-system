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
					
					houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
					houseData.setInitialized(true);
					HouseSystem.getInstance().getPlayerManager().addHouseData(playerName, houseData);
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
					
					houseData.setSpawnLocation(HouseModel.loadSpawnLocation(houseData));
					houseData.setInitialized(true);
					HouseSystem.getInstance().getPlayerManager().addHouseData(playerName, houseData);
				}
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
		normalX = normalX / i;
		normalY = normalY / i;
		normalZ = normalZ / i;
		
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
			 //TODO   if(HouseSystem.getInstance().spawnLocations.get(modelId) != null) houseData.setSpawnLocation(HouseSystem.getInstance().spawnLocations.get(modelId));
			    //To initialize the doors
			    for (SampObject object : objects) {
			    	if (isDoor(object.getModelId())) {
			    		houseData.addDoors(object);
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
			|| HouseSystem.getInstance().getMysqlConnection().check("samp_housesystem", "spawn_Y", "0")
			|| HouseSystem.getInstance().getMysqlConnection().check("samp_housesystem", "spawn_Z", "0")) {
				Vector3D spawn = HouseSystem.getInstance().spawnLocations.get(houseData.getHouseId());
				if(HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()) != null
				&& spawn != null) {
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
					normalX = normalX / i;
					normalY = normalY / i;
					normalZ = normalZ / i;
					/*
					float x = spawn.x + (HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).x - normalX);
					float y = spawn.y + (HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).y - normalY);
					float z = spawn.z + (HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).z - normalZ);
					*/
					float x = spawn.getX() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getX() + normalX;
					float y = spawn.getY() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getY() + normalY;
					float z = spawn.getZ() - HouseSystem.getInstance().houseLocations.get(houseData.getHouseId()).getZ() + normalZ;
					
					spawn = new Vector3D(x, y, z-1000.0f);
				}
				if(spawn != null) return spawn;
			} else {
				return new Vector3D(
					HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_X"),
					HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_Y"),
					HouseSystem.getInstance().getMysqlConnection().getFloat("samp_housesystem", houseData.getPlayerName(), "spawn_Z")
				);
			}
		}
		return null;
	}
	
	public static boolean isDoor(int modelId){
		if(modelId == 1491
		|| modelId == 1492
		|| modelId == 1493
		|| modelId == 1494
		|| modelId == 1495
		|| modelId == 1496
		|| modelId == 1497
		|| modelId == 1498
		|| modelId == 1499
		|| modelId == 1500
		|| modelId == 1502
		|| modelId == 1504
		|| modelId == 1505
		|| modelId == 1506
		|| modelId == 1507
		|| modelId == 1508
		|| modelId == 1522 //?
		|| modelId == 1523 //?
		|| modelId == 1532
		|| modelId == 1533
		|| modelId == 1535
		|| modelId == 1536
		|| modelId == 1537
		|| modelId == 1538
		|| modelId == 1552 //?
		|| modelId == 1555 //?
		|| modelId == 1556
		|| modelId == 1557
		|| modelId == 1560
		|| modelId == 1561
		|| modelId == 1566
		|| modelId == 1567
		|| modelId == 1569)
			return true;
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
		
		final float normalX = x/i;
		final float normalY = y/i;
		final float normalZ = z/i;
		
		SampObject object = SampObject.create(1253, new Location(player.getLocation().getX() + 10.0f, player.getLocation().getY(), player.getLocation().getZ() + 0.85f), new Vector3D(0, 0, 0));
		houseData.getObjects().forEach((houseDataObject) -> {
			float x1 = houseDataObject.getLocation().getX()-normalX;
			float y1 = houseDataObject.getLocation().getY()-normalY;
			float z1 = houseDataObject.getLocation().getZ()-normalZ;
			float rX1 = houseDataObject.getRotation().getX(); //rX1+=rX1!=0?rX1:0;
			float rY1 = houseDataObject.getRotation().getY(); 
			float rZ1 = houseDataObject.getRotation().getZ(); //rZ1+=rZ1!=0?rZ1:0;

			Vector3D vector3d = AttachObjectToObjectEx(object.getId(), rX1, rY1, rZ1);
			rX1 = vector3d.getX();
			rY1 = vector3d.getY();
			rZ1 = vector3d.getZ();
			
			player.sendMessage("------------------------------ (ID: '"+houseDataObject.getModelId()+"')");
			player.sendMessage(houseDataObject.getRotation().getX() + " -> " + rX1);
			player.sendMessage(houseDataObject.getRotation().getY() + " -> " + rY1);
			player.sendMessage(houseDataObject.getRotation().getZ() + " -> " + rZ1);
			player.sendMessage("--------------------------------------------");
			player.sendMessage("");
			
			houseDataObject.attach(object, x1, y1, z1, rX1, rY1, rZ1, true);
		});
		
		PlayerData playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		playerLifecycle.setEditObject(object);
		playerLifecycle.setEditHouseOwner(houseOwner);
		player.editObject(object);
	}
	
	public static void finishEditModel(Player player, String houseOwner) {
		HouseData houseData = HouseSystem.getInstance().getPlayerManager().getHouseData(houseOwner);
		PlayerData playerLifecycle = HouseSystem.getInstance().getPlayerLifecycleHolder().getObject(player, PlayerData.class);
		playerLifecycle.getEditObject().destroy();
		playerLifecycle.setEditObject(null);
		if (houseData.getSpawnLocation() != null)
			if(houseData.getSpawnLocation().getX() != 0.0f
			|| houseData.getSpawnLocation().getY() != 0.0f
			|| houseData.getSpawnLocation().getZ() != 0.0f) 
				player.setLocation(houseData.getSpawnLocation());
		player.sendMessage("Du hast das Haus platziert!");
	}
	
//-----------------------------------------------------------------------------------------------
/*	Vector3D[] AttachObjectToObjectEx(int attachoid, float off_x, float off_y, float off_z, float rot_x, float rot_y, float rot_z, float X, float Y, float Z, float RX, float RY, float RZ) // By Stylock - http://forum.sa-mp.com/member.php?u=114165
	{
		//return X, Y, Z, RX, RY, RZ
		Vector3D newRotation;
	    float sin[] = new float[3], cos[] = new float[3], pos[] = new float[3], rot[] = new float[3];
	
	    SampObject object = SampObject.get(attachoid);
	    pos[0] = object.getLocation().getX();
	    pos[1] = object.getLocation().getY();
	    pos[2] = object.getLocation().getZ();
	    rot[0] = object.getRotation().getX();
	    rot[1] = object.getRotation().getY();
	    rot[2] = object.getRotation().getZ();
	    
        newRotation = EDIT_FloatEulerFix(rot[0], rot[1], rot[2]);
        rot[0] = newRotation.getX();
        rot[1] = newRotation.getY();
        rot[2] = newRotation.getZ();
        
        cos[0] = (float) Math.toDegrees(Math.cos(rot[0])); cos[1] = (float) Math.toDegrees(Math.cos(rot[1])); cos[2] = (float) Math.toDegrees(Math.cos(rot[2])); sin[0] = (float) Math.toDegrees(Math.sin(rot[0])); sin[1] = (float) Math.toDegrees(Math.sin(rot[1])); sin[2] = (float) Math.toDegrees(Math.sin(rot[2]));
        pos[0] = pos[0] + off_x * cos[1] * cos[2] - off_x * sin[0] * sin[1] * sin[2] - off_y * cos[0] * sin[2] + off_z * sin[1] * cos[2] + off_z * sin[0] * cos[1] * sin[2];
        pos[1] = pos[1] + off_x * cos[1] * sin[2] + off_x * sin[0] * sin[1] * cos[2] + off_y * cos[0] * cos[2] + off_z * sin[1] * sin[2] - off_z * sin[0] * cos[1] * cos[2];
        pos[2] = pos[2] - off_x * cos[0] * sin[1] + off_y * sin[0] + off_z * cos[0] * cos[1];
        rot[0] = (float) Math.asin(cos[0] * cos[1]); rot[1] = (float) Math.atan2(sin[0], cos[0] * sin[1]) + rot_z; rot[2] = (float) Math.atan2(cos[1] * cos[2] * sin[0] - sin[1] * sin[2], cos[2] * sin[1] - cos[1] * sin[0] * -sin[2]);
        cos[0] = (float) Math.toDegrees(Math.cos(rot[0])); cos[1] = (float) Math.toDegrees(Math.cos(rot[1])); cos[2] = (float) Math.toDegrees(Math.cos(rot[2])); sin[0] = (float) Math.toDegrees(Math.sin(rot[0])); sin[1] = (float) Math.toDegrees(Math.sin(rot[1])); sin[2] = (float) Math.toDegrees(Math.sin(rot[2]));
        rot[0] = (float) Math.asin(cos[0] * sin[1]); rot[1] = (float) Math.atan2(cos[0] * cos[1], sin[0]); rot[2] = (float) Math.atan2(cos[2] * sin[0] * sin[1] - cos[1] * sin[2], cos[1] * cos[2] + sin[0] * sin[1] * sin[2]);
        cos[0] = (float) Math.toDegrees(Math.cos(rot[0])); cos[1] = (float) Math.toDegrees(Math.cos(rot[1])); cos[2] = (float) Math.toDegrees(Math.cos(rot[2])); sin[0] = (float) Math.toDegrees(Math.sin(rot[0])); sin[1] = (float) Math.toDegrees(Math.sin(rot[1])); sin[2] = (float) Math.toDegrees(Math.sin(rot[2]));
        rot[0] = (float) Math.atan2(sin[0], cos[0] * cos[1]) + rot_x; rot[1] = (float) Math.asin(cos[0] * sin[1]); rot[2] = (float) Math.atan2(cos[2] * sin[0] * sin[1] + cos[1] * sin[2], cos[1] * cos[2] - sin[0] * sin[1] * sin[2]);
        cos[0] = (float) Math.toDegrees(Math.cos(rot[0])); cos[1] = (float) Math.toDegrees(Math.cos(rot[1])); cos[2] = (float) Math.toDegrees(Math.cos(rot[2])); sin[0] = (float) Math.toDegrees(Math.sin(rot[0])); sin[1] = (float) Math.toDegrees(Math.sin(rot[1])); sin[2] = (float) Math.toDegrees(Math.sin(rot[2]));
        rot[0] = (float) Math.asin(cos[1] * sin[0]); rot[1] = (float) Math.atan2(sin[1], cos[0] * cos[1]) + rot_y; rot[2] = (float) Math.atan2(cos[0] * sin[2] - cos[2] * sin[0] * sin[1], cos[0] * cos[2] + sin[0] * sin[1] * sin[2]);
        X = pos[0];
        Y = pos[1];
        Z = pos[2];
        RX = rot[0];
        RY = rot[1];
        RZ = rot[2];
        
        Vector3D[] objData = {new Vector3D(X, Y, Z), new Vector3D(RX, RY, RZ)};
        return objData;
	} */
	static Vector3D AttachObjectToObjectEx(int attachoid, float rot_x, float rot_y, float rot_z) // By Stylock - http://forum.sa-mp.com/member.php?u=114165
	{
	    double sin[] = new double[3], cos[] = new double[3], rot[] = new double[3];
	
	    SampObject object = SampObject.get(attachoid);
	    Vector3D newRotation = EDIT_FloatEulerFix(object.getRotation().getX(), object.getRotation().getY(), object.getRotation().getZ());
        rot[0] = (double) newRotation.getX();
        rot[1] = (double) newRotation.getY();
        rot[2] = (double) newRotation.getZ();
        
        cos[0] = Math.cos(rot[0]); cos[1] = Math.cos(rot[1]); cos[2] = Math.cos(rot[2]); sin[0] = Math.sin(rot[0]); sin[1] = Math.sin(rot[1]); sin[2] = Math.sin(rot[2]);
        
        rot[0] = Math.asin(cos[0] * cos[1]); 
        rot[1] = Math.atan2(Math.toDegrees(sin[0]), Math.toDegrees(cos[0] * sin[1])) + Math.toRadians(rot_z); 
        rot[2] = Math.atan2(Math.toDegrees(cos[1] * cos[2] * sin[0] - sin[1] * sin[2]), Math.toDegrees(cos[2] * sin[1] - cos[1] * sin[0] * -sin[2]));
        
        cos[0] = Math.cos(rot[0]); cos[1] = Math.cos(rot[1]); cos[2] = Math.cos(rot[2]); sin[0] = Math.sin(rot[0]); sin[1] = Math.sin(rot[1]); sin[2] = Math.sin(rot[2]);
        
        rot[0] = Math.asin(cos[0] * sin[1]); 
        rot[1] = Math.atan2(Math.toDegrees(cos[0] * cos[1]), Math.toDegrees(sin[0])); 
        rot[2] = Math.atan2(Math.toDegrees(cos[2] * sin[0] * sin[1] - cos[1] * sin[2]), Math.toDegrees(cos[1] * cos[2] + sin[0] * sin[1] * sin[2]));
        
        cos[0] = Math.cos(rot[0]); cos[1] = Math.cos(rot[1]); cos[2] = Math.cos(rot[2]); sin[0] = Math.sin(rot[0]); sin[1] = Math.sin(rot[1]); sin[2] = Math.sin(rot[2]);
        
        rot[0] = Math.atan2(Math.toDegrees(sin[0]), Math.toDegrees(cos[0] * cos[1])) + Math.toRadians(rot_x); 
        rot[1] = Math.asin(cos[0] * sin[1]);
        rot[2] = Math.atan2(Math.toDegrees(cos[2] * sin[0] * sin[1] + cos[1] * sin[2]), Math.toDegrees(cos[1] * cos[2] - sin[0] * sin[1] * sin[2]));
        
        cos[0] = Math.cos(rot[0]); cos[1] = Math.cos(rot[1]); cos[2] = Math.cos(rot[2]); sin[0] = Math.sin(rot[0]); sin[1] = Math.sin(rot[1]); sin[2] = Math.sin(rot[2]);
        
        rot[0] = Math.asin(cos[1] * sin[0]); 
        rot[1] = Math.atan2(Math.toDegrees(sin[1]), Math.toDegrees(cos[0] * cos[1])) + Math.toRadians(rot_y);
        rot[2] = Math.atan2(Math.toDegrees(cos[0] * sin[2] - cos[2] * sin[0] * sin[1]), Math.toDegrees(cos[0] * cos[2] + sin[0] * sin[1] * sin[2]));
        
        rot[0] = Math.toDegrees(rot[0]);
        rot[1] = Math.toDegrees(rot[1]);
        rot[2] = Math.toDegrees(rot[2]);
        
        return new Vector3D((float) rot[0], (float) rot[1], (float) rot[2]);
	}
	 
	 
	static Vector3D EDIT_FloatEulerFix(float rot_x, float rot_y, float rot_z)
	{
	    Vector3D vector3d = EDIT_FloatGetRemainder(rot_x, rot_y, rot_z);
	    rot_x = vector3d.getX();
	    rot_y = vector3d.getY();
	    rot_z = vector3d.getZ();
	    if((!floatcmp(rot_x, 0.0f) || !floatcmp(rot_x, 360.0f))
	    && (!floatcmp(rot_y, 0.0f) || !floatcmp(rot_y, 360.0f)))
	    {
	        rot_y = 0.0000002f;
	    }
	    return new Vector3D(rot_x, rot_y, rot_z);
	}
	 
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
	
/*	
	public static boolean floatcmp(float da, float db) {
	    //    double da = 3 * .3333333333;
	    //    double db = 0.99999992857;
        final float EPSILON = 0.0000001f;
        
        if(da == db) return true;
        else if(equals(da, db, EPSILON)) return true;
        else return false;
    }
    
    public static boolean equals(float a, float b, float eps) {
        return Math.abs(a - b) < eps;
    }
*/
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
