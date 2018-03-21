package xyz.rodeldev.invasion.invasion;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;
import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.ConfigNodes;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.effects.RandomFirework;
import xyz.rodeldev.invasion.mobs.InvasionMobs;
import xyz.rodeldev.invasion.utils.RandomHelper;
import xyz.rodeldev.invasion.utils.Util;

public class InvasionManager {
	public InvasionResponseData startInvasion(Location location, Main pl){
		InvasionResponse response = InvasionResponse.STARTED;
		boolean slotFind = false;
		int slot = 0;
		final Invasion invasion = new Invasion();
		
		for (int i = 0; i < 20; i++) {
			if(pl.getInvasions().get(i).getState()==InvasionState.EMPTY){
				slotFind = true;
				slot = i;
			}
		}
		
		if(pl.getWorldInvasions().containsKey(location.getWorld().getUID())){
			response=InvasionResponse.WORLDININVASION;
		}
		
		if(!slotFind)
			response=InvasionResponse.TOP;
		
		for(Entry<Integer, Invasion> near : pl.getInvasions().entrySet()){
			if(near.getValue().getState()!=InvasionState.EMPTY){
				if(near.getValue().getLocation().distance(location)<Main.range){
					response=InvasionResponse.NEAR;
				}
			}
		}
		
		if(pl.getConfig().getList(ConfigNodes.BLOCKEDWORLDS.getKey()).contains(location.getWorld().getName())){
			response=InvasionResponse.WORLD;
		}
		
		if(response==InvasionResponse.STARTED){
			ArmorStand armor = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
			armor.setGravity(false);
			armor.setCustomName(Util.translate("&2&lInvasionControl"));
			invasion.setPlugin(pl);
			invasion.setStand(armor);
			invasion.setState(InvasionState.STARTING);
			invasion.setRound(pl.getRounds().get(1));
			invasion.setSlot(slot);
			armor.remove();
			
			
			
			
			InvasionHandler handler = new InvasionHandler(pl, invasion);
			invasion.setTask(handler);
			pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				public void run() {
					invasion.setState(InvasionState.PLAYING);
				}
			}, 100);
			handler.runTaskTimer(pl, 100, 10);
			for(Entity entity : armor.getNearbyEntities(Main.range, Main.range, Main.range)){
				if(entity instanceof Player){
					invasion.addPlayer(((Player) entity).getUniqueId());
					
					pl.sendMessage((Player) entity, StringConfig.START);
				}
			}
			pl.getInvasions().put(slot, invasion);
		}
		
		final InvasionResponse finalResponse = response;
		
		return new InvasionResponseData() {
			
			@Override
			public InvasionResponse getInvasionResponse() {
				return finalResponse;
			}
			
			@Override
			public Invasion getInvasion() {
				return invasion;
			}
		};
	}
	
	public void stopInvasion(int invasionid, Main pl){
		pl.getInvasions().get(invasionid).reset();
	}
	
	public void stopInvasionNaturally(final int invasionid, final Main pl){
		final Invasion invasion = pl.getInvasions().get(invasionid);
		stopInvasion(invasionid, pl);
		for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
			Player player = pl.getServer().getPlayer(entry.getKey());
			player.playSound(player.getLocation(), "entity.enderdragon.death", 1, 0);
			RandomFirework.spawn(player.getLocation());
		}
		
		for (int i = 0; i < 10; i++) {
			pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				public void run() {
					for (int j = 0; j < 10; j++) {						
						int range = 30;
						int x = invasion.getLocation().getBlockX();
						int z = invasion.getLocation().getBlockZ();
						int randx = new RandomHelper().range(x - range, x + range);
						int randz = new RandomHelper().range(z - range, z + range);
						Location location = new Location(invasion.getLocation().getWorld(),randx, invasion.getLocation().getWorld().getHighestBlockYAt(randx, randz),randz);
						RandomFirework.spawn(location); 
					}
				}
			}, 20*i);
		}
		
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if(invasion.getPlayers().size()==1){
					StringBuilder builder = new StringBuilder();
					for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
						builder.append(Util.translate("&6&lTotal ("+entry.getValue().calculateTotal()+")\n"));
						for(InvasionMobs mobs : InvasionMobs.values()){
							if(InvasionMobs.values()[0]==mobs){
								builder.append(Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
							}else{
								builder.append("\n"+Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
							}
						}
						builder.append("\n");
						builder.append("\n"+Util.translate("&6&lBosses:"));
						
						for(String boss : entry.getValue().getBosses()){
							builder.append("\n"+Util.translate(boss));
						}
					}
					
					FancyMessage cmsg = new FancyMessage(pl.getString(StringConfig.DEFEAT).replace("{SECOUNDS}", invasion.getTime()+"")).color(ChatColor.RED).then(" ").then(pl.getString(StringConfig.HOVER)).color(ChatColor.GREEN).tooltip(builder.toString());
					for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
						cmsg.send(pl.getServer().getPlayer(entry.getKey()));
					}
				}else{
					FancyMessage cmsg = new FancyMessage(pl.getString(StringConfig.DEFEATGROUP).replace("{SECOUNDS}", invasion.getTime()+"")).color(ChatColor.GREEN).then(" ");
					int i = 0;
					for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
						++i;
						if(invasion.getPlayers().size()==i){
							StringBuilder builder = new StringBuilder();
							builder.append(Util.translate("&6&lTotal ("+entry.getValue().calculateTotal()+")\n"));
							for(InvasionMobs mobs : InvasionMobs.values()){
								if(InvasionMobs.values()[0]==mobs){
									builder.append(Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}else{
									builder.append("\n"+Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}
							}
							builder.append("\n");
							builder.append("\n"+Util.translate("&6&lBosses:"));
							
							for(String boss : entry.getValue().getBosses()){
								builder.append("\n"+Util.translate(boss));
							}
							
							if(pl.getServer().getPlayer(entry.getKey()).getName().equals("rodel77")){
								cmsg.then(pl.getString(StringConfig.AND)+" -DEV rodel77-").color(ChatColor.BLUE).tooltip(builder.toString());
							}else if(pl.getServer().getPlayer(entry.getKey()).isOp()){
								cmsg.then(pl.getString(StringConfig.AND)+" OP "+pl.getServer().getPlayer(entry.getKey()).getName()).color(ChatColor.RED).tooltip(builder.toString());
							}else{
								cmsg.then(pl.getString(StringConfig.AND)+" "+pl.getServer().getPlayer(entry.getKey()).getName()).color(ChatColor.RED).tooltip(builder.toString());
							}
							for(Entry<UUID, InvasionPlayer> entry2 : invasion.getPlayers().entrySet()){
								cmsg.send(pl.getServer().getPlayer(entry2.getKey()));
							}
						}else if(i==1){
							StringBuilder builder = new StringBuilder();
							builder.append(Util.translate("&6&lTotal ("+entry.getValue().calculateTotal()+")\n"));
							for(InvasionMobs mobs : InvasionMobs.values()){
								if(InvasionMobs.values()[0]==mobs){
									builder.append(Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}else{
									builder.append("\n"+Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}
							}
							builder.append("\n");
							builder.append("\n"+Util.translate("&6&lBosses:"));
							
							for(String boss : entry.getValue().getBosses()){
								builder.append("\n"+Util.translate(boss));
							}
							
							if(pl.getServer().getPlayer(entry.getKey()).getName().equals("rodel77")){
								cmsg.then("-DEV rodel77-, ").color(ChatColor.BLUE).tooltip(builder.toString());
							}else if(pl.getServer().getPlayer(entry.getKey()).isOp()){
								cmsg.then("OP "+pl.getServer().getPlayer(entry.getKey()).getName()+", ").color(ChatColor.RED).tooltip(builder.toString());
							}else{
								cmsg.then(pl.getServer().getPlayer(entry.getKey()).getName()+", ").color(ChatColor.RED).tooltip(builder.toString());
							}
						}else{
							StringBuilder builder = new StringBuilder();
							builder.append(Util.translate("&6&lTotal ("+entry.getValue().calculateTotal()+")\n"));
							for(InvasionMobs mobs : InvasionMobs.values()){
								if(InvasionMobs.values()[0]==mobs){
									builder.append(Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}else{
									builder.append("\n"+Util.translate(mobs.getName()+" ("+entry.getValue().getStats(mobs)+")"));
								}
							}
							builder.append("\n");
							builder.append("\n"+Util.translate("&6&lBosses:"));
							
							for(String boss : entry.getValue().getBosses()){
								builder.append("\n"+Util.translate(boss));
							}
							
							if(pl.getServer().getPlayer(entry.getKey()).getName().equals("rodel77")){
								cmsg.then(", -DEV rodel77-").color(ChatColor.BLUE).tooltip(builder.toString());
							}else if(pl.getServer().getPlayer(entry.getKey()).isOp()){
								cmsg.then(pl.getServer().getPlayer(", OP "+entry.getKey()).getName()).color(ChatColor.RED).tooltip(builder.toString());
							}else{
								cmsg.then(", "+pl.getServer().getPlayer(entry.getKey()).getName()).color(ChatColor.RED).tooltip(builder.toString());
							}
						}
					}
				}
				
				for (int j = 0; j < 50; j++) {
					int range = 30;
					int x = invasion.getLocation().getBlockX();
					int z = invasion.getLocation().getBlockZ();
					int randx = new RandomHelper().range(x - range, x + range);
					int randz = new RandomHelper().range(z - range, z + range);
					Location location = new Location(invasion.getLocation().getWorld(),randx, invasion.getLocation().getWorld().getHighestBlockYAt(randx, randz),randz);
					RandomFirework.spawn(location);
				}
			}
		}, 20*10);
	}
}
