package mx.com.rodel.invasion.invasion;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.config.StringConfig;
import mx.com.rodel.invasion.messages.Messenger;

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
	
	/**
	 * Add one player to invasion
	 * 
	 * @param player
	 */
	public void addPlayer(Player player){
		InvasionPlayer ip = new InvasionPlayer();
		
		ip.setInInvasion(true);
		ip.setSender(player);
		
		players.put(player.getUniqueId(), ip);
		sendMessageToAll(StringConfig.JOINUSER.getFromConfig().replace("{PLAYER}", player.getName()), player.getUniqueId());
		Messenger.sendMessage(player, StringConfig.JOIN);
	}
	
	/**
	 * Remove one player to invasion
	 * <br>
	 * If you remove someone his data go to still in invasion for give credis in the final
	 * @param player
	 */
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
	
	/**
	 * This method execute when invasion start
	 */
	public abstract void onInvasionStart();
	
	/**
	 * This method execute each 50 ticks
	 */
	public void onInvasionTick(){
		
	}
	
	/**
	 * Call this method for stop invasion
	 * <br>
	 * You can make your own stop effect and then add super.stopInvasion();
	 * <br>
	 * Or only call it for set default effects, stop etc...
	 */
	public void stopInvasion(){
		
	}
}
