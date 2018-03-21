package xyz.rodeldev.invasion.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import xyz.rodeldev.invasion.invasion.Invasion;
import xyz.rodeldev.invasion.invasion.InvasionManager;
import xyz.rodeldev.invasion.invasion.InvasionPlayer;
import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.ConfigNodes;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.utils.Util;

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
