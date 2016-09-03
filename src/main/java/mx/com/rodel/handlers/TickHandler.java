package main.java.mx.com.rodel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.config.ConfigNodes;
import main.java.mx.com.rodel.config.StringConfig;
import main.java.mx.com.rodel.invasion.Invasion;
import main.java.mx.com.rodel.invasion.InvasionManager;
import main.java.mx.com.rodel.invasion.InvasionPlayer;
import main.java.mx.com.rodel.utils.Util;

public class TickHandler implements Runnable{
	
	Main pl;
	
	public TickHandler(Main pl) {
		this.pl = pl;
	}
	
	@Override
	public void run() {
		try {
			if(pl.getConfig().getBoolean(ConfigNodes.RANDOMSPAWNS_ENABLED.getKey(), true) && Math.random()*100<pl.getConfig().getDouble(ConfigNodes.RANDOMSPAWMNS_PROBABILITY.getKey(), 0.3D)){
				if(pl.getServer().getOnlinePlayers().size()!=0){
					final List<UUID> playerL = new ArrayList<UUID>();
					for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
						for(Entry<UUID, InvasionPlayer> players : invasion.getValue().getPlayers().entrySet()){
							playerL.add(players.getKey());
						}
					}
					
					for(Player player : pl.getServer().getOnlinePlayers()){
						if(!playerL.contains(player.getUniqueId()) && !pl.getWorldInvasions().containsKey(player.getWorld().getUID())){
							player.sendMessage(Util.translate(pl.getString(StringConfig.APPROACHING)));
							player.playSound(player.getLocation(), "entity.elder_guardian.curse", 1, 0);
						}
					}
					
					pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
						public void run() {
							List<UUID> valid = new ArrayList<UUID>();
							for(Player player : pl.getServer().getOnlinePlayers()){
								if(!playerL.contains(player.getUniqueId())){
									valid.add(player.getUniqueId());
								}
							}
							
							if(valid.size()!=0){
								Player player = pl.getServer().getPlayer(valid.get(new Random().nextInt(valid.size())));
								new InvasionManager().startInvasion(player.getLocation(), pl);
							}
						}
					}, 600);
				}
			}
		} catch (Exception e) {}
	}
}