package xyz.rodeldev.invasion.worldinvasion;

import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.ConfigNodes;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.invasion.InvasionResponse;
import xyz.rodeldev.invasion.invasion.InvasionSpecialEvent;
import xyz.rodeldev.invasion.invasion.InvasionState;
import xyz.rodeldev.invasion.utils.Util;

public class WorldInvasionManager {
	Main pl;
	
	public WorldInvasionManager(Main pl) {
		this.pl = pl;
	}
	
	public InvasionResponse startInvasion(World world, InvasionSpecialEvent event){
		InvasionResponse response = InvasionResponse.STARTED;
		if(pl.getConfig().getList(ConfigNodes.BLOCKEDWORLDS.getKey()).contains(world.getName()) && pl.getWorldInvasions().containsKey(world.getUID())){
			response = InvasionResponse.WORLD;
		}
		
		if(pl.getWorldInvasions().containsKey(world.getUID())){
			response = InvasionResponse.WORLDININVASION;
		}
		
		if(response==InvasionResponse.STARTED){
			final WorldInvasion invasion = new WorldInvasion();
			invasion.setMain(pl);
			invasion.setWorld(world);
			invasion.setState(InvasionState.STARTING);
			invasion.setGoal(50*world.getPlayers().size());
			invasion.setEvent(event);
			
			for(Player player : world.getPlayers()){
				if(event==InvasionSpecialEvent.ID){
					player.sendMessage(Util.translate("&6[&cSteven Hilter&6] &aLook, I really don't think they flew 90 billion light years to come down here and start a fight. Get all rowdy."));
					player.sendRawMessage(Util.translate("&6New objective: &cGo to layer 150"));
				}
			}
			
			if(event==InvasionSpecialEvent.NONE){
				WorldInvasionHandler handler = new WorldInvasionHandler(pl, invasion);
				invasion.setTask(handler);
				handler.runTaskTimer(pl, 100, 10);
			}
			
			if(event==InvasionSpecialEvent.ID){
				//IndependenceDayEvent handler = new IndependenceDayEvent(pl, invasion);
				//invasion.setTask(handler);
				//handler.runTaskTimer(pl, 100, 10);
			}
			pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				public void run() {
					invasion.setState(InvasionState.PLAYING);
				}
			}, 100);
			
			for(Player player : world.getPlayers()){
				invasion.addPlayer(player.getUniqueId());
			}
		}
		
		return response;
	}
	
	public void stopInvasion(World world){
		if(pl.getWorldInvasions().containsKey(world.getUID())){
			pl.getWorldInvasions().get(world.getUID()).reset();
		}
	}
	
	public void stopInvasionNaturally(World world){
		if(pl.getWorldInvasions().containsKey(world.getUID())){
			WorldInvasion wI = pl.getWorldInvasions().get(world.getUID());
			StringBuilder builder = new StringBuilder();
			
			for(Player player : world.getPlayers()){
				builder.append(player.getName()+" ");
			}
			
			if(wI.getEvent()==InvasionSpecialEvent.ID){
				/*wI.sendRawMessage("&cYou defeat alien invasion with "+builder.toString().substring(0, builder.toString().length()-1));
				
				IndependenceDayEvent event = (IndependenceDayEvent) wI.getTask();
				
				for(Block block : event.blocks){
					block.setType(Material.AIR);
				}
				
				for(Player player : wI.getWorld().getPlayers()){
					player.playSound(player.getLocation(), "entity.generic.explode", 999999, 0);
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 255));
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 255));
					player.teleport(wI.getWorld().getHighestBlockAt(event.locations.get(player.getUniqueId())).getLocation());
					player.resetPlayerTime();
					event.h.remove();
					event.h1.remove();
					event.chair1.remove();
					event.chair2.remove();
					((LivingEntity) event.zombie1).damage(999999);
					((LivingEntity) event.zombie2).damage(999999);
				}*/
			}else{
				wI.sendRawMessage(pl.getString(StringConfig.DEFEATGROUP).replace("{CONTRIBUTORS}", builder.toString().substring(0, builder.toString().length()-1)));
			}
			
			stopInvasion(world);
		}
	}
}
