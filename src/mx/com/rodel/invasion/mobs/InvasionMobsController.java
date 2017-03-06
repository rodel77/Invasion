package mx.com.rodel.invasion.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.effects.ParticleEffects;
import mx.com.rodel.invasion.invasion.Invasion;
import mx.com.rodel.invasion.invasion.InvasionSpecialEvent;
import mx.com.rodel.invasion.invasion.InvasionState;
import mx.com.rodel.invasion.utils.FormulaHelper;
import mx.com.rodel.invasion.utils.Util;

public class InvasionMobsController {
	Main pl;
	public InvasionMobsController(Main pl) {
		this.pl = pl;
	}
	
	public String getMobName(InvasionMobs mob){
		return Util.translate(pl.getStringInConfig("mobs."+mob.toString().toLowerCase()+".name"));
	}
	
	public List<Entity> getAllMobsInServer(){
		List<Entity> mobs = new ArrayList<Entity>();
		for (World world : pl.getServer().getWorlds()) {
			for (Entity entity : world.getEntities()){
				for (InvasionMobs im : InvasionMobs.values()){
					if(entity.getType()==getRegisteredMob(im).getEntity() && entity.getCustomName()!=null && entity.getCustomName().equals(Util.translate(getRegisteredMob(im).getName()))){
						mobs.add(entity);
					}
				}
			}
		}
		return mobs;
	}
	
	public void killAllMobs(){
		List<Entity> list = getAllMobsInServer();
		for(Entity entity : list){
			LivingEntity le = (LivingEntity) entity;
			le.teleport(new Location(le.getWorld(), 0, 0, 0));
			le.damage(999999);
		}
		
		for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
			if(invasion.getValue().getState()!=InvasionState.EMPTY){
				invasion.getValue().getStand().remove();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public Entity spawnEntity(InvasionMobs mob, Location location){
		Entity result = location.getWorld().spawnEntity(location, mob.getType());
		result.setCustomName(Util.translate(getRegisteredMob(mob).getName()));
		
		ParticleEffects.CLOUD.display(3, 3, 3, 0.1F, 200, location, Main.range);
		
		double heal = FormulaHelper.calculateDouble(getRegisteredMob(mob).getHP(), "players", pl.getServer().getOnlinePlayers().size());
		
		((LivingEntity) result).setMaxHealth(heal==0 ? 20 : heal);
		((LivingEntity) result).setHealth(((LivingEntity) result).getMaxHealth());
		try{
			LivingEntity le = (LivingEntity) result;
			le.setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));
			//Vanilla mobs
			if(mob==InvasionMobs.SUMMONER){
				ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
				ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
				LeatherArmorMeta lam = (LeatherArmorMeta)helmet.getItemMeta();
				lam.setColor(Color.PURPLE);
				helmet.setItemMeta(lam);
				chestplate.setItemMeta(lam);
				leggings.setItemMeta(lam);
				boots.setItemMeta(lam);
				le.getEquipment().setHelmet(helmet);
				le.getEquipment().setChestplate(chestplate);
				le.getEquipment().setLeggings(leggings);
				le.getEquipment().setBoots(boots);
				le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 1, false, false));
			}
			
			//Event mobs
			if(mob==InvasionMobs.SPACE_MONSTER){
				ItemStack helmet = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
				ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
				LeatherArmorMeta lam = (LeatherArmorMeta)chestplate.getItemMeta();
				lam.setColor(Color.GREEN);
				chestplate.setItemMeta(lam);
				leggings.setItemMeta(lam);
				boots.setItemMeta(lam);
				SkullMeta skull = (SkullMeta)helmet.getItemMeta();
				skull.setOwner("Fredbob");
				skull.setDisplayName(Util.translate("&2&lMonster Head"));
				helmet.setItemMeta(skull);
				le.getEquipment().setHelmet(helmet);
				le.getEquipment().setChestplate(chestplate);
				le.getEquipment().setLeggings(leggings);
				le.getEquipment().setBoots(boots);
			}
			
			if(mob==InvasionMobs.SPACE_MONSTER_SHIP){
				ItemStack helmet = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
				ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
				LeatherArmorMeta lam = (LeatherArmorMeta)chestplate.getItemMeta();
				lam.setColor(Color.GREEN);
				chestplate.setItemMeta(lam);
				leggings.setItemMeta(lam);
				boots.setItemMeta(lam);
				SkullMeta skull = (SkullMeta)helmet.getItemMeta();
				skull.setOwner("Fredbob");
				skull.setDisplayName(Util.translate("&2&lMonster Head"));
				helmet.setItemMeta(skull);
				le.getEquipment().setHelmet(helmet);
				le.getEquipment().setChestplate(chestplate);
				le.getEquipment().setLeggings(leggings);
				le.getEquipment().setBoots(boots);
				
				Minecart space_ship = (Minecart) location.getWorld().spawnEntity(location.add(0,2,0), EntityType.MINECART);
				space_ship.setPassenger(le);
				space_ship.setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));
				
				Bat bat = (Bat) location.getWorld().spawnEntity(location.add(0, 2, 0), EntityType.BAT);
				bat.setMetadata("remove_on_disable", new FixedMetadataValue(pl, true));
				bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255, false, false));
				bat.setPassenger(space_ship);
			}
		}catch(Exception e){}
		return result;
	}
	
	public void mobParticle(Entity entity){
		if(isInvasionMob(entity)){
			switch (convertMob(entity)) {
			case SPACE_MONSTER_SHIP:
				ParticleEffects.CLOUD.display(1.5F, 0, 1.5F, 0, 50, entity.getLocation().add(0, -1, 0), 255);
				break;

			default:
				break;
			}
		}
	}
	
	public InvasionMobs convertMob(Entity entity){
		InvasionMobs result = null;
		for(InvasionMobs mob : InvasionMobs.values()){
			if(entity.getCustomName()!=null && entity.getCustomName().equals(Util.translate(getRegisteredMob(mob).getName()))){
				result = mob;
			}
		}
		return result;
	}
	
	public boolean isInvasionMob(Entity entity){
		boolean isib = false;
		for(InvasionMobs mob : InvasionMobs.values()){
			if(entity.getCustomName()!=null && entity.getCustomName().contains(Util.translate(getRegisteredMob(mob).getName()))){
				isib = true;
			}
		}
		return isib;
	}
	
	public MobRegister getRegisteredMob(final InvasionMobs mob){		
		if(pl.getConfig().contains("mobs."+mob.toString().toLowerCase()+".name") && pl.getConfig().contains("mobs."+mob.toString().toLowerCase()+".hp")){
			return new MobRegister() {
				
				@Override
				public String getName() {
					return pl.getStringInConfig("mobs."+mob.toString().toLowerCase()+".name");
				}
				
				@Override
				public String getHP() {
					return pl.getStringInConfig("mobs."+mob.toString().toLowerCase()+".hp");
				}

				@SuppressWarnings("deprecation")
				@Override
				public EntityType getEntity() {
					return EntityType.fromName(pl.getStringInConfig("mobs."+mob.toString().toLowerCase()+".entity"));
				}

				@Override
				public double getChance() {
					return pl.getConfig().getDouble("mobs."+mob.toString().toLowerCase()+".chance", 30D);
				}

				@Override
				public InvasionSpecialEvent getInvasionSpecialEvent() {
					return mob.getInvasionSpecialEvent();
				}

				@Override
				public boolean isOnlyInCode() {
					return mob.isOnlyInCode();
				}
			};
		}else{
			return getUnregisteredMob(mob);
		}
	}
	
	public MobRegister getUnregisteredMob(final InvasionMobs mob){
		MobRegister r = new MobRegister() {
			
			@Override
			public String getName() {
				return mob.getName();
			}
			
			@Override
			public String getHP() {
				return mob.getHp();
			}
			
			@Override
			public EntityType getEntity() {
				return mob.getType();
			}

			@Override
			public double getChance() {
				return mob.getChance();
			}

			@Override
			public InvasionSpecialEvent getInvasionSpecialEvent() {
				return mob.getInvasionSpecialEvent();
			}

			@Override
			public boolean isOnlyInCode() {
				return mob.isOnlyInCode();
			}
		};
		return r;
	}
	
	/*public Entity getEntity(InvasionMobs mob){
		Entity result;
		if(pl.getConfig().contains("mobs."+mob.toString().toLowerCase()+".name")){
			String builder = mob.toString().toLowerCase();
			char[] may = {builder.charAt(0)};
			String finall = new String(may).toUpperCase()+""+builder.substring(1);
			
		}
	}*/
}
