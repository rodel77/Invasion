package xyz.rodeldev.invasion.invasion;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import xyz.rodeldev.invasion.Main;

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
