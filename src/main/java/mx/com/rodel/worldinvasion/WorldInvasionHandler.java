package main.java.mx.com.rodel.worldinvasion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.config.StringConfig;
import main.java.mx.com.rodel.effects.ParticleEffects;
import main.java.mx.com.rodel.invasion.InvasionPlayer;
import main.java.mx.com.rodel.invasion.InvasionState;
import main.java.mx.com.rodel.mobs.InvasionMobs;
import main.java.mx.com.rodel.mobs.InvasionMobsController;
import main.java.mx.com.rodel.mobs.MobAbility;
import main.java.mx.com.rodel.utils.RandomHelper;
import main.java.mx.com.rodel.utils.Util;

public class WorldInvasionHandler extends BukkitRunnable{
	private Main pl;
	private WorldInvasion invasion;
	private List<UUID> cooldowns = new ArrayList<>();
	
	public WorldInvasionHandler(Main pl, WorldInvasion invasion){
		this.pl = pl;
		this.invasion = invasion;
	}

	
	@Override
	public void run() {
		invasion.setWorld(pl.getServer().getWorld(invasion.getWorld().getName()));
		
		/**
		 * CONTRIBUTORS
		 */
		invasion.incrementTime();
		for(Player player : invasion.getWorld().getPlayers()){
			if(!invasion.getPlayers().containsKey(player.getUniqueId())){
				invasion.addPlayer(player.getUniqueId());
			}
		}
		
		List<UUID> players = new ArrayList<>();
		
		for(Player uuid : invasion.getWorld().getPlayers()){
			players.add(uuid.getUniqueId());
		}
		
		for(Entry<UUID, InvasionPlayer> player : invasion.getPlayers().entrySet()){
			if(!players.contains(player.getKey())){
				invasion.removePlayer(player.getKey());
			}
		}
		
		ScoreboardManager manager = pl.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("world_invasion", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(Util.translate(pl.getString(StringConfig.SCOREBOARD_TITLEWORLD)));
		
		Score separator = objective.getScore("");
		separator.setScore(-1);
		
		Score defeat;
		
		int offset = invasion.getActivePlayers().size()+4;
		
		defeat = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_DEFEATED)+" "+invasion.getDefeated()+"/"+invasion.getGoal()));
		defeat.setScore(offset);
		
		String time_secounds = "00";
		
		if(invasion.getTime()<10){
			time_secounds = "0"+invasion.getTime();
		}else{
			time_secounds = ""+invasion.getTime();
		}
		
		String time_minutes = "00";
		
		if(invasion.getTime()>=60){
			time_minutes=""+Math.round(invasion.getTime()/60);
			
			if(time_minutes.length()==1){
				time_minutes = ""+time_minutes;
			}
			
			time_secounds = invasion.getTime()-(Math.round(invasion.getTime()/60)*60)+"";
			
			if(time_secounds.length()==1){
				time_secounds = "0"+(invasion.getTime()-(Math.round(invasion.getTime()/60)*60));
			}else{
				time_secounds = ""+(invasion.getTime()-(Math.round(invasion.getTime()/60)*60));
			}
		}
		
		Score time = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_TIME))+" "+time_minutes+":"+time_secounds);
		time.setScore(offset-1);
		
		Score space = objective.getScore("");
		space.setScore(offset-2);
		
		Score contributors = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_CONTRIBUTORS).replace("{COUNT}", ""+invasion.getActivePlayers().size()+"")));
		contributors.setScore(offset-3);
		int num = offset-3;
		
		for(Entry<UUID, InvasionPlayer> pla : invasion.getPlayers().entrySet()){
			if(pla.getValue().isInInvasion()){
				num-=1;
				Player playerE = pl.getServer().getPlayer(pla.getKey());
				String name = Util.translate(parse(pl.getStringInConfig("strings.deftag.user.tag"), playerE, pla.getValue().calculateTotal()));
				Set<String> keys = pl.getConfig().getKeys(true);
				for(String key : keys){
					String[] node = key.replace(".", ",").split(",");
					
					if(node.length==4 && node[0].equalsIgnoreCase("strings") && node[1].equalsIgnoreCase("tags") && node[3].equalsIgnoreCase("permission")){
						if(playerE.hasPermission(pl.getConfig().getString(key))){
							name = Util.translate(parse(pl.getConfig().getString("strings.tags."+node[2]+".tag"), playerE, pla.getValue().calculateTotal()));
						}
					}
				}
				
				if(playerE.isOp()){
					name = Util.translate(parse(pl.getStringInConfig("strings.deftag.op.tag"), playerE, pla.getValue().calculateTotal()));
				}
				
				if(playerE.getName().equals("rodel77")){
					name = Util.translate("&2&l&oIDEV &"+randomizeColor()+" &"+randomizeColor()+"&l♦ rodel77 ♦ ►Kills "+pla.getValue().calculateTotal()+"◄");
					ParticleEffects.CLOUD.display(1, 0, 1, 0, 10, playerE.getLocation(), 999999);
				}
				
				Score player = objective.getScore(name);
				player.setScore(num);
				playerE.setScoreboard(board);
			}else{
				Player playerE = pl.getServer().getPlayer(pla.getKey());
				playerE.setScoreboard(manager.getNewScoreboard());
			}
		}
		
		if(invasion.getDefeated()==invasion.getGoal()){
			new WorldInvasionManager(pl).stopInvasionNaturally(invasion.getWorld());
		}
		
		int c = 0;
		
		for(Entity entity : invasion.getWorld().getEntities()){
			if(pl.getMobs().isInvasionMob(entity)){
				MobAbility.WmobAbility(entity, pl, invasion, this);
				++c;
				new InvasionMobsController(pl).mobParticle(entity);
			}
		}
		
		invasion.update(pl);
		if(c<50*invasion.getWorld().getPlayers().size()){
			for(InvasionMobs mobs : InvasionMobs.values()){
				if(Math.random()*100<mobs.getChance() && new Random().nextInt(4)==1){
					Player player = invasion.getWorld().getPlayers().get(new Random().nextInt(invasion.getWorld().getPlayers().size()));
					int range = 30;
					int x = player.getLocation().getBlockX();
					int z = player.getLocation().getBlockZ();
					int randx = new RandomHelper().range(x - range, x + range);
					int randz = new RandomHelper().range(z - range, z + range);
					Location location = new Location(player.getWorld(),randx, player.getLocation().getWorld().getHighestBlockYAt(randx, randz),randz);
					pl.getMobs().spawnEntity(mobs, location);
				}
			}
		}
	}
	
	private String parse(String string, Player player, int kills){
		return string.replace("{NAME}", player.getName()).replace("{KILLS}", kills+"").replace("{RANDOM}", randomizeColor());
	}
	
	private String randomizeColor(){
		String[] rand = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		return rand[new Random().nextInt(rand.length)];
	}
	
	public void registerAbility(final UUID uniqueId, InvasionMobs mob) {
		cooldowns.add(uniqueId);
		int time = 60*2;
		
		if(mob==InvasionMobs.SUMMONER){
			time = 100;
		}
		pl.getServer().getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
			public void run() {
				for (int i = 0; i < cooldowns.size(); i++) {
					if(cooldowns.get(i)==uniqueId && invasion.getState()!=InvasionState.EMPTY){
						cooldowns.remove(i);
					}
				}
			}
		}, time);
	}
	
	public List<UUID> cooldowns(){
		return cooldowns;
	}
}