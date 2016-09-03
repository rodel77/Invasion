package main.java.mx.com.rodel.mobs;

import org.bukkit.entity.EntityType;

import main.java.mx.com.rodel.invasion.InvasionSpecialEvent;

public interface MobRegister {
	public String getHP();
	
	public String getName();
	
	public EntityType getEntity();
	
	public double getChance();
	
	public InvasionSpecialEvent getInvasionSpecialEvent();
	
	public boolean isOnlyInCode();
}
