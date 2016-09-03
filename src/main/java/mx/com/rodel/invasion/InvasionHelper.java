package main.java.mx.com.rodel.invasion;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import main.java.mx.com.rodel.Main;

public class InvasionHelper {
	public static boolean playerIsInvasion(Player player){
		for(Entry<Integer, Invasion> invasion : Main.getInstance().getInvasions().entrySet()){
			return invasion.getValue().getActivePlayers().containsKey(player.getUniqueId());
		}
		return false;
	}
	
	public static Invasion getPlayerInvasion(){
		for(Entry<Integer, Invasion> invasion : Main.getInstance().getInvasions().entrySet()){
			return invasion.getValue();
		}
		return null;
	}
}
