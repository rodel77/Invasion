package mx.com.rodel.invasion.worldinvasion;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.config.StringConfig;
import mx.com.rodel.invasion.effects.RandomFirework;
import mx.com.rodel.invasion.invasion.InvasionPlayer;
import mx.com.rodel.invasion.invasion.InvasionSpecialEvent;
import mx.com.rodel.invasion.invasion.InvasionState;
import mx.com.rodel.invasion.utils.Util;

public class WorldInvasion{
	private World world;
	private int defeated;
	private int goal;
	private int time;
	
	private HashMap<UUID, InvasionPlayer> players = new HashMap<>();
	private Main pl;
	private BukkitRunnable task;
	private InvasionState state = InvasionState.EMPTY;
	private InvasionSpecialEvent event;
	
	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}

	public HashMap<UUID, InvasionPlayer> getPlayers() {
		return players;
	}
	
	public HashMap<UUID, InvasionPlayer> getActivePlayers() {
		HashMap<UUID, InvasionPlayer> active = new HashMap<>();
		for(Entry<UUID, InvasionPlayer> p : players.entrySet()){
			if(p.getValue().isInInvasion()){
				active.put(p.getKey(), p.getValue());
			}
		}
		return active;
	}

	public void setPlayers(HashMap<UUID, InvasionPlayer> players) {
		this.players = players;
	}
	
	public void setMain(Main pl){
		this.pl = pl;
	}

	public void addPlayer(UUID uuid){
		if(pl.getServer().getPlayer(uuid)!=null){
			InvasionPlayer player = new InvasionPlayer();
			player.setInInvasion(true);
			players.put(uuid, player);
			sendMessage(StringConfig.JOIN, "{PLAYER}", pl.getServer().getPlayer(uuid).getName(), uuid);
			pl.sendMessage(pl.getServer().getPlayer(uuid), StringConfig.JOINUSER);
		}
	}
	
	public void removePlayer(UUID uuid){
		InvasionPlayer player = new InvasionPlayer();
		player.setInInvasion(false);
		if(pl.getServer().getPlayer(uuid) != null){
			sendMessage(StringConfig.LEAVE, "{PLAYER}", pl.getServer().getPlayer(uuid).getName(), uuid);
			pl.sendMessage(pl.getServer().getPlayer(uuid), StringConfig.LEAVEUSER);
		}
		players.put(uuid, player);
	}
	
	public HashMap<UUID, InvasionPlayer> players(){
		return players;
	}
	
	public void sendRawMessage(String msg){
		for(UUID player : players.keySet()){
			Player p = pl.getServer().getPlayer(player);
			p.sendMessage(Util.translate(msg));
		}
	}
	
	public void sendMessage(StringConfig node){
		for(UUID player : players.keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node);
		}
	}
	
	public void sendMessage(StringConfig node, String arg1, String to){
		for(UUID player : players.keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node, arg1, to);
		}
	}
	
	public void sendMessage(StringConfig node, String arg1, String to, UUID exception){
		for(UUID player : players.keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node, arg1, to);
		}
	}

	public BukkitRunnable getTask() {
		return task;
	}

	public void setTask(BukkitRunnable task) {
		this.task = task;
	}

	public InvasionState getState() {
		return state;
	}

	public void setState(InvasionState state) {
		this.state = state;
	}

	public int getDefeated() {
		return defeated;
	}

	public void setDefeated(int defeated) {
		this.defeated = defeated;
	}
	
	public void incrementDefeated(){
		++defeated;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getTime() {
		return time/2;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public void incrementTime(){
		++time;
	}
	
	public void update(Main pl){
		pl.getWorldInvasions().put(world.getUID(), this);
	}

	public void reset() {
		butcher();
		task.cancel();
		for(Player player: world.getPlayers()){
			player.setScoreboard(pl.getServer().getScoreboardManager().getNewScoreboard());
		}
		pl.getWorldInvasions().remove(world.getUID());
	}
	
	public void butcher(){
		for(Entity entity : world.getEntities()){
			if(pl.getMobs().isInvasionMob(entity)){
				RandomFirework.spawnAndGet(entity.getLocation()).setPassenger(entity);
				((LivingEntity) entity).damage(999999);
			}
		}
		
		for(Player player : world.getPlayers()){
			player.playSound(player.getLocation(), "entity.generic.explode", 1, 2);
		}
	}

	public InvasionSpecialEvent getEvent() {
		return event;
	}

	public void setEvent(InvasionSpecialEvent event) {
		this.event = event;
	}
}
