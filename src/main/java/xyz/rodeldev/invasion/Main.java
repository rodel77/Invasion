package xyz.rodeldev.invasion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import xyz.rodeldev.invasion.boss.Ability;
import xyz.rodeldev.invasion.boss.AbilityActions;
import xyz.rodeldev.invasion.commands.InvasionCommand;
import xyz.rodeldev.invasion.config.ConfigNodes;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.handlers.ListenerHandler;
import xyz.rodeldev.invasion.handlers.TickHandler;
import xyz.rodeldev.invasion.invasion.Invasion;
import xyz.rodeldev.invasion.invasion.InvasionState;
import xyz.rodeldev.invasion.minigame.MinigameManager;
import xyz.rodeldev.invasion.mobs.InvasionMobs;
import xyz.rodeldev.invasion.mobs.InvasionMobsController;
import xyz.rodeldev.invasion.utils.InvasionUpdatedData;
import xyz.rodeldev.invasion.utils.ReadeableArray;
import xyz.rodeldev.invasion.utils.Util;
import xyz.rodeldev.invasion.worldinvasion.WorldInvasion;
import xyz.rodeldev.invasion.worldinvasion.WorldInvasionManager;

public class Main extends JavaPlugin{
	/**
	 * FIELDS
	 */
	
	/**
	 * FIELDS - LISTS / HASHMAPS
	 */
	private HashMap<String, Object> st = new HashMap<>();
	private HashMap<Integer, Invasion> invasions = new HashMap<>();
	private HashMap<UUID, WorldInvasion> worldInvasions = new HashMap<>();
	private HashMap<Integer, Round> rounds = new HashMap<>();
	private List<Ability> abilities = new ArrayList<>();
	public List<Block> addons = new ArrayList<>();
	
	/**
	 * FIELDS - MISC
	 */
	private static Logger log;
	public static double range = 40;
	public List<ItemStack> prizesL = new ArrayList<ItemStack>();
	private static Main instance;
	private Metrics metrics;

	/**
	 * FIELDS - CONFIG
	 */
	
	private FileConfiguration roundsC = new YamlConfiguration();
	private FileConfiguration bossAbility = new YamlConfiguration();
	private FileConfiguration prizes = new YamlConfiguration();
	
	/**
	 * FIELDS - CONFIG PATHS
	 */
	
	public String roundsP = getDataFolder().getAbsolutePath()+"/rounds.yml";
	public String prizesP = getDataFolder().getAbsolutePath()+"/prizes.yml";
	public String bossP = getDataFolder().getAbsolutePath()+"/boss_abilities.yml";
	public String arenasP = getDataFolder().getAbsolutePath()+"/arenas/";
	
	/**
	 * MANAGERS
	 */
	private MinigameManager minigame = new MinigameManager();
	
	/**
	 * FIELDS - VERSION
	 */
	public String VERSION = "";
	public static final String configVersion = "2.0";
	public static final String roundsVersion = "1.0";
	public static final String bossabVersion = "2.0";
	public String mcV="1.9";
	
	public static Main getInstance(){
		return instance;
	}
	
	/**
	 * FIELDS - METHODS
	 */
	@Override
	public void onEnable() {
		instance = this;
		VERSION = getDescription().getVersion();
		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(new ListenerHandler(this), this);
		getCommand("invasion").setExecutor(new InvasionCommand(this));
		
		if(getServer().getBukkitVersion().startsWith("1.9.4")){
			mcV="1.9.4";
		}else if(getServer().getBukkitVersion().startsWith("1.9")){
			mcV="1.9";
		}else if(getServer().getBukkitVersion().startsWith("1.8")){
			mcV="1.8";
		}
		
		log.log(Level.INFO, "Running invasion in bukkit version: {0}", mcV);

		for(InvasionMobs c : InvasionMobs.values()){
			st.put("mobs."+c.toString().toLowerCase()+".name", getMobs().getUnregisteredMob(c).getName());
			st.put("mobs."+c.toString().toLowerCase()+".hp", getMobs().getUnregisteredMob(c).getHP());
			st.put("mobs."+c.toString().toLowerCase()+".entity", getMobs().getUnregisteredMob(c).getEntity().toString());
			st.put("mobs."+c.toString().toLowerCase()+".chance", getMobs().getUnregisteredMob(c).getChance());
		}
		
		processConfigs();
		
		for (int i = 0; i < 20; i++) {
			invasions.put(i, new Invasion());
		}
		
		try {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new TickHandler(this), 0, 50);
		} catch (Exception e) {}
		
		
		/*if(!InvasionUpdatedData.getJsonValue("version", getConfig().getBoolean("config.updatechecker")).equals(VERSION)){
			getServer().getConsoleSender().sendMessage(Util.translate("&4New invasion update avaible &c"+InvasionUpdatedData.getJsonValue("version", getConfig().getBoolean("config.updatechecker"))+" "+InvasionUpdatedData.getJsonValue("versionname", getConfig().getBoolean("config.updatechecker"))));
			for(String value : InvasionUpdatedData.getChangelog(getConfig().getBoolean("config.updatechecker"))){
				getServer().getConsoleSender().sendMessage(Util.translate("&a"+value));
			}
		}*/

		getServer().getScheduler().runTaskAsynchronously(this, ()->{
            enableMetrics();
        });
	}
	
	public void enableMetrics(){
		if(getConfig().getBoolean("config.metrics", true)){
			try {
				metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				Bukkit.getLogger().warning("Error on submit stats!");
			}
		}else{
			try {
				metrics.disable();
			} catch (IOException e) {
				Bukkit.getLogger().warning("Error on disable stats!");
			}
		}
	}
	
	public void processConfigs(){
		loadConfig();
		processConfig();
		processRounds();
		processPrices();
		parseRounds();
		parsePrices();
		bossAbility();
		parseAbilities();
		minigame.loadArenas();
	}
	
	@Override
	public void onDisable() {
		for(Entry<Integer, Invasion> invasion : invasions.entrySet()){
			if(invasion.getValue().getState()!= InvasionState.EMPTY){
				invasion.getValue().reset();
			}
		}
		
		for(Player player : getServer().getOnlinePlayers()){
			player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
		}
		
		for(World world : getServer().getWorlds()){
			for(Entity entity : world.getEntities()){
				if(getMobs().isInvasionMob(entity)){
					((LivingEntity) entity).damage(999999);
				}
				
				if(entity.hasMetadata("remove_on_disable") || entity.hasMetadata("invasion_special")){
					if(entity instanceof LivingEntity){
						((LivingEntity) entity).damage(999999);
					}else{
						entity.remove();
					}
				}
			}
		}
		
		for(Entry<UUID, WorldInvasion> wI : getWorldInvasions().entrySet()){
			new WorldInvasionManager(this).stopInvasion(getServer().getWorld(wI.getKey()));
		}
		
		for(Block block : addons){
			block.setType(Material.AIR);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void parseRounds(){
		Iterator<String> set = roundsC.getKeys(false).iterator();
		while(set.hasNext()){
			String roundn = set.next();
			if(!roundn.equals("version")){
				Round parse = new Round();
				parse.setRound(Integer.parseInt(roundn));
				if(roundsC.getBoolean(roundn+".boss", false)){
					parse.setMessage(roundsC.getString(roundn+".message", "&2&lRound "+roundn+" boss!"));
					parse.setBoss(true);
					parse.setBossT(EntityType.fromName(roundsC.getString(roundn+".type", "GIANT")));
					parse.setBoosLive(roundsC.getInt(roundn+".bosslive", 100));
					parse.setBossName(roundsC.getString(roundn+".bossname", "&2&lInvasion Captain"));
					parse.parseMobs(this);
				}else{
					parse.parseMobs(this);
					parse.setGoal(roundsC.getInt(roundn+".goal", 10));
					parse.setMessage(roundsC.getString(roundn+".message", "&2&lRound "+roundn+"!"));
				}
				getRounds().put(Integer.parseInt(roundn), parse);
			}
		}
	}
	
	public void bossAbility(){
		try {
			bossAbility.load(bossP);
			String header = "Boss ability avaible actions: (Last change 2.1.0)\n";
			
			for(AbilityActions action : AbilityActions.values()){
				if(action.canBeUsed()){
					header+=action.getSemanticSyntaxis()+"\n";
				}
			}
			bossAbility.options().header(header);
			bossAbility.save(bossP);
		} catch (FileNotFoundException e) {
			try {
				bossAbility.set("version", bossabVersion);
				
				String header = "Boss ability avaible actions: (Last change 2.1.0)\n";
				
				for(AbilityActions action : AbilityActions.values()){
					if(action.canBeUsed()){
						header+=action.getSemanticSyntaxis()+"\n";
					}
				}
				
				header+="\nIf you create ability with name "+'"'+"ultimate"+'"'+" it go to be rare (Like ultimate)";
				
				bossAbility.options().header(header);
				bossAbility.set("earthquake.actions", new String[] {"message:The boss is preparing earthquake!", "wait", "wait", "wait", "move:0:2:0", "wait", "wait", "damage:1"});
				bossAbility.set("regeneration.actions", new String[] {"message:I am getting more heal!", "heal:20", "summon 22"});
				bossAbility.set("damage.actions", new String[] {"message:Now all players go to die!!!!", "wait", "wait", "damage:4", ""});
				bossAbility.set("teleport.actions", new String[] {"message:Come with me!", "teleport:player"});
				bossAbility.set("summon.actions", new String[] {"messages:And now here are my allies!!", "summon:51:10"});
				bossAbility.save(bossP);
			} catch (IOException e1) {
				log.warning("Error creating boss config");
			}
		} catch (IOException e) {
			log.warning("Error loading boss config");
		} catch (InvalidConfigurationException e) {
			log.warning("Invalid boss configuration");
			log.warning(e.getMessage());
		}
	}
	
	public void parseAbilities(){
		for(String ability : bossAbility.getKeys(false)){
			if(!ability.equals("version")){
				if(new Ability().fromConfig(this, ability)!=null){
					abilities.add(new Ability().fromConfig(this, ability));
				}
			}
		}
	}
	
	public void processRounds() {
		try {
			roundsC.load(roundsP);
			if(!roundsC.contains("version")){
				roundsC.set("version", roundsVersion);
			}
		} catch (FileNotFoundException e) {
			try {
				roundsC.set("version", "1.0");
				for (int i = 0; i < 10; i++) {
					if(i==4 || i==9){
						roundsC.set((i+1)+".message", "&2&lRound "+(i+1)+" boss!");
						roundsC.set((i+1)+".boss", true);
						roundsC.set((i+1)+".zombies", 30);
						roundsC.set((i+1)+".skeleton", 10);
						roundsC.set((i+1)+".creeper", 5);
						roundsC.set((i+1)+".spider", 5);
						roundsC.set((i+1)+".goal", -1);
						if(i==5){
							roundsC.set((i+1)+".bosslive", 100);
							roundsC.set((i+1)+".type", EntityType.SLIME.toString());
							
						}else{
							roundsC.set((i+1)+".bosslive", 1000);
							roundsC.set((i+1)+".type", EntityType.GIANT.toString());
						}
					}else if(i==0){
						roundsC.set((i+1)+".message", "&2&lRound "+(i+1)+"!");
						roundsC.set((i+1)+".boss", false);
						roundsC.set((i+1)+".zombies", 30);
						roundsC.set((i+1)+".skeleton", 0);
						roundsC.set((i+1)+".creeper", 0);
						roundsC.set((i+1)+".spider", 0);
						roundsC.set((i+1)+".goal", 10);
						roundsC.set((i+1)+".bosslive", -1);
					}else{
						roundsC.set((i+1)+".message", "&2&lRound "+(i+1)+"!");
						roundsC.set((i+1)+".boss", false);
						roundsC.set((i+1)+".zombies", 30+i);
						roundsC.set((i+1)+".skeleton", 10+i);
						roundsC.set((i+1)+".creeper", 5+i);
						roundsC.set((i+1)+".spider", 5+i);
						roundsC.set((i+1)+".goal", 10*i);
						roundsC.set((i+1)+".bosslive", -1);
					}
				}
				roundsC.save(roundsP);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			roundsC.load(roundsP);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public HashMap<Integer, Invasion> getInvasions(){
		return invasions;
	}
	
	public HashMap<UUID, WorldInvasion> getWorldInvasions(){
		return worldInvasions;
	}
	
	public HashMap<Integer, Round> getRounds(){
		return rounds;
	}
	
	public List<Ability> getAbilities(){
		return abilities;
	}
	
	@Override
	public void onLoad() {
		log = getLogger();
	}
	
	public void sendMessage(CommandSender sender, StringConfig node){
		sender.sendMessage(Util.translate(getString(StringConfig.HEADER)+" "+getString(node)));
	}
	
	public void sendMessage(CommandSender sender, StringConfig msg, String arg1, String arg1t){
		sender.sendMessage(Util.translate(getString(StringConfig.HEADER)+" "+getString(msg).replace(arg1, arg1t)));
	}
	
	public void sendMessageRaw(CommandSender sender, String msg){
		sender.sendMessage(Util.translate(getString(StringConfig.HEADER)+" "+msg));
	}
	
	public InvasionMobsController getMobs(){
		return new InvasionMobsController(this);
	}
	
	public void loadConfig(){
		try {
			getConfig().load(getDataFolder().getAbsolutePath()+"/config.yml");
			processConfig();
		} catch (FileNotFoundException e) {
			processConfig();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			getConfig().load(getDataFolder().getAbsolutePath()+"/config.yml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, String> processConfig(){
		saveConfig();
		final HashMap<Integer, String> complete = new HashMap<Integer, String>();
		
		//Header
		ReadeableArray header = new ReadeableArray("--- Invasion Configuration Help ---");
		for(ConfigNodes cfg : ConfigNodes.values()){
			header.addLine("("+cfg.getKey()+" / "+cfg.getValue()+") "+cfg.getHelp());
		}
		
		header.addSpace();
		
		for(StringConfig sfg : StringConfig.values()){
			header.addLine("("+sfg.getKey()+" / "+sfg.getLanguage().getEnglish()+") "+sfg.getHelp());
		}
		
		getConfig().options().header(header.read());
		
		//Version
		getConfig().set("version", configVersion);
		
		//Add config
		for(ConfigNodes node : ConfigNodes.values()){
			if(!getConfig().contains(node.getKey())){
				getConfig().set(node.getKey(), node.getValue());
			}
		}
		
		//Add strings
		for(StringConfig string : StringConfig.values()){
			if(!getConfig().contains(string.getKey())){
				getConfig().set(string.getKey(), string.getLanguage().getEnglish());
			}
		}
		
		//Add extra strings
		for(Entry<String, Object> f : st.entrySet()){
			if(!getConfig().contains(f.getKey())){
				getConfig().set(f.getKey(), f.getValue());
				complete.put(complete.size(), f.getKey());
			}
		}
		
		if(getConfig().contains("RandomSpawn")){
			getConfig().set("config.randomspawn", getConfig().get("RandomSpawn"));
			getConfig().set("RandomSpawn", null);
		}
		
		if(getConfig().contains("RandomSpawnProb")){
			getConfig().set("config.randomspawnprob", getConfig().get("RandomSpawnProb"));
			getConfig().set("RandomSpawnProb", null);
		}
		
		if(getConfig().contains("UpdateChecker")){
			getConfig().set("config.updatechecker", getConfig().get("UpdateChecker"));
			getConfig().set("UpdateChecker", null);
		}
		saveConfig();
		return complete;
	}
	
	public String getString(StringConfig node){
		if(getConfig().contains(node.getKey())){
			return getConfig().getString(node.getKey(), node.getLanguage().getEnglish());
		}else{
			processConfig();
			return getConfig().getString(node.getKey(), node.getLanguage().getEnglish());
		}
	}
	
	public String getStringInConfig(String node){
		if(getConfig().contains(node)){
			return getConfig().getString(node, st.get(node).toString());
		}else{
			processConfig();
			return getConfig().getString(node, st.get(node).toString());
		}
	}
	
	public FileConfiguration getRoundsConfig(){
		return roundsC;
	}
	
	public FileConfiguration getBossAbility(){
		return bossAbility;
	}
	
	public List<String> getBosses(){
		List<String> result = new ArrayList<String>();
		for(Entry<Integer, Round> bossnames : getRounds().entrySet()){
			if(bossnames.getValue().isBoss()){
				result.add(Util.translate(bossnames.getValue().getBossName()));
			}
		}
		return result;
	}
	
	public void processPrices(){
		try {
			prizes.load(prizesP);
		} catch (FileNotFoundException e) {
			try {
				prizes.set("default", new ItemStack(Material.GOLD_INGOT));
				prizes.save(prizesP);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			prizes.load(prizesP);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void parsePrices(){
		prizesL.clear();
		for(String key : prizes.getKeys(false)){
			prizesL.add(prizes.getItemStack(key, new ItemStack(Material.GOLD_HOE)));
		}
	}
	
	public FileConfiguration getPrizes(){
		return prizes;
	}
	
	public MinigameManager getMinigameManager(){
		return minigame;
	}
}
