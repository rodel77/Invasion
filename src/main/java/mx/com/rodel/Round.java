package main.java.mx.com.rodel;

import java.util.HashMap;

import org.bukkit.entity.EntityType;

import main.java.mx.com.rodel.invasion.InvasionSpecialEvent;
import main.java.mx.com.rodel.mobs.InvasionMobs;

public class Round {
	private String message = "null";
	private HashMap<InvasionMobs, Double> probabilitys = new HashMap<InvasionMobs, Double>();
	private boolean boss = false;
	private int goal = -1;
	private int round = -1;
	private int boosLive = -1;
	private EntityType bossT;
	private String bossName;

	public HashMap<InvasionMobs, Double> getProbabilitys(){
		return probabilitys;
	}
	
	public Double getProbability(InvasionMobs mob){
		if(mob.getInvasionSpecialEvent()==InvasionSpecialEvent.NONE){
			return probabilitys.get(mob);
		}else{
			return -999.0;
		}
	}
	
	public boolean isBoss() {
		return boss;
	}

	public void setBoss(boolean boss) {
		this.boss = boss;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getBoosLive() {
		return boosLive;
	}

	public void setBoosLive(int boosLive) {
		this.boosLive = boosLive;
	}

	public EntityType getBossT() {
		return bossT;
	}

	public void setBossT(EntityType bossT) {
		this.bossT = bossT;
	}

	public String getBossName() {
		return bossName;
	}

	public void setBossName(String bossName) {
		this.bossName = bossName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void parseMobs(Main pl) {
		for(InvasionMobs mobs : InvasionMobs.values()){
			if(pl.getRoundsConfig().contains(round+"."+mobs.toString().toLowerCase())){
				probabilitys.put(mobs, pl.getRoundsConfig().getDouble(round+"."+mobs.toString().toLowerCase(), mobs.getChance()));
			}else{
				pl.getRoundsConfig().set(round+"."+mobs.toString().toLowerCase(), mobs.getChance());
				probabilitys.put(mobs, pl.getRoundsConfig().getDouble(round+"."+mobs.toString().toLowerCase(), mobs.getChance()));
			}
		}
	}
}
