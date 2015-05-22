package me.alf21.housesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.SampObject;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/

public class HouseModel {
	public static void createModel(String playerName, Location location, int modelId){
		if(HouseSystem.getInstance().getPlayerManager().hasHouseData(playerName)){
			for(SampObject object : HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects()){
				float x = object.getLocation().x-HouseSystem.getInstance().houseLocations.get(modelId).x+location.x;
				float y = object.getLocation().y-HouseSystem.getInstance().houseLocations.get(modelId).y+location.y;
				float z = object.getLocation().z-HouseSystem.getInstance().houseLocations.get(modelId).z+location.z;
				Shoebill.get().runOnSampThread(() -> object.setLocation(new Location(x, y, z)));
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
	                        float z = Float.parseFloat(parts[3].trim());
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
	                        float z = Float.parseFloat(parts[3].trim());
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
			normalX += object.getLocation().x;
			normalY += object.getLocation().y;
			normalZ += object.getLocation().z;
			i++;
		}
		normalX = normalX / i;
		normalY = normalY / i;
		normalZ = normalZ / i;
		
		return new Location(normalX, normalY, normalZ);
	}
	
	public static void destroyObjects(ArrayList<SampObject> objects){
		Shoebill.get().runOnSampThread(() -> {
			for(SampObject object : objects){
				object.destroy();
				if(!object.isDestroyed()){
					System.out.println("ERROR: " + object);
				}
			}
		});
	}
	
	public static boolean initialize(String playerName, int modelId){
		try {
			String filename = "";
			File dir = new File(HouseSystem.getInstance().getDataDir(), HouseSystem.folderName);
			if(!dir.isDirectory()) throw new IllegalStateException("Error in initialization");
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
			    if(HouseSystem.getInstance().spawnLocations.get(modelId) != null) houseData.setSpawnLocation(HouseSystem.getInstance().spawnLocations.get(modelId));
			    //To initialize the doors
			    for (SampObject object : objects) {
			    	if (isDoor(object.getModelId())) {
			    		houseData.addDoors(object);
			    	}
			    }
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
	
	public static Vector3D loadSpawnLocation(int modelId) {
		Vector3D spawn = HouseSystem.getInstance().spawnLocations.get(modelId);
		if(spawn != null) return spawn;
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
}
