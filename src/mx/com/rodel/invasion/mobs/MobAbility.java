package mx.com.rodel.invasion.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.effects.ParticleEffects;
import mx.com.rodel.invasion.invasion.Invasion;
import mx.com.rodel.invasion.invasion.InvasionHandler;
import mx.com.rodel.invasion.invasion.InvasionPlayer;
import mx.com.rodel.invasion.utils.Util;
import mx.com.rodel.invasion.worldinvasion.WorldInvasion;
import mx.com.rodel.invasion.worldinvasion.WorldInvasionHandler;

public class MobAbility {
	public static void mobAbility(Entity entity, Main pl, Invasion invasion, InvasionHandler handler){
		if(pl.getMobs().isInvasionMob(entity)){
			caller(pl.getMobs().convertMob(entity), invasion, pl, (Creature) entity, handler);
		}
	}
	
	public static void WmobAbility(Entity entity, Main pl, WorldInvasion invasion, WorldInvasionHandler handler){
		if(pl.getMobs().isInvasionMob(entity)){
			Wcaller(pl.getMobs().convertMob(entity), invasion, pl, (Creature) entity, handler);
		}
	}
	
	private static void caller(InvasionMobs mob, Invasion invasion, Main pl, Creature entity, InvasionHandler handler){
		List<UUID> list = new ArrayList<UUID>();
		
		for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
			list.add(entry.getKey());
		}
		
		if(mob.hasTargetAbility() && entity.getTarget()==null){
			entity.setTarget(pl.getServer().getPlayer(list.get(new Random().nextInt(list.size()))));
		}
		
		if(mob.hasTargetAbility() && entity.getTarget()!=null && entity.getLocation().distance(entity.getTarget().getLocation())<=10 && !handler.cooldowns().contains(entity.getUniqueId())){
			abilityCaller(mob, invasion, pl, entity, handler);
		}
		
		if(mob==InvasionMobs.SUMMONER){
			for(Player player : pl.getServer().getOnlinePlayers()){
				if(player.getLocation().distance(entity.getLocation())<=20 && new Random().nextDouble()*100<1){
					summonerAbility(invasion, pl, entity, handler, player);
				}
			}
		}
	}
	
	private static void Wcaller(InvasionMobs mob, WorldInvasion invasion, Main pl, Creature entity, WorldInvasionHandler handler){
		List<UUID> list = new ArrayList<UUID>();
		
		for(Entry<UUID, InvasionPlayer> entry : invasion.getPlayers().entrySet()){
			list.add(entry.getKey());
		}
		
		if(mob.hasTargetAbility() && entity.getTarget()==null){
			entity.setTarget(pl.getServer().getPlayer(list.get(new Random().nextInt(list.size()))));
		}
		
		if(mob.hasTargetAbility() && entity.getTarget()!=null && entity.getLocation().distance(entity.getTarget().getLocation())<=10 && !handler.cooldowns().contains(entity.getUniqueId())){
			WabilityCaller(mob, invasion, pl, entity, handler);
		}
		
		if(mob==InvasionMobs.SUMMONER){
			for(Player player : pl.getServer().getOnlinePlayers()){
				if(player.getLocation().distance(entity.getLocation())<=20 && new Random().nextDouble()*100<1){
					WsummonerAbility(invasion, pl, entity, handler, player);
				}
			}
		}
	}
	
	private static void WsummonerAbility(WorldInvasion invasion, Main pl, final Creature creature, WorldInvasionHandler handler, final Player player){
		Location loc = creature.getLocation();

		final Fireball fireball = (Fireball) creature.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY()+4, loc.getZ()), EntityType.FIREBALL);
		fireball.setYield(0);
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
			  Location location = player.getLocation();
			  if (location.distance(fireball.getLocation()) > 1) {
			   int speed = 1;
			   Vector dir = location.toVector().subtract(fireball.getLocation().toVector()).normalize();
			   fireball.setVelocity(dir.multiply(speed));
			  } else {
			   this.cancel();
			  }
			}
			};
			runnable.runTaskTimer(pl, 0, 3);
	}
	
	private static void WabilityCaller(InvasionMobs mob, WorldInvasion invasion, Main pl, final Creature creature, WorldInvasionHandler handler){
		if(creature.getTarget() instanceof Player){
			final Player player = (Player) creature.getTarget(); 
			if(mob==InvasionMobs.CREEPER && !creature.isDead()){
				throwProjectile(creature, EntityType.PRIMED_TNT);
				for (int l = 0; l < 10; l++) {
					pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
						public void run() {
							try {
								player.playSound(creature.getTarget().getLocation(), "entity.tnt.primed", 1, 1);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							} catch (Exception e) {}
						}
					}, 1*l);
				}
			}else if(mob==InvasionMobs.SPIDER){
				creature.damage(creature.getMaxHealth()/2);
				final Location location = creature.getTarget().getLocation();
				final int x = location.getBlockX();
				final int y = location.getBlockY();
				final int z = location.getBlockZ();
				location.getBlock().setType(Material.WEB);
				location.getWorld().getBlockAt(x, y+1, z).setType(Material.WEB);
				location.getWorld().getBlockAt(x, y, z-1).setType(Material.WEB);
				location.getWorld().getBlockAt(x, y, z+1).setType(Material.WEB);
				location.getWorld().getBlockAt(x+1, y, z).setType(Material.WEB);
				location.getWorld().getBlockAt(x-1, y, z).setType(Material.WEB);
				player.playSound(location, "block.anvil.destroy", 1, 1);
				for (int l = 0; l < 10; l++) {
					pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
						public void run() {
							player.playSound(location, "entity.tnt.primed", 1, 1);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
						}
					}, 1*l);
				}
				
				pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
					public void run() {
						location.getBlock().setType(Material.AIR);
						location.getWorld().getBlockAt(x, y+1, z).setType(Material.AIR);
						location.getWorld().getBlockAt(x, y, z-1).setType(Material.AIR);
						location.getWorld().getBlockAt(x, y, z+1).setType(Material.AIR);
						location.getWorld().getBlockAt(x+1, y, z).setType(Material.AIR);
						location.getWorld().getBlockAt(x-1, y, z).setType(Material.AIR);
					}
				}, 20);
			}
			
			handler.registerAbility(creature.getUniqueId(), mob);
		}
	}
	
	private static void summonerAbility(Invasion invasion, Main pl, final Creature creature, InvasionHandler handler, final Player player){
		Location loc = creature.getLocation();

		final Fireball fireball = (Fireball) creature.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY()+4, loc.getZ()), EntityType.FIREBALL);
		fireball.setYield(0);
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
			  Location location = player.getLocation();
			  if (location.distance(fireball.getLocation()) > 1) {
			   int speed = 1;
			   Vector dir = location.toVector().subtract(fireball.getLocation().toVector()).normalize();
			   fireball.setVelocity(dir.multiply(speed));
			  } else {
			   this.cancel();
			  }
			}
			};
			runnable.runTaskTimer(pl, 0, 3);
	}
	
	private static void abilityCaller(InvasionMobs mob, Invasion invasion, Main pl, final Creature creature, InvasionHandler handler){
		if(creature.getTarget() instanceof Player){
			final Player player = (Player) creature.getTarget(); 
			if(mob==InvasionMobs.CREEPER && !creature.isDead()){
				throwProjectile(creature, EntityType.PRIMED_TNT);
				for (int l = 0; l < 10; l++) {
					pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
						public void run() {
							try {
								player.playSound(creature.getTarget().getLocation(), "entity.tnt.primed", 1, 1);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
								ParticleEffects.SMOKE_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							} catch (Exception e) {}
						}
					}, 1*l);
				}
			}else if(mob==InvasionMobs.SPIDER){
				creature.damage(creature.getMaxHealth()/2);
				final Location location = creature.getTarget().getLocation();
				final int x = location.getBlockX();
				final int y = location.getBlockY();
				final int z = location.getBlockZ();
				location.getBlock().setType(Material.WEB);
				location.getWorld().getBlockAt(x, y+1, z).setType(Material.WEB);
				location.getWorld().getBlockAt(x, y, z-1).setType(Material.WEB);
				location.getWorld().getBlockAt(x, y, z+1).setType(Material.WEB);
				location.getWorld().getBlockAt(x+1, y, z).setType(Material.WEB);
				location.getWorld().getBlockAt(x-1, y, z).setType(Material.WEB);
				player.playSound(location, "block.anvil.destroy", 1, 1);
				for (int l = 0; l < 10; l++) {
					pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
						public void run() {
							player.playSound(location, "entity.tnt.primed", 1, 1);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
							ParticleEffects.EXPLOSION_NORMAL.display(creature.getEyeLocation().getDirection(), 1, creature.getLocation(), 999);
						}
					}, 1*l);
				}
				
				pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
					public void run() {
						location.getBlock().setType(Material.AIR);
						location.getWorld().getBlockAt(x, y+1, z).setType(Material.AIR);
						location.getWorld().getBlockAt(x, y, z-1).setType(Material.AIR);
						location.getWorld().getBlockAt(x, y, z+1).setType(Material.AIR);
						location.getWorld().getBlockAt(x+1, y, z).setType(Material.AIR);
						location.getWorld().getBlockAt(x-1, y, z).setType(Material.AIR);
					}
				}, 40);
			}
			
			handler.registerAbility(creature.getUniqueId(), mob);
		}
	}
	
	private static void throwProjectile(Entity shooter, EntityType type){
		if(type==EntityType.PRIMED_TNT){
			Entity e = shooter.getWorld().spawnEntity(shooter.getLocation(), type);
			e.setVelocity(((LivingEntity) shooter).getEyeLocation().getDirection());
			e.setCustomName(Util.translate("&2&lKABOM!"));
			e.setCustomNameVisible(true);
		}
	}
}
