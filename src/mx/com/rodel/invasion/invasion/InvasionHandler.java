package mx.com.rodel.invasion.invasion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.Round;
import mx.com.rodel.invasion.boss.Ability;
import mx.com.rodel.invasion.boss.AbilityExecutor;
import mx.com.rodel.invasion.config.StringConfig;
import mx.com.rodel.invasion.effects.ParticleEffects;
import mx.com.rodel.invasion.effects.ReflectionUtils.PackageType;
import mx.com.rodel.invasion.mobs.InvasionMobs;
import mx.com.rodel.invasion.mobs.InvasionMobsController;
import mx.com.rodel.invasion.mobs.MobAbility;
import mx.com.rodel.invasion.utils.RandomHelper;
import mx.com.rodel.invasion.utils.Util;

public class InvasionHandler extends BukkitRunnable{
	Main pl;
	Invasion invasion;
	List<UUID> cooldowns = new ArrayList<UUID>();
	
	public InvasionHandler(Main pl, Invasion invasion){
		this.pl = pl;
		this.invasion = invasion;
	}
	
	boolean readyForDelete = false;
	
	@Override
	public void run() {
		/**
		 * CONTRIBUTORS CONTROLER
		 */
		for (Entity entity : invasion.getStand().getNearbyEntities(Main.range, Main.range, Main.range)) {
			if(entity instanceof Player){
				Player player = (Player) entity;
				if(!invasion.players().containsKey(player.getUniqueId()) || invasion.players().containsKey(player.getUniqueId()) && !invasion.players().get(player.getUniqueId()).isInInvasion()){
					invasion.addPlayer(player.getUniqueId());
				}
			}
		}
		for(Entry<UUID, InvasionPlayer> players : invasion.players().entrySet()){
			
			boolean find = pl.getServer().getPlayer(players.getKey())!=null;
			
			
			if(!find && invasion.getActivePlayers().containsKey(players.getKey())){
				invasion.removePlayer(players.getKey());
			}
			
			if(find && pl.getServer().getPlayer(players.getKey()).getLocation().distance(invasion.getLocation())>Main.range && invasion.players().get(players.getKey()).isInInvasion()){
				invasion.removePlayer(players.getKey());
			}
		}
		
		if(invasion.getActivePlayers().isEmpty() && !readyForDelete){
			pl.getServer().broadcastMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(StringConfig.DISAPPEAR).replace("{X}", invasion.getLocation().getBlockX()+"").replace("{Y}", invasion.getLocation().getBlockY()+"").replace("{Z}", invasion.getLocation().getBlockZ()+"").replace("{SECONDS}", "60")));
			disappear();
			readyForDelete=true;
		}

		ScoreboardManager manager = pl.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("invasion", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(Util.translate(pl.getString(StringConfig.SCOREBOARD_TITLE)));
		
		int offset = invasion.getActivePlayers().size()+6;
		
		Score defeat;
		
		if(invasion.getState()==InvasionState.BOSS){
			defeat = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_BOSSLIVE)+" "+Math.round(invasion.getBoss().getHealth())+"/"+Math.round(invasion.getBoss().getMaxHealth())));
		}else{
			defeat = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_DEFEATED)+" "+invasion.getKills()+"/"+invasion.getRound().getGoal()));
		}
		
		defeat.setScore(offset);
		
		Score round = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_ROUND))+" "+invasion.getRound().getRound()+"/"+pl.getRounds().size());
		round.setScore(offset-1);
		
		
		
		Score time = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_TIME))+" "+invasion.getTime());
		time.setScore(offset-3);

		Score separator = objective.getScore("");
		separator.setScore(offset-4);
		
		Score contributors = objective.getScore(Util.translate(pl.getString(StringConfig.SCOREBOARD_CONTRIBUTORS).replace("{COUNT}", "("+invasion.getActivePlayers().size()+")")));
		contributors.setScore(offset-5);
		
		int num = offset-5;
		for (Entry<UUID, InvasionPlayer> players : invasion.getPlayers().entrySet()) {
			if(players.getValue().isInInvasion()){
				num-=1;
				Player playerE = pl.getServer().getPlayer(players.getKey());
				String name = playerE.getDisplayName()+" "+players.getValue().calculateTotal()+" kills";
				
				if(playerE.getName().equals("rodel77")){
					name = Util.translate("&2&l&oIDEV &"+randomizeColor()+" &"+randomizeColor()+"&l rodel77 "+players.getValue().calculateTotal()+" kills");
					ParticleEffects.CLOUD.display(1, 0, 1, 0, 10, playerE.getLocation(), 999999);
				}
				
				if(playerE.getDisplayName().length()>=25){
					name = playerE.getDisplayName().substring(0, 25)+"... "+players.getValue().calculateTotal()+" kills";
				}
				
				Score player = objective.getScore(name);
				player.setScore(num);
				playerE.setScoreboard(board);
			}else{
				if(pl.getServer().getPlayer(players.getKey())!=null){
					Player playerE = pl.getServer().getPlayer(players.getKey());
					playerE.setScoreboard(manager.getNewScoreboard());
				}
			}
		}

		/**
		 * ROUNDS
		 */
		try {
			if(invasion.getState()==InvasionState.PLAYING && invasion.getKills()>=invasion.getRound().getGoal()){
				if(invasion.getRound().getRound()==pl.getRounds().size()){
					new InvasionManager().stopInvasionNaturally(invasion.getSlot(), pl);
				}else{
					if(pl.getRounds().get(invasion.getRound().getRound()+1).isBoss()){
						invasion.changeRound();
						boss();
					}else{
						invasion.changeRound();
						invasion.setState(InvasionState.PLAYING);
					}
				}
			} else if(invasion.getState()==InvasionState.BOSS && invasion.getBoss().isDead() && invasion.getBoss().getClass().isInstance(PackageType.CRAFTBUKKIT_ENTITY.getClass("CraftEntity"))){
				if(invasion.getRound().getRound()==pl.getRounds().size()){
					new InvasionManager().stopInvasionNaturally(invasion.getSlot(), pl);
				}else{
					invasion.sendRawMessage(pl.getString(StringConfig.KILLBOSS).replace("{PLAYER}", invasion.getBoss().getKiller().getName()).replace("{BOSS}", invasion.getBoss().getCustomName()));
					invasion.getPlayers().get(invasion.getBoss().getKiller().getUniqueId()).addBoss(invasion.getBoss().getCustomName());
					
					if(pl.getRounds().get(invasion.getRound().getRound()+1).isBoss()){
						invasion.changeRound();
						boss();
					}else{
						invasion.changeRound();
						invasion.setState(InvasionState.PLAYING);
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		int c = 0;
		try {
			for(Entity entity : invasion.getStand().getNearbyEntities(Main.range, Main.range, Main.range)){
				if(pl.getMobs().isInvasionMob(entity)){
					MobAbility.mobAbility(entity, pl, invasion, this);
					++c;
					new InvasionMobsController(pl).mobParticle(entity);
				}
			}	
		} catch (Exception e) {
			
		}
		
		if(invasion.getState()==InvasionState.BOSS){
			if(invasion.getBoss().getLocation().distance(invasion.getStand().getLocation())>40){
				invasion.getBoss().teleport(invasion.getStand());
			}
			
			invasion.getBoss().setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));
			
			if(pl.getAbilities().size()!=0 && new Random().nextInt(15)==1){
				Ability ability = pl.getAbilities().get(new Random().nextInt(pl.getAbilities().size()));
				new AbilityExecutor(pl).execute(ability.getActions(), invasion);
			}
		}
		
		invasion.update(pl);
		if(c<60){
			for(InvasionMobs mobs : InvasionMobs.values()){
				if(Math.random()*100<invasion.getRound().getProbability(mobs) && new Random().nextInt(3)==1){
					int range = 30;
					int x = invasion.getLocation().getBlockX();
					int z = invasion.getLocation().getBlockZ();
					int randx = new RandomHelper().range(x - range, x + range);
					int randz = new RandomHelper().range(z - range, z + range);
					Location location = new Location(invasion.getLocation().getWorld(),randx, invasion.getLocation().getWorld().getHighestBlockYAt(randx, randz),randz);
					pl.getMobs().spawnEntity(mobs, location);
				}
			}
		}
	}
	
	private void disappear(){
		
		BukkitRunnable btask = new BukkitRunnable() {
			int init_time = 60;
			@Override
			public void run() {
				--init_time;
				if((init_time+"").endsWith("0")){
					pl.getServer().broadcastMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(StringConfig.DISAPPEAR).replace("{X}", invasion.getLocation().getBlockX()+"").replace("{Y}", invasion.getLocation().getBlockY()+"").replace("{Z}", invasion.getLocation().getBlockZ()+"").replace("{SECONDS}", init_time+"")));
					
				}
				
				if(init_time==0){
					new InvasionManager().stopInvasion(invasion.getSlot(), pl);
					cancel();
				}
				
				if(!invasion.getActivePlayers().isEmpty()){
					readyForDelete=false;
					cancel();
				}
			}
		};
		
		btask.runTaskTimer(pl, 0, 20);
	}
	
	private String randomizeColor(){
		String[] rand = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		return rand[new Random().nextInt(rand.length)];
	}

	private void boss(){
		Round round = invasion.getRound();
		
		if(round.getBossT()==EntityType.SLIME){
			 Slime entity = (Slime) invasion.getLocation().getWorld().spawnEntity(invasion.getLocation(), round.getBossT());
			 entity.setSize(3);
			 LivingEntity le = (LivingEntity) entity;
				le.setMaxHealth(round.getBoosLive());
				le.setHealth(round.getBoosLive());
				le.setCustomName(Util.translate(round.getBossName()));
				le.setCustomNameVisible(true);
				invasion.setBoss(le);
				invasion.setState(InvasionState.BOSS);
		}else{
			Entity entity = invasion.getLocation().getWorld().spawnEntity(invasion.getLocation(), round.getBossT());
			 LivingEntity le = (LivingEntity) entity;
				le.setMaxHealth(round.getBoosLive());
				le.setHealth(round.getBoosLive());
				le.setCustomName(Util.translate(round.getBossName()));
				le.setCustomNameVisible(true);
				invasion.setBoss(le);
				invasion.setState(InvasionState.BOSS);
		}
		
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
