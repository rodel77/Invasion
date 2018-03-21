package xyz.rodeldev.invasion.handlers;

import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.Round;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.effects.RandomFirework;
import xyz.rodeldev.invasion.invasion.Invasion;
import xyz.rodeldev.invasion.invasion.InvasionHelper;
import xyz.rodeldev.invasion.invasion.InvasionManager;
import xyz.rodeldev.invasion.invasion.InvasionPlayer;
import xyz.rodeldev.invasion.invasion.InvasionState;
import xyz.rodeldev.invasion.mobs.InvasionMobsController;
import xyz.rodeldev.invasion.utils.InvasionUpdatedData;
import xyz.rodeldev.invasion.utils.Util;
import xyz.rodeldev.invasion.worldinvasion.WorldInvasion;

public class ListenerHandler implements Listener{
	
	Main pl;
	
	public ListenerHandler(Main pl){
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e){
		if(InvasionHelper.playerIsInvasion(e.getEntity())){
		}
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		if(e.getPlayer().isOp()){
			e.getPlayer().sendMessage(Util.translate("&b&l[&2&lInvasion - MOTD &c&l(News)&b&l] &2&l"+InvasionUpdatedData.getJsonValue("motd", true)));
			if(!InvasionUpdatedData.getJsonValue("version", true).equals(pl.VERSION)){
				e.getPlayer().sendMessage(Util.translate("&b&l[&2&lInvasion - MOTD &c&l(News)&b&l] &2&lVersion &c&l"+InvasionUpdatedData.getJsonValue("version", true)+" ("+InvasionUpdatedData.getJsonValue("versionname", true)+"&c&l)&2&l avaible!"));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e){
		if(pl.addons.contains(e.getBlock())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent e){
		try {
			if(new InvasionMobsController(pl).isInvasionMob(e.getEntity())){
				if(new Random().nextInt(20)==1){
					if(pl.prizesL.size()!=0){
						e.getDrops().clear();
						e.getDrops().add(pl.prizesL.get(new Random().nextInt(pl.prizesL.size())));
						for (int i = 0; i < 5; i++) {
							RandomFirework.spawn(e.getEntity().getLocation());
						}
					}
				}
				
				for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
					if(invasion.getValue().getActivePlayers().containsKey(e.getEntity().getKiller().getUniqueId()) && invasion.getValue().getActivePlayers().get(e.getEntity().getKiller().getUniqueId()).isInInvasion()){
						invasion.getValue().incrementKill();
						if(invasion.getValue().getActivePlayers().containsKey(e.getEntity().getKiller().getUniqueId())){
							invasion.getValue().getActivePlayers().get(e.getEntity().getKiller().getUniqueId()).incrementStat(pl.getMobs().convertMob(e.getEntity()));
						}
					}
				}
				
				for(Entry<UUID, WorldInvasion> wInvasion : pl.getWorldInvasions().entrySet()){
					wInvasion.getValue().incrementDefeated();
					if(wInvasion.getValue().getActivePlayers().containsKey(e.getEntity().getKiller().getUniqueId()) && wInvasion.getValue().getActivePlayers().get(e.getEntity().getKiller().getUniqueId()).isInInvasion()){
						wInvasion.getValue().getActivePlayers().get(e.getEntity().getKiller().getUniqueId()).incrementStat(pl.getMobs().convertMob(e.getEntity()));
					}
				}
			}
			
			for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
				if(invasion.getValue().getPlayers().containsKey(e.getEntity().getKiller().getUniqueId()) && invasion.getValue().getPlayers().get(e.getEntity().getKiller().getUniqueId()).isInInvasion()){
					Invasion inv = invasion.getValue();
					if(inv.getState()== InvasionState.BOSS && inv.getRound().getBossT()==e.getEntityType() && e.getEntity().getUniqueId()==inv.getBoss().getUniqueId()){
						for (Entry<UUID, InvasionPlayer> player : invasion.getValue().getPlayers().entrySet()) {
							if(player.getKey()==e.getEntity().getKiller().getUniqueId()){
								inv.sendRawMessage(pl.getString(StringConfig.KILLBOSS).replace("{PLAYER}", e.getEntity().getKiller().getName()).replace("{BOSS}", e.getEntity().getCustomName()!=null ? e.getEntity().getCustomName() : e.getEntity().getType().toString()));
								player.getValue().addBoss(e.getEntity().getCustomName());
							}
						}
						
						if(inv.getRound().getRound()==pl.getRounds().size()){
							new InvasionManager().stopInvasionNaturally(inv.getSlot(), pl);
						}else{
							if(pl.getRounds().get(inv.getRound().getRound()+1).isBoss()){
								inv.setRound(pl.getRounds().get(inv.getRound().getRound()+1));
								inv.setKills(0);
								boss(inv);
							}else{
								inv.setRound(pl.getRounds().get(inv.getRound().getRound()+1));
								inv.setKills(0);
								inv.setState(InvasionState.PLAYING);
							}
						}
					}
				}
			}
		} catch (Exception e2) {}
		
	}
	
	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent e){
		if(e.getEntity() instanceof TNTPrimed && e.getEntity().getCustomName()!=null && e.getEntity().getCustomName().equals(Util.translate("&2&lKABOM!"))){
			e.setCancelled(true);
		}
		
		if(pl.getMobs().isInvasionMob(e.getEntity()) && e.getEntity() instanceof Creeper){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e){
		/*if(pl.getWorldInvasions().containsKey(e.getPlayer().getWorld().getUID()) && pl.getWorldInvasions().get(e.getPlayer().getWorld().getUID()).getEvent()==InvasionSpecialEvent.ID && ((IndependenceDayEvent) pl.getWorldInvasions().get(e.getPlayer().getWorld().getUID()).getTask()).mission!=0){
			e.setCancelled(true);
		}*/
	}
	
	private void boss(Invasion invasion){
		Round round = invasion.getRound();
		Entity entity = invasion.getLocation().getWorld().spawnEntity(invasion.getLocation(), round.getBossT());
		LivingEntity le = (LivingEntity) entity;
		le.setMaxHealth(round.getBoosLive());
		le.setHealth(round.getBoosLive());
	
		invasion.setState(InvasionState.BOSS);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntity().getCustomName()!=null && pl.getBosses().contains(e.getEntity().getCustomName())){
			if(e.getCause()!=DamageCause.ENTITY_ATTACK && e.getCause()!=DamageCause.PROJECTILE){
				e.setCancelled(true);
			}
		}
		
		if(pl.getMobs().isInvasionMob(e.getEntity()) && e.getCause()==DamageCause.FIRE_TICK){
			e.getEntity().setFireTicks(0);
			e.setCancelled(true);
		}
	}
}
