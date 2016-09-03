package main.java.mx.com.rodel.invasion;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.config.StringConfig;
import main.java.mx.com.rodel.messages.Messenger;

/**
 * WARNING
 * 
 * This class is current in development and is a optimization for "Minigame Update"
 * 
 * It's a api class for create invasion and when the dev end's this class go to take the name "Invasion"
 * 
 * @author rodel77
 */
public abstract class CustomInvasion {
	protected Main pl;
	private HashMap<UUID, InvasionPlayer> players = new HashMap<>();
	
	public CustomInvasion() {
		pl = Main.getInstance();
	}
	
	public HashMap<UUID, InvasionPlayer> getPlayers(){
		return players;
	}
	
	public void addPlayer(Player player){
		InvasionPlayer ip = new InvasionPlayer();
		
		ip.setInInvasion(true);
		ip.setSender(player);
		
		players.put(player.getUniqueId(), ip);
		sendMessageToAll(StringConfig.JOINUSER.getFromConfig().replace("{PLAYER}", player.getName()), player.getUniqueId());
		Messenger.sendMessage(player, StringConfig.JOIN);
	}
	
	public void removePlayer(Player player){
		InvasionPlayer ip;
		if(players.containsKey(player.getUniqueId())){
			ip = players.get(player.getUniqueId());
			ip.setInInvasion(false);
			
			players.put(player.getUniqueId(), ip);
			sendMessageToAll(StringConfig.LEAVEUSER.getFromConfig().replace("{PLAYER}", player.getName()), player.getUniqueId());
			Messenger.sendMessage(player, StringConfig.LEAVE);
		}
	}
	
	public void sendMessageToAll(String message){
		sendMessageToAll(message, null);
	}
	
	public void sendMessageToAll(String message, UUID exception){
		for(Entry<UUID, InvasionPlayer> player : players.entrySet()){
			if(exception==null || exception.equals(player.getKey())){
				Messenger.sendMessageRaw(player.getValue().getSender(), message);
			}
		}
	}
	
	public abstract void onInvasionStart();
	
	public abstract void onInvasionTick();
	
	public void stopInvasion(){
		
	}
}
