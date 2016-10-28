package main.java.mx.com.rodel.minigame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.utils.Util;

public class MinigameManager {
	private static MinigameManager instance;
	
	private List<String> nodes = Arrays.asList(new String[] {"world", "mobspawns", "playerspawn"}); 
	private HashMap<String, InvasionMinigameArenaData> arenas = new HashMap<>();
	
	public static MinigameManager getInstance(){
		return instance;
	}
	
	public MinigameManager(){
		instance = this;
	}
	
	public InvasionMinigameArenaData getData(File arenaFile){
		try {
			FileConfiguration arena = new YamlConfiguration();
			arena.load(arenaFile);
			
			for(String node : nodes){
				if(!arena.contains(node)){
					Bukkit.getLogger().warning("Error loading "+arenaFile.getName()+", can't found "+'"'+node+'"'+" node");
					return null;
				}
			}
			
			World world = Bukkit.getWorld(UUID.fromString(arena.getString("world")));
			List<Location> locations = new ArrayList<>();
			
			for(String loc : arena.getStringList("mobspawns")){
				locations.add(Util.stringToLocation(loc, world));
			}
			
			return new InvasionMinigameArenaData() {
				@Override
				public List<Location> getMobSpawns() {
					return locations;
				}
				
				@Override
				public Location getArenaSpawn() {
					return Util.stringToLocation(arena.getString("playerspawn"), world);
				}
				
				@Override
				public UUID getWorld(){
					return world.getUID();
				}
			};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap<String, InvasionMinigameArenaData> getArenas(){
		return arenas;
	}
	
	public void loadArenas(){
		for(File arena : getArenasFolder().listFiles()){
			if(arena.getName().endsWith(".yml")){
				loadArena(arena);
			}
		}
	}
	
	public void loadArena(File arena){
		arenas.put(arena.getName().replace(".yml", ""), getData(arena));
	}
	
	public File getArenasFolder(){
		File folder = new File(Main.getInstance().arenasP);
		if(!folder.exists()){
			folder.mkdirs();
		}
		return folder;
	}
}
