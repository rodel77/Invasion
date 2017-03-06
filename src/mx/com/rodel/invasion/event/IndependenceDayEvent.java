package mx.com.rodel.invasion.event;

import org.bukkit.scheduler.BukkitRunnable;

public class IndependenceDayEvent extends BukkitRunnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	/*private Main pl;
	private WorldInvasion invasion;
	private List<UUID> cooldowns = new ArrayList<>();
	public ArmorStand h;
	public ArmorStand h1;
	public ArmorStand chair1 = null;
	public ArmorStand chair2 = null;
	public Entity zombie1 = null;
	public Entity zombie2 = null;
	public int hacked;
	public int mission = 0;
	public Location button1 = null;
	public Location button2 = null;
	public boolean endsend = false;
	public HashMap<UUID, Location> locations = new HashMap<>();
	public ArrayList<Block> blocks = new ArrayList<>();
	
	
	public IndependenceDayEvent(Main pl, WorldInvasion invasion){
		this.pl = pl;
		this.invasion = invasion;
		//invasion.getBossbar().removeAll();
		invasion.getBossbar().setVisible(false);
	}

	
	@Override
	public void run() {
		invasion.setWorld(pl.getServer().getWorld(invasion.getWorld().getName()));
		
		invasion.incrementTime();
		for(Player player : invasion.getWorld().getPlayers()){
			if(!invasion.getPlayers().containsKey(player.getUniqueId())){
				invasion.addPlayer(player.getUniqueId());
			}
		}
		
		List<UUID> players = new ArrayList<>();
		
		for(Player uuid : invasion.getWorld().getPlayers()){
			players.add(uuid.getUniqueId());
		}
		
		for(Entry<UUID, InvasionPlayer> player : invasion.getPlayers().entrySet()){
			if(!players.contains(player.getKey())){
				invasion.removePlayer(player.getKey());
			}
		}
		
		int offset = invasion.getActivePlayers().size()*4;
		
		ScoreboardManager manager = pl.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("independece_day", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(Util.translate("&cWorld Alien Invasion"));
		
		Score goal = objective.getScore(Util.translate("&cInvalid goal"));
		
		if(mission==0){
			goal = objective.getScore(Util.translate("&6Goal: &aGo to y=150"));
		}else if(mission==1){
			goal = objective.getScore(Util.translate("&6Goal: &aUse the button to hack"));
		}else if(mission==2){
			goal = objective.getScore(Util.translate("&6Goal: &aWait the hack end ("+hacked+"%)"));
		}else if(mission==3){
			goal = objective.getScore(Util.translate("&6Goal: &aUse the button to destoy it"));
		}else if(mission==4){
			goal = objective.getScore(Util.translate("&6Goal: &aYou defeat the alien invasion!"));
		}
		
		goal.setScore(offset-1);
		
		Score separator = objective.getScore("");
		separator.setScore(offset-2);
		
		Score contributors = objective.getScore(Util.translate("&6Alien Killers:"));
		contributors.setScore(offset-3);
		
		int num=offset-3;
		
		for(Entry<UUID, InvasionPlayer> pla : invasion.getPlayers().entrySet()){
			if(pla.getValue().isInInvasion()){
				num-=1;
				Player playerE = pl.getServer().getPlayer(pla.getKey());
				String name = Util.translate(parse(pl.getStringInConfig("strings.deftag.user.tag"), playerE, pla.getValue().calculateTotal()));
				Set<String> keys = pl.getConfig().getKeys(true);
				playerE.setGlowing(true);
				for(String key : keys){
					String[] node = key.replace(".", ",").split(",");
					
					if(node.length==4 && node[0].equalsIgnoreCase("strings") && node[1].equalsIgnoreCase("tags") && node[3].equalsIgnoreCase("permission")){
						if(playerE.hasPermission(pl.getConfig().getString(key))){
							name = Util.translate(parse(pl.getConfig().getString("strings.tags."+node[2]+".tag"), playerE, pla.getValue().calculateTotal()));
						}
					}
				}
				
				if(playerE.isOp()){
					name = Util.translate(parse(pl.getStringInConfig("strings.deftag.op.tag"), playerE, pla.getValue().calculateTotal()));
				}
				
				if(playerE.getName().equals("rodel77")){
					name = Util.translate("&2&l&oIDEV &"+randomizeColor()+" &"+randomizeColor()+"&l rodel77 Kills "+pla.getValue().calculateTotal()+"");
					ParticleEffects.CLOUD.display(1, 0, 1, 0, 10, playerE.getLocation(), 999999);
				}
				
				Score player = objective.getScore(name);
				player.setScore(num);
				playerE.setScoreboard(board);
			}else{
				Player playerE = pl.getServer().getPlayer(pla.getKey());
				playerE.setScoreboard(manager.getNewScoreboard());
			}
		}
		
		if(mission==4 && !endsend){
			endsend=true;
			new WorldInvasionManager(pl).stopInvasionNaturally(invasion.getWorld());
		}
		
		if(mission==3){
			Button btn = (Button) button1.getBlock().getState().getData();
			if(btn.isPowered()){
				++mission;
				TNTPrimed tnt1 = ((TNTPrimed) zombie1.getWorld().spawnEntity(zombie1.getLocation(), EntityType.PRIMED_TNT));
				tnt1.setYield(7);
				tnt1.setFuseTicks(0);
				
				TNTPrimed tnt2 = ((TNTPrimed) zombie2.getWorld().spawnEntity(zombie2.getLocation(), EntityType.PRIMED_TNT));
				tnt2.setYield(7);
				tnt2.setFuseTicks(0);
				
				new WorldInvasionManager(pl).stopInvasionNaturally(invasion.getWorld());
			}
			
			Button btn2 = (Button) button2.getBlock().getState().getData();
			if(btn2.isPowered()){
				++mission;
				TNTPrimed tnt1 = ((TNTPrimed) zombie1.getWorld().spawnEntity(zombie1.getLocation(), EntityType.PRIMED_TNT));
				tnt1.setYield(7);
				tnt1.setFuseTicks(0);
				
				TNTPrimed tnt2 = ((TNTPrimed) zombie2.getWorld().spawnEntity(zombie2.getLocation(), EntityType.PRIMED_TNT));
				tnt2.setYield(7);
				tnt2.setFuseTicks(0);
				
				new WorldInvasionManager(pl).stopInvasionNaturally(invasion.getWorld());
			}
		}
		
		if(mission==2){
			++hacked;
			h1.setCustomName(Util.translate("&6"+hacked+"%"));
			
			if(hacked==25){
				invasion.sendRawMessage("&6[&cDavid Levinson&6]&a They are pulling us in. I was counting on this");
			}
			
			if(hacked==30){
				invasion.sendRawMessage("&6[&cSteven Hiller&6]&a When the hell was you plannin' on tellin' me?");
			}

			if(hacked==35){
				invasion.sendRawMessage("&6[&cDavid Levinson&6]&a Oops");
			}
			
			if(hacked==40){
				invasion.sendRawMessage("&6[&cSteven Hiller&6]&a We're gonna have to work on our communication");
			}
			
			for(Player player : invasion.getWorld().getPlayers()){
				player.playSound(player.getLocation(), Sound.BLOCK_METAL_HIT, 999999, 2);
			}
			
			if(hacked==100){
				invasion.sendRawMessage("&6[&cDavid Levinson&6]&a Time's up");
				for(Player player : invasion.getWorld().getPlayers()){
					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 999999, 1);
				}
				h.setCustomName(Util.translate("&aHacked!"));
				h1.remove();
				invasion.sendRawMessage("&6New objective: &cUse the button to destroy it");
				++mission;
			}
		}
		
		if(mission==1){
			Button btn = (Button) button1.getBlock().getState().getData();
			if(btn.isPowered()){
				++mission;
				invasion.sendRawMessage("&6New objective: &cWait the hack end");
			}
			
			Button btn2 = (Button) button2.getBlock().getState().getData();
			if(btn2.isPowered()){
				++mission;
				invasion.sendRawMessage("&6New objective: &cWait the hack end");
			}
		}
		
		for(Player player : invasion.getWorld().getPlayers()){
			if(player.getLocation().getBlockY()>150 && mission==0){
				++mission;
				for(Player player2 : invasion.getWorld().getPlayers()){
					player2.sendMessage(Util.translate("&6[&c"+player.getName()+"&6] &aI am in the mothership"));
				}
				invasion.sendRawMessage("&6New objective: &cUse the button to hack");
				Schematic schematic = new Schematic(pl.getResource("spaceship.schematic"));
				
				Location loc = player.getLocation();
				loc.setY(230);
				
				for (Block block : schematic.paste(loc)) {
					block.setMetadata("invasion_special", new FixedMetadataValue(pl, true));
					pl.addons.add(block);
					blocks.add(block);
					if(block.getType()==Material.WOOD_BUTTON){
						if(button1==null){
							button1=block.getLocation();
						}else{
							button2=block.getLocation();
						}
					}
					
					if(block.getType()==Material.IRON_BLOCK){
						block.setType(Material.AIR);
						Location spawn = block.getLocation();
						spawn.setYaw(180);
						for(Player tp : invasion.getWorld().getPlayers()){
							locations.put(tp.getUniqueId(), tp.getLocation());
							tp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 255));
							tp.teleport(spawn);
							tp.setPlayerTime(14000, true);
						}
					}
					
					if(block.getType()==Material.GOLD_BLOCK){
						final ArmorStand armorstand = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -2, 0.5), EntityType.ARMOR_STAND);
						armorstand.setMetadata("invasion_special", new FixedMetadataValue(pl, true));
						((CraftArmorStand) armorstand).getHandle().setInvisible(true);
						armorstand.setInvulnerable(true);
						armorstand.setSmall(true);
						
						if(chair1==null){
							chair1 = armorstand;
						}else{
							chair2 = armorstand;
						}
						
						Zombie alien = (Zombie) block.getWorld().spawnEntity(block.getLocation(), EntityType.ZOMBIE);
						alien.setBaby(false);
						armorstand.setPassenger(alien);
						alien.setMetadata("invasion_special", new FixedMetadataValue(pl, true));
						
						if(zombie1==null){
							zombie1=alien;
						}else{
							zombie2=alien;
						}
						
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
						alien.getEquipment().setHelmet(helmet);
						alien.getEquipment().setChestplate(chestplate);
						alien.getEquipment().setLeggings(leggings);
						alien.getEquipment().setBoots(boots);
						block.setType(Material.AIR);
						armorstand.setGravity(false);
					}
					
					if(block.getType()==Material.DIAMOND_BLOCK){
						block.setType(Material.AIR);
						final ArmorStand hologram = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -1.8, 0.5), EntityType.ARMOR_STAND);
						hologram.setMetadata("invasion_hologram", new FixedMetadataValue(pl, true));
						((CraftArmorStand) hologram).getHandle().setInvisible(true);
						hologram.setInvulnerable(true);
						hologram.setCustomNameVisible(true);
						hologram.setCustomName(Util.translate("&cHacking..."));
						hologram.setGravity(false);
						h=hologram;
						
						final ArmorStand hologram2 = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -2.1, 0.5), EntityType.ARMOR_STAND);
						hologram2.setMetadata("invasion_hologram", new FixedMetadataValue(pl, true));
						((CraftArmorStand) hologram2).getHandle().setInvisible(true);
						hologram2.setInvulnerable(true);
						hologram2.setCustomNameVisible(true);
						hologram2.setCustomName(Util.translate("&60%"));
						hologram2.setGravity(false);
						h1=hologram2;
					}
				}
			}
		}
		
		if(invasion.getDefeated()==invasion.getGoal()){
			new WorldInvasionManager(pl).stopInvasionNaturally(invasion.getWorld());
		}
		
		int c = 0;
		
		for(Entity entity : invasion.getWorld().getEntities()){
			if(pl.getMobs().isInvasionMob(entity)){
				++c;
				new InvasionMobsController(pl).mobParticle(entity);
			}
		}
		
		invasion.update(pl);
		if(c<50*invasion.getWorld().getPlayers().size()){
			for(InvasionMobs mobs : InvasionMobs.values()){
				if(Math.random()*100<50 && new Random().nextInt(4)==1 && mobs.getInvasionSpecialEvent()==InvasionSpecialEvent.ID){
					Player player = invasion.getWorld().getPlayers().get(new Random().nextInt(invasion.getWorld().getPlayers().size()));
					int range = 30;
					int x = player.getLocation().getBlockX();
					int z = player.getLocation().getBlockZ();
					int randx = new RandomHelper().range(x - range, x + range);
					int randz = new RandomHelper().range(z - range, z + range);
					Location location = new Location(player.getWorld(),randx, player.getLocation().getWorld().getHighestBlockYAt(randx, randz),randz);
					pl.getMobs().spawnEntity(mobs, location);
				}
			}
		}
	}
	
	private int getPercent(int percent, int goal){
		return Integer.parseInt(Math.round((Double.parseDouble(percent+"") * 100.0f)/Double.parseDouble(goal+""))+"");
	}
	
	private double convertToBar(int number){
		int total = Math.abs(number-100);
		String t = total+"";
		if(total==100){
			return 1;
		}else{
			if(t.length()==1){
				return Double.parseDouble("0.0"+total);
			}else{
			    return Double.parseDouble("0."+total);
			}
		}
	}
	
	private String parse(String string, Player player, int kills){
		return string.replace("{NAME}", player.getName()).replace("{KILLS}", kills+"").replace("{RANDOM}", randomizeColor());
	}
	
	private String randomizeColor(){
		String[] rand = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		return rand[new Random().nextInt(rand.length)];
	}
	
	public void registerAbility(final UUID uniqueId, InvasionMobs mob) {
		cooldowns.add(uniqueId);
		int time = 60*2;
		
		if(mob==InvasionMobs.SUMMONER){
			time = 100;
		}
		pl.getServer().getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
			public void run() {
				for (int i = 0; i < cooldowns.size(); i++) {
					if(cooldowns.get(i)==uniqueId && invasion.getState()!=InvasionState.EMPTY){
						cooldowns.remove(i);
					}
				}
			}
		}, time);
	}
	
	public List<UUID> cooldowns(){
		return cooldowns;
	}
	
	public void setBar(String title, int progress){
		BarStyle style = BarStyle.SEGMENTED_20;
		BarColor color = BarColor.GREEN;
		
		String prefix = "";
		
		int progressbar = Math.abs(progress-100);
		
		if(progressbar<10){
			style=BarStyle.SOLID;
		}else if(progressbar<20){
			style=BarStyle.SEGMENTED_6;
		}else if(progressbar<30){
			style=BarStyle.SEGMENTED_10;
		}else if(progressbar<60){
			style=BarStyle.SEGMENTED_12;
		}
		
		if(progressbar<30){
			color=BarColor.RED;
			prefix="&c&l";
		}else if(progressbar<50){
			color=BarColor.YELLOW;
			prefix="&e&l";
		}
		
		if(color==BarColor.GREEN){
			prefix = "&a&l";
		}
		
		
		
		invasion.getBossbar().setProgress(convertToBar(progress));
		invasion.getBossbar().setTitle(Util.translate(prefix+Util.uncolorize(title)));
		invasion.getBossbar().setStyle(style);
		invasion.getBossbar().setColor(color);
	}*/
}