package main.java.mx.com.rodel.utils;

import java.util.List;

import main.java.mx.com.rodel.Main;

public class WorldBlockManager {
	Main pl;
	
	public WorldBlockManager(Main pl){
		this.pl = pl;
	}
	
	public boolean worldIsBloked(String worldName){
		if(pl.getServer().getWorld(worldName)!=null){
			List<String> list = pl.getConfig().getStringList("config.blockedworlds");
			if(list.contains(worldName)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void blockWorld(String worldName){
		if(!worldIsBloked(worldName)){
			List<String> list = pl.getConfig().getStringList("config.blockedworlds");
			list.add(worldName);
			pl.getConfig().set("config.blockedworlds", list);
			pl.saveConfig();
		}
	}
	
	public void unblockWorld(String worldName){
		if(worldIsBloked(worldName)){
			List<String> list = pl.getConfig().getStringList("config.blockedworlds");
			list.remove(worldName);
			pl.getConfig().set("config.blockedworlds", list);
			pl.saveConfig();
		}		
	}
	
	public WorldResponse switchWorld(String worldName){
		if(worldIsBloked(worldName)){
			unblockWorld(worldName);
			return WorldResponse.UNBLOCKED;
		}else{
			blockWorld(worldName);
			return WorldResponse.BLOCKED;
		}
	}
}