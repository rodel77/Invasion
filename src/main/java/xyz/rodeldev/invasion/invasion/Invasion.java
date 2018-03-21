package xyz.rodeldev.invasion.invasion;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.Round;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.effects.RandomFirework;
import xyz.rodeldev.invasion.utils.Util;

public class Invasion {
	private HashMap<UUID, InvasionPlayer> players = new HashMap<UUID, InvasionPlayer>();
	private Main pl;
	private ArmorStand stand;
	private InvasionState state = InvasionState.EMPTY;
	private InvasionHandler task;
	private Round round;
	private LivingEntity boss;
	private int kills;
	private long timestart;
	private int slot;

	public Invasion(){
		timestart = System.currentTimeMillis();
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

	public void addPlayer(UUID uuid){
		InvasionPlayer player = new InvasionPlayer();
		
		if(players.containsKey(uuid)){
			player=players.get(uuid);
		}
		
		player.setInInvasion(true);
		player.setName(pl.getServer().getPlayer(uuid).getName());
		players.put(uuid, player);
		sendMessage(StringConfig.JOIN, "{PLAYER}", pl.getServer().getPlayer(uuid).getName(), uuid);
		pl.sendMessage(pl.getServer().getPlayer(uuid), StringConfig.JOINUSER);
	}
	
	public void removePlayer(UUID uuid){
		InvasionPlayer player = players.get(uuid);
		player.setInInvasion(false);
		if(pl.getServer().getPlayer(uuid)!=null){
			pl.sendMessage(pl.getServer().getPlayer(uuid), StringConfig.LEAVEUSER);
			sendMessage(StringConfig.LEAVE, "{PLAYER}", player.getName(), uuid);
		}else{
			
		}
		players.put(uuid, player);
	}
	
	public HashMap<UUID, InvasionPlayer> players(){
		return players;
	}
	
	public void sendRawMessage(String msg){
		for(UUID player : getActivePlayers().keySet()){
			Player p = pl.getServer().getPlayer(player);
			p.sendMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+msg));
		}
	}
	
	public void sendMessage(StringConfig node){
		for(UUID player : getActivePlayers().keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node);
		}
	}
	
	public void sendMessage(StringConfig node, String arg1, String to){
		for(UUID player : getActivePlayers().keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node, arg1, to);
		}
	}
	
	public void sendMessage(StringConfig node, String arg1, String to, UUID exception){
		for(UUID player : getActivePlayers().keySet()){
			Player p = pl.getServer().getPlayer(player);
			pl.sendMessage(p, node, arg1, to);
		}
	}

	public Location getLocation(){
		return stand.getLocation();
	}
	
	public Main getPlugin() {
		return pl;
	}

	public void setPlugin(Main pl) {
		this.pl = pl;
	}

	public ArmorStand getStand() {
		return stand;
	}

	public void setStand(ArmorStand stand) {
		this.stand = stand;
	}

	public InvasionState getState() {
		return state;
	}

	public void setState(InvasionState state) {
		this.state = state;
	}

	public InvasionHandler getTask() {
		return task;
	}

	public void setTask(InvasionHandler handler) {
		this.task = handler;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

	public LivingEntity getBoss() {
		return boss;
	}

	public void setBoss(LivingEntity boss) {
		this.boss = boss;
	}

	public int getKills() {
		return kills;
	}
	
	public void incrementKill(){
		++kills;
	}
	
	public void setKills(int kills) {
		this.kills = kills;
	}

	public long getCurrentTimestamp(){
		return System.currentTimeMillis()-timestart;
	}
	
	public long getSecounds(){
		return TimeUnit.MILLISECONDS.toSeconds(getCurrentTimestamp())>60 ? TimeUnit.MILLISECONDS.toSeconds(getCurrentTimestamp())-getMinutes()*60 : TimeUnit.MILLISECONDS.toSeconds(getCurrentTimestamp());
	}
	
	public long getMinutes(){
		return TimeUnit.MILLISECONDS.toMinutes(getCurrentTimestamp())>60 ? TimeUnit.MILLISECONDS.toMinutes(getCurrentTimestamp())-getHours()*60 : TimeUnit.MILLISECONDS.toMinutes(getCurrentTimestamp());
	}
	
	public long getHours(){
		return TimeUnit.MILLISECONDS.toHours(getCurrentTimestamp());
	}
	
	public String getTime(){
		return Util.numberZerorizer(getHours())+":"+Util.numberZerorizer(getMinutes())+":"+Util.numberZerorizer(getSecounds());
	}
	
	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public void update(Main pl){
		pl.getInvasions().put(slot, this);
	}
	
	public void reset(){
		butcher();
		task.cancel();
		stand.remove();
		state = InvasionState.EMPTY;
		for(Entry<UUID, InvasionPlayer> entry : getPlayers().entrySet()){
			pl.getServer().getPlayer(entry.getKey()).setScoreboard(pl.getServer().getScoreboardManager().getNewScoreboard());
		}
		pl.getInvasions().put(slot, new Invasion());
	}
	
	public void butcher(){
		for(Entity entity : stand.getNearbyEntities(Main.range, Main.range, Main.range)){
			if(pl.getMobs().isInvasionMob(entity)){
				RandomFirework.spawnAndGet(entity.getLocation()).setPassenger(entity);
				((LivingEntity) entity).damage(999999);
			}
			
			if(entity.getCustomName()!=null && pl.getBosses().contains(entity.getCustomName())){
				RandomFirework.spawnAndGet(entity.getLocation()).setPassenger(entity);
				((LivingEntity) entity).damage(999999);
			}
		}
		for(Entry<UUID, InvasionPlayer> entry : players.entrySet()){
			pl.getServer().getPlayer(entry.getKey()).playSound(pl.getServer().getPlayer(entry.getKey()).getLocation(), "entity.generic.explode", 1, 2);
		}
	}

	public void changeRound() {
		butcher();
		setRound(pl.getRounds().get(getRound().getRound()+1));
		for(Entry<UUID, InvasionPlayer> player : getPlayers().entrySet()){
			Player p = pl.getServer().getPlayer(player.getKey());
			p.playSound(p.getLocation(), "entity.blaze.death", 1, 0);
			sendRawMessage(getRound().getMessage());
		}
		
		if(getRound().isBoss()){
			for(Entry<UUID, InvasionPlayer> player : getPlayers().entrySet()){
				Player p = pl.getServer().getPlayer(player.getKey());
				p.playSound(p.getLocation(), "entity.blaze.death", 1, 0);
			}
			
			for(Entity mob : stand.getNearbyEntities(Main.range, Main.range, Main.range)){
				if(pl.getMobs().isInvasionMob(mob)){
					((LivingEntity) mob).damage(999999);
				}
			}
		}
		setKills(0);
	}
}
