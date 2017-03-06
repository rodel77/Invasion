package mx.com.rodel.invasion.boss;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.invasion.Invasion;
import mx.com.rodel.invasion.invasion.InvasionPlayer;

public class AbilityExecutor {
	Main pl;
	
	public AbilityExecutor(Main pl){
		this.pl = pl;
	}
	
	public void execute(List<AbilityAction> action, final Invasion invasion){
		final List<AbilityAction> queue = action;
		
		queue.add(new AbilityAction(AbilityActions.END, new Object[]{}));
		
		BukkitRunnable task = new BukkitRunnable() {
			boolean waiting = false;
			int currentint = 0;
			
			public void run() {
				if(waiting)
					waiting=false;
				
				if(!waiting){
					for (int i = currentint; i < queue.size(); i++) {
						if(!waiting){
							currentint=i;
							if(queue.get(i).getAction()!=AbilityActions.WAIT){
								execute(queue.get(i), invasion);
							}else{
								waiting = true;
								currentint=i+1;
							}
						}
					}
				}
				
				if(currentint==queue.size()-1){
					this.cancel();
				}
				
			}
		};
		task.runTaskTimer(pl, 0, 20);
	}
	
	@SuppressWarnings("deprecation")
	public void execute(AbilityAction action, Invasion invasion){
		Object[] args = action.getArguments();
		switch (action.getAction()) {
			case MESSAGE:
				invasion.sendRawMessage(args[0].toString());
				break;
			case DAMAGE:
				for(Entry<UUID, InvasionPlayer> player : invasion.getPlayers().entrySet()){
					if(player.getValue().isInInvasion()){
						pl.getServer().getPlayer(player.getKey()).damage((int) args[0]);
					}
				}
				break;
			case MOVE:
				invasion.getBoss().setVelocity(new Vector(Double.parseDouble(args[0].toString()), Double.parseDouble(args[1].toString()), Double.parseDouble(args[2].toString())));
				break;
			case SUMMON:
				for (int i = 0; i < Integer.parseInt(args[1].toString()); i++) {
					invasion.getBoss().getWorld().spawnEntity(invasion.getBoss().getLocation(), EntityType.fromId((int) args[0])).setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));;
				}
				break;
			case SUMMON_EQUIPED:
				for (int i = 0; i < Integer.parseInt(args[1].toString()); i++) {
					Entity entity = invasion.getBoss().getWorld().spawnEntity(invasion.getBoss().getLocation(), EntityType.fromId((int) args[0]));
					entity.setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));
					EntityEquipment equip = ((LivingEntity) entity).getEquipment();
					equip.setHelmet(new ItemStack((int) args[2]));
					equip.setChestplate(new ItemStack((int) args[3]));
					equip.setLeggings(new ItemStack((int) args[4]));
					equip.setBoots(new ItemStack((int) args[5]));
				}
				break;
			case SOUND:
				for(Entry<UUID, InvasionPlayer> p : invasion.getPlayers().entrySet()){
					Player player = pl.getServer().getPlayer(p.getKey());
					player.playSound(player.getLocation(), args[0].toString(), 1, Float.parseFloat(args[1].toString()));
				}
				break;
			case TELEPORT:
				if(args[0].toString().equalsIgnoreCase("center")){
					invasion.getBoss().teleport(invasion.getLocation());
				}else if(args[0].toString().equalsIgnoreCase("player")){
					invasion.getBoss().teleport(pl.getServer().getPlayer(UUID.fromString(invasion.getActivePlayers().keySet().toArray()[new Random().nextInt(invasion.getActivePlayers().keySet().toArray().length)].toString())));
				}
				break;
			case HEAL:
				double heal = invasion.getBoss().getHealth()+Double.parseDouble(args[0].toString());
				if(heal>invasion.getBoss().getMaxHealth()){
					heal=invasion.getBoss().getMaxHealth();
				}
				invasion.getBoss().setHealth(heal);
				break;
			case BE_A_PRETTY_BUTTERFLY:
				//LOL NOW THE BOSS IS A HARDCORE MLG MAN
				Bat butterfly = (Bat) invasion.getBoss().getWorld().spawnEntity(invasion.getBoss().getLocation(), EntityType.BAT);
				butterfly.setPassenger(invasion.getBoss());
				break;
			default:
				break;
		}
	}
}
