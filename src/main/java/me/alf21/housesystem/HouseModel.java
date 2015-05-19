package me.alf21.housesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.SampObject;

public class HouseModel {
	public static void createModel(String playerName, Location location, int modelId){
		for(int i=0; i<=HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects().size();i++){
			if(modelId == i && HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getModel() == i){
				for(SampObject object : HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects()){
					float x = object.getLocation().x-HouseSystem.getInstance().houseLocations.get(i).x+location.x;
					float y = object.getLocation().y-HouseSystem.getInstance().houseLocations.get(i).y+location.y;
					float z = object.getLocation().z-HouseSystem.getInstance().houseLocations.get(i).z+location.z;
					object.setLocation(new Location(x, y, z));
				}
			}
		}
	}
	
	public static void destroyModel(String playerName, Location location, int modelId){
		for(int i=0; i<=HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects().size();i++){
			if(modelId == i){
				destroyObjects(HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).getObjects());
			}
		}
	}

	public static ArrayList<SampObject> loadObjectData(String filename) {
		String str, lineText = "";
		ArrayList<SampObject> objects = new ArrayList<>();
		try {
			File fl = new File(HouseSystem.getInstance().getDataDir(), filename);
	        if (fl.exists()) {
	            BufferedReader br = new BufferedReader(new FileReader(fl));

	    	    while((lineText = br.readLine()) != null)
	    	    {
    	    		str = lineText.trim();
    	    		
    	    		str.replace("CreateObject(", "");
    	    		str.replace("CreateDynamicObject(", ""); //TODO
    	    		str.replace(");", "");
    	    		
    	    	    str = str.split("\\/\\/")[0];
    	    		
    	    		String[] parts = str.split("[,]");
                    if (parts.length >= 7) {
                    	int modelId = Integer.parseInt(parts[0]);
                        float x = Float.parseFloat(parts[1]);
                        float y = Float.parseFloat(parts[2]);
                        float z = Float.parseFloat(parts[3]);
                        float rX = Float.parseFloat(parts[4]);
                        float rY = Float.parseFloat(parts[5]);
                        float rZ = Float.parseFloat(parts[6]);
                        objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ)));
                    } else if (parts.length >= 8){
                    	int modelId = Integer.parseInt(parts[0]);
                        float x = Float.parseFloat(parts[1]);
                        float y = Float.parseFloat(parts[2]);
                        float z = Float.parseFloat(parts[3]);
                        float rX = Float.parseFloat(parts[4]);
                        float rY = Float.parseFloat(parts[5]);
                        float rZ = Float.parseFloat(parts[6]);
                        float drawDistance = Float.parseFloat(parts[7]);
                        objects.add(HouseSystem.getInstance().getShoebill().getSampObjectManager().createObject(modelId, new Location(x, y, z), new Vector3D(rX, rY, rZ), drawDistance));
                    }
	    	    }

	    	    br.close();
	        }
	    } catch (Exception ex) {
	    	System.out.println("[Fehler] Verbindung zur Datei "+filename+" ("+HouseSystem.getInstance().getDataDir()+"\\"+filename+") konnte nicht hergestellt werden!");
	    	ex.printStackTrace();
	    }
		return objects;
	}
	
	public static Location loadLocationData(ArrayList<SampObject> objects){
		int normalX = 0;
		int normalY = 0;
		int normalZ = 0;
		int i = 0;
		for(SampObject object : objects){
			normalX += object.getLocation().x;
			normalY += object.getLocation().y;
			normalZ += object.getLocation().z;
			i++;
		}
		normalX /= i;
		normalY /= i;
		normalZ /= i;
		
		return new Location(normalX, normalY, normalZ);
	}
	
	public static void destroyObjects(ArrayList<SampObject> objects){
		for(SampObject object : objects){
			object.destroy();
		}
	}
	
	public static void initialize(String playerName, int modelId){
		try {
			String filename = "";
			if(!HouseSystem.getInstance().getDataDir().isDirectory()) throw new IllegalStateException("Error in initialization");
			for(File file : HouseSystem.getInstance().getDataDir().listFiles(new RegexFileFilter(modelId + "_*\\.txt"))) {
				filename = file.getName();
			}
			
			ArrayList<SampObject> objects = loadObjectData(filename);
		    int houseId = Integer.parseInt(filename.split("_")[0]);
		    HouseSystem.getInstance().houseLocations.put(houseId, loadLocationData(objects));
		    HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).setObjects(objects);
		    HouseSystem.getInstance().getPlayerManager().getHouseData(playerName).setModel(houseId);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static Vector3D loadSpawnLocation(int modelId) {
		return new Vector3D(1958.3783f, 1343.1572f, 15.3746f);
	}
}
