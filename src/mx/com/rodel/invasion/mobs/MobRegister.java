package mx.com.rodel.invasion.mobs;

import org.bukkit.entity.EntityType;

import mx.com.rodel.invasion.invasion.InvasionSpecialEvent;

public interface MobRegister {
	public String getHP();
	
	public String getName();
	
	public EntityType getEntity();
	
	public double getChance();
	
	public InvasionSpecialEvent getInvasionSpecialEvent();
	
	public boolean isOnlyInCode();
}
