package main.java.mx.com.rodel.invasion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import main.java.mx.com.rodel.mobs.InvasionMobs;

public class InvasionPlayer {
	private boolean inInvasion;
	private HashMap<InvasionMobs, Integer> stats = new HashMap<InvasionMobs, Integer>();
	private List<String> bosses = new ArrayList<String>();
	private String name;
	private CommandSender sender;
	
	public InvasionPlayer(){
		for(InvasionMobs mobs : InvasionMobs.values()){
			stats.put(mobs, 0);
		}
	}
	
	public boolean isInInvasion() {
		return inInvasion;
	}

	public void setInInvasion(boolean inInvasion) {
		this.inInvasion = inInvasion;
	}
	
	public void incrementStat(InvasionMobs mob){
		stats.put(mob, stats.get(mob)+1);
	}
	
	public Integer getStats(InvasionMobs mob){
		return stats.get(mob);
	}
	
	public Integer calculateTotal(){
		int total = getKilledBosses();
		for(InvasionMobs mobs : InvasionMobs.values()){
			total+=stats.get(mobs);
		}
		return total;
	}

	public int getKilledBosses() {
		return bosses.size();
	}

	public void addBoss(String name) {
		bosses.add(name);
	}
	
	public List<String> getBosses(){
		return bosses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
}
