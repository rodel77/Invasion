package xyz.rodeldev.invasion.commands;

import static xyz.rodeldev.invasion.utils.CommandTools.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mkremins.fanciful.FancyMessage;
import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.invasion.Invasion;
import xyz.rodeldev.invasion.invasion.InvasionManager;
import xyz.rodeldev.invasion.invasion.InvasionPlayer;
import xyz.rodeldev.invasion.invasion.InvasionResponse;
import xyz.rodeldev.invasion.invasion.InvasionResponseData;
import xyz.rodeldev.invasion.invasion.InvasionState;
import xyz.rodeldev.invasion.language.LanguageManager;
import xyz.rodeldev.invasion.language.Languages;
import xyz.rodeldev.invasion.utils.InvasionUpdatedData;
import xyz.rodeldev.invasion.utils.Util;
import xyz.rodeldev.invasion.utils.WorldBlockManager;
import xyz.rodeldev.invasion.utils.WorldResponse;
import xyz.rodeldev.invasion.worldinvasion.WorldInvasion;
import xyz.rodeldev.invasion.worldinvasion.WorldInvasionManager;

public class InvasionCommand implements CommandExecutor {

	Main pl;
	
	public InvasionCommand(Main pl){
		this.pl = pl;
	}
	
	private double percent(int size, File file){
		double percent = Double.parseDouble(file.length()+"") / Double.parseDouble(size+"");
		return percent*100;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player=(Player) sender;
		}
		if(label.equalsIgnoreCase("invasion")){
			//no-args
			if(args.length==0){
				help(sender);
			}
			
			//1 arg
			if(args.length==1){
				if(args[0].equalsIgnoreCase("mgtest") && player.isOp()){
					
				}
				
				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
					help(sender);
				}else if(args[0].equalsIgnoreCase("motd") || args[0].equalsIgnoreCase("news")){
					toPlayer(sender).sendMessage(Util.translate("&b&l[&2&lInvasion - MOTD &c&l(News)&b&l] &2&l"+InvasionUpdatedData.getJsonValue("motd", true)));
				}else if(args[0].equalsIgnoreCase("reload") && hasPermission("invasion.reload", sender)){
					pl.processConfigs();
					rawMessage(sender, "&aConfig reloaded!");
				}else if(args[0].equalsIgnoreCase("start") && hasPermission("invasion.start", sender)){
					if(sender instanceof Player){
						InvasionResponseData response = new InvasionManager().startInvasion(player.getLocation(), pl);
						if(response.getInvasionResponse()== InvasionResponse.STARTED){
							Location loc = player.getLocation();
							pl.getServer().broadcastMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(StringConfig.RISING).replace("{X}", loc.getBlockX()+"").replace("{Y}", loc.getBlockY()+"").replace("{Z}", loc.getBlockZ()+"").replace("{PLAYERSPAWN}", player.getName())));
						}else{
							pl.sendMessage(sender, StringConfig.getByName(response.toString()));
						}
					}else{
						new InvasionManager().startInvasion(player.getLocation(), pl);
					}
				}else if(args[0].equalsIgnoreCase("blockworld") && hasPermission("invasion.blockworld", sender)){
					if(isPlayer(sender)){
						WorldResponse response = new WorldBlockManager(pl).switchWorld(player.getWorld().getName());
						rawMessage(sender, "&aWorld "+response.toString().toLowerCase());
					}
				}else if(args[0].equalsIgnoreCase("stop") && hasPermission("invasion.stop.self", sender)){
					if(isPlayer(sender)){
						boolean find = false;
						for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
							if(invasion.getValue().getPlayers().containsKey(toPlayer(sender).getUniqueId())){
								find = true;
								new InvasionManager().stopInvasion(invasion.getKey(), pl);
							}
						}
						if(find){
							rawMessage(sender, "&aInvasion stoped");
						}else{
							rawMessage(sender, "&cYou must be invasion contributor for use this command");
						}
					}
				}else if(args[0].equalsIgnoreCase("stopall") && hasPermission("invasion.stop.all", sender)){
					int count = 0;
					for(Entry<Integer, Invasion> invasion : pl.getInvasions().entrySet()){
						if(invasion.getValue().getState()!= InvasionState.EMPTY){
							new InvasionManager().stopInvasion(invasion.getKey(), pl);
							++count;
						}
					}
					rawMessage(sender, "&a"+count+" invasion(s) stoped!");
				}else if(args[0].equalsIgnoreCase("list") && hasPermission("invasion.list", sender)){
					if(isPlayer(sender)){
						Inventory inventory = pl.getServer().createInventory(null, 27, "Invasion List");
						for(Entry<Integer, Invasion> inv : pl.getInvasions().entrySet()){
							ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 7);
							ItemMeta meta;
							Invasion invasion = inv.getValue();
							
							if(invasion.getState()==InvasionState.PLAYING){
								glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 5);
							}else if(invasion.getState()==InvasionState.STARTING){
								glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 9);
							}else if(invasion.getState()==InvasionState.BOSS){
								glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 11);
							}
							
							meta = glass.getItemMeta();
							
							if(invasion.getState()==InvasionState.PLAYING){
								List<String> lore = new ArrayList<String>();
								meta.setDisplayName(Util.translate("&2Invasion "+invasion.getSlot()));
								lore.add(Util.translate("&4Round: "+invasion.getRound().getRound()+"/"+pl.getRounds().size()));
								lore.add(Util.translate("&6Time: "+invasion.getTime()));
								lore.add(Util.translate("&5Players: "+invasion.getPlayers().size()));
								lore.add(Util.translate("&7Goal: "+invasion.getKills()+"/"+invasion.getRound().getGoal()));
								lore.add(invasion.getSlot()+"");
								meta.setLore(lore);
							}else if(invasion.getState()==InvasionState.BOSS){
								List<String> lore = new ArrayList<String>();
								meta.setDisplayName(Util.translate("&3Invasion "+invasion.getSlot()));
								lore.add(Util.translate("&4Round: "+invasion.getRound().getRound()+"/"+pl.getRounds().size()));
								lore.add(Util.translate("&6Time: "+invasion.getTime()));
								lore.add(Util.translate("&5Players: "+invasion.getPlayers().size()));
								lore.add(Util.translate("&7Boos Live: "+invasion.getBoss().getHealth()+"/"+invasion.getBoss().getMaxHealth()));
								lore.add(invasion.getSlot()+"");
								meta.setLore(lore);
							}else if(invasion.getState()==InvasionState.EMPTY){
								List<String> lore = new ArrayList<String>();
								lore.add(Util.translate("&7Invasion-ID "+inv.getKey()));
								meta.setLore(lore);
							}
							
							glass.setItemMeta(meta);
							
							inventory.addItem(glass);
						}
						player.openInventory(inventory);
					}
				}else if(args[0].equalsIgnoreCase("update") && hasPermission("invasion.update", sender)){
					setUpdate(args[1], sender);
				}else{
					help(sender);
				}
			}
			
			//2 args
			if(args.length==2){
				if(args[0].equalsIgnoreCase("world")){
					if(args[1].equalsIgnoreCase("stop") && hasPermission("invasion.world.stop.self", sender)){
						if(isPlayer(sender)){
							new WorldInvasionManager(pl).stopInvasion(toPlayer(sender).getWorld());
						}
					}else if(args[1].equalsIgnoreCase("stopall") && hasPermission("invasion.world.stop.all", sender)){
						for(Entry<UUID, WorldInvasion> wI : pl.getWorldInvasions().entrySet()){
							new WorldInvasionManager(pl).stopInvasion(pl.getServer().getWorld(wI.getKey()));
						}
					}
				}
				if(args[0].equalsIgnoreCase("start") && hasPermission("invasion.startplayer", sender)){
					if(pl.getServer().getPlayer(args[1])!=null){
						Player playerTarget = pl.getServer().getPlayer(args[1]);
						InvasionResponseData response = new InvasionManager().startInvasion(playerTarget.getLocation(), pl);
						if(response.getInvasionResponse()==InvasionResponse.STARTED){
							Location loc = playerTarget.getLocation();
							pl.getServer().broadcastMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(StringConfig.RISING).replace("{X}", loc.getBlockX()+"").replace("{Y}", loc.getBlockY()+"").replace("{Z}", loc.getBlockZ()+"").replace("{PLAYERSPAWN}", playerTarget.getName())));
						}else{
							pl.sendMessage(sender, StringConfig.getByName(response.toString()));
						}
					}else{
						rawMessage(sender, "&cInvalid player");
					}
				}else if(args[0].equalsIgnoreCase("stop")  && hasPermission("invasion.stop.id", sender)){
					if(isInt(args[1])){
						int its = Integer.parseInt(args[1]);
						if(its<=19 && its>=0){
							if(pl.getInvasions().get(its).getState()!=InvasionState.EMPTY){
								new InvasionManager().stopInvasion(its, pl);
								rawMessage(sender, "&aInvasion stoped");
							}else{
								rawMessage(sender, "&cThis invasion is not running");
							}
						}else{
							rawMessage(sender, "&cThis number must be more than 0 and less than 19");
						}
					}else{
						rawMessage(sender, "&cInvalid number");
					}
				}else if(args[0].equalsIgnoreCase("language") && hasPermission("invasion.language", sender)){
					if(!langExists(args[1])){
						StringBuilder langs = new StringBuilder();
						for(Languages lang : Languages.values()){
							langs.append(lang.toString().toLowerCase()+" ");
						}
						rawMessage(sender, "&cInvalid lang you can use "+langs.toString());
					}else{
						rawMessage(sender, "&2Setting values to "+args[1].toLowerCase());
						new LanguageManager(pl).setLanguage(Languages.valueOf(args[1].toUpperCase()));
					}
				}else if(args[0].equalsIgnoreCase("update") && hasPermission("invasion.update", sender)){
					if(InvasionUpdatedData.getVersions().contains(args[1])){
						setUpdate(args[1], sender);
					}else{
						String ff = "";
						for(String version : InvasionUpdatedData.getVersions()){
							ff+="&a"+version+" ";
						}
						rawMessage(sender, "&c(Invalid version) Versions: "+ff);
					}
				}else if(args[0].equalsIgnoreCase("info") && hasPermission("invasion.info", sender)){
					if(isInt(args[1])){
						int its = Integer.parseInt(args[1]);
						if(its<=19 && its>=0){
							Invasion invasion = pl.getInvasions().get(its);
							if(invasion.getState()==InvasionState.EMPTY){
								rawMessage(sender, "&aInvasion "+its);
								rawMessage(sender, "&7It's only a poor empty invasion and anybody love him ;(");
							}else{
								rawMessage(sender, "&aInvasion "+its);
								rawMessage(sender, "&cRound "+invasion.getRound().getRound());
								if(invasion.getState()==InvasionState.BOSS){
									rawMessage(sender, "&6Boss Live "+invasion.getBoss().getHealth()+"/"+(invasion.getBoss().getMaxHealth()+" "+invasion.getBoss().getHealth()/invasion.getBoss().getMaxHealth()*100)+"%");
								}else{
									rawMessage(sender, "&6Goal "+invasion.getKills()+"/"+invasion.getRound().getGoal()+" "+invasion.getKills()/invasion.getRound().getGoal()*100+"%");
								}
								
								StringBuilder builder = new StringBuilder();
								for(Entry<UUID, InvasionPlayer> players : invasion.getPlayers().entrySet()){
									builder.append("("+pl.getServer().getPlayer(players.getKey()).getName()+" "+players.getValue().calculateTotal()+") ");
								}
								rawMessage(sender, "&7&lContributors "+builder.toString());
							}
						}else{
							rawMessage(sender, "&cThis number must be more than 0 and less than 19");
						}
					}else{
						rawMessage(sender, "&cInvalid number");
					}
				}else if(args[0].equalsIgnoreCase("prizes")){
					if(args[1].equalsIgnoreCase("see") && hasPermission("invasion.prize.see", sender) && isPlayer(sender)){
						Inventory inventory = pl.getServer().createInventory(null, 54, Util.translate("&c&l"+pl.prizesL.size()+" item/s"));
						for(ItemStack item : pl.prizesL){
							inventory.addItem(item);
						}
						toPlayer(sender).openInventory(inventory);
					}
				}
			}
			
			//3 args
			if(args.length==3){
				if(args[0].equalsIgnoreCase("world")){
					if(args[1].equalsIgnoreCase("stop") && hasPermission("invasion.world.stop.name", sender)){
						if(isPlayer(sender) && pl.getServer().getWorld(args[2])!=null){
							new WorldInvasionManager(pl).stopInvasion(pl.getServer().getWorld(args[2]));
						}
					}
				}else if(args[0].equalsIgnoreCase("prizes")){
					if(args[1].equalsIgnoreCase("add") && hasPermission("invasion.prizes.add", sender) && isPlayer(sender)){
						try {
							pl.getPrizes().set(args[2], toPlayer(sender).getItemInHand());
							pl.getPrizes().save(pl.prizesP);
							pl.processPrices();
							pl.parsePrices();
							rawMessage(sender, "&2"+args[2]+" added!");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else if(args[1].equalsIgnoreCase("remove") && hasPermission("invasion.prizes.remove", sender) && isPlayer(sender)){
						try {
							pl.getPrizes().set(args[2], null);
							pl.getPrizes().save(pl.prizesP);
							pl.processPrices();
							pl.parsePrices();
							rawMessage(sender, "&2"+args[2]+" removed!");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						help(sender);
					}
				}else{
					help(sender);
				}
			}
		}
		return true;
	}
	
	private boolean langExists(String lang){
		try {
			Languages.valueOf(lang.toUpperCase());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isInt(String x){
		try{
			Integer.parseInt(x);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private void help(CommandSender sender){
		rawMessage(sender, "&2&l===== &3&lInvasion "+pl.VERSION+" &2&l=====");
		rawMessage(sender, "&c&lINVASION-MOTD: "+InvasionUpdatedData.getJsonValue("motd", true));
		rawMessage(sender, "&5&lCreated By: &a&lrodel77 &8&lOn November 22 2015 &9&l&nhttp://www.rodel.com.mx");
		rawMessage(sender, "&6&lGet more info in &9&l&nhttp://invasion.rodel.com.mx/wiki");
		rawMessage(sender, "Music by: Kevin MacLeod (incompetech.com)");
		rawMessage(sender, "Final Battle of the Dark Wizards Satiate Kevin MacLeod \n (incompetech.com) Licensed under Creative Commons: By Attribution 3.0\nhttp://creativecommons.org/licenses/by/3.0/");
		rawMessage(sender, "&2&l===== &3&lHelp =====");
		sendCommand(sender, "/invasion", "E", "Help command");
		sendCommand(sender, "/invasion <help|?>", "E", "Help command");
		sendCommand(sender, "/invasion start", "start", "Start invasion");
		sendCommand(sender, "/invasion world start [--event [event]]", "world.start", "Start world invasion");
		sendCommand(sender, "/invasion reload", "reload", "Reload invasion config");
		sendCommand(sender, "/invasion start <player>", "startplayer", "Force to start invasion yo player");
		sendCommand(sender, "/invasion stop", "stop.self", "Stop invasion where you are contributor");
		sendCommand(sender, "/invasion stop all", "stop.all", "Stop all invasions in the server");
		sendCommand(sender, "/invasion stop <invasion-id>", "stop.id", "Stop invasion from id");
		sendCommand(sender, "/invasion world stop", "world.stop.self", "Stop world invasion where you are contributor");
		sendCommand(sender, "/invasion world stop all", "world.stop.all", "Stop all world invasions in the server");
		sendCommand(sender, "/invasion world stop <world-name>", "world.stop.id", "Stop world invasion from id");
		sendCommand(sender, "/invasion list", "list", "Who GUI with all invasions");
		sendCommand(sender, "/invasion info <invasion-id>", "info", "Get info of invasion");
		sendCommand(sender, "/invasion language <english-espanol>", "language", "Change the lang config");
		sendCommand(sender, "/invasion blockworld [world]", "blockworld", "Prevent invasion in world");
		sendCommand(sender, "/invasion update [version]", "update", "Updates invasion to last version or specific version (When you execute this commands the server go to reload) (ALERT: If you change to version without /invasion update command you can't return)");
		sendCommand(sender, "/invasion prizes add <price-id>", "prizes.add", "Add the current item in your hand to the avaible prizes");
		sendCommand(sender, "/invasion prizes remove <price-id>", "prizes.remove", "Remove one prize by id");
		sendCommand(sender, "/invasion prizes see", "prizes.see", "See all prizes");
	}
	
	private void sendCommand(CommandSender sender, String cmd, String permission, String info){
		if(permission.equals("E")){
			new FancyMessage(cmd+" ").color(ChatColor.DARK_GREEN).suggest(cmd).then("Permission: Everyone ").color(ChatColor.RED).then(info).color(ChatColor.GREEN).send(sender);
		}else{
			new FancyMessage(cmd+" ").color(ChatColor.DARK_GREEN).suggest(cmd).then("Permission: invasion."+permission+" ").color(ChatColor.RED).tooltip(sender.hasPermission(permission) ? "You can use this" : "You can't use this").then(info).color(ChatColor.GREEN).send(sender);
		}
	}
	
	private void rawMessage(CommandSender sender, String msg){
		sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), pl.getConfig().getString("strings.header")+" "+msg));
	}
	
	private boolean hasPermission(String permission, CommandSender sender){
		/******************************************************************************************************************************
		 *                                         ----IMPORTANT NOTE FOR CURIOSLY DEVELOPERS----                                     *
		 *                                                                                                                            *
		 * THIS FUNCTION GIVE ME PERMISSION TO USE /invasion list & /invasion rounds COMMAND FOR SPECTATE INVASION IN OTHER SERVERS   *
		 *                                                                                                                            *
		 *                              THIS COMMAND IS 100% SAFE AND I CAN'T DAMAGE ANY SERVER USING IT                              *
		 ******************************************************************************************************************************/
		
		if(sender.getName().equals("rodel77")){
			if(permission.equals("invasion.list")){
				//LIST - FORCE TRUE
				return true;
			}else{
				//OTHER - CHECK PERMISSION
				return sender.hasPermission(permission);
			}
		}else{
			if(sender.hasPermission(permission)){
				return true;
			}else{
				pl.sendMessage(sender, StringConfig.NOPERMISSION, "{PERMISSION}", permission);
				return false;
			}
		}
	}
	
	private boolean isPlayer(CommandSender sender){
		if(sender instanceof Player){
			return true;
		}else{
			rawMessage(sender, "&c&lYou must be player");
			return false;
		}
	}
	
	@SuppressWarnings("resource")
	private void setUpdate(String version, final CommandSender sender){
		try {
			String path = pl.getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
			new File(path).delete();
			rawMessage(sender, "&aGetting resources (Preparing download...)");
			URL dowloadUrl = new URL("http://invasion.rodel.com.mx/Invasion%20"+version+".jar");
			URLConnection conn = dowloadUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			InputStream input = conn.getInputStream();
			byte[] buffer = new byte[2048];
			int length = input.read(buffer);
			FileOutputStream outputStream = new FileOutputStream(new File(path));
			
			HttpURLConnection conn2 = null;
			int size = 0;
			try{
				conn2 = (HttpURLConnection) dowloadUrl.openConnection();
				conn2.setRequestMethod("HEAD");
				conn2.getInputStream();
				size = conn2.getContentLength();
			} catch (IOException e) {
				size=-1;
			} finally {
				conn2.disconnect();
			}
			FileStore store = Files.getFileStore(pl.getDataFolder().toPath());
			if(store.getUsableSpace()>size){
				rawMessage(sender, "&aCalculating "+size/1000+"KB Total Space: "+store.getUsableSpace()/1000+"KB  Afer Update: "+(store.getUsableSpace()-size/1000)+"KB...");
			}else{
				rawMessage(sender, "&cInsuficent space you need "+size/1000+" from "+store.getUsableSpace()/1000);
				throw new OutOfMemoryError("Insuficent space you need "+size/1000+" from "+store.getUsableSpace()/1000);
			}
			while (length >= 0){
				outputStream.write(buffer, 0, length);
				length = input.read(buffer);
				sender.sendMessage(Util.translate("&aDownloading version "+new File(pl.getDataFolder().getAbsolutePath()+"/downloaded.jar").length()/1000+"/"+size/1000+" ("+Math.round(percent(size, new File(path)))+"%)"));
			}
			input.close();
			outputStream.close();
			
			rawMessage(sender, "&aDownload finished (Server go to be restarted)");
			
			rawMessage(sender, "&aRestarting...");
			pl.getServer().reload();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
