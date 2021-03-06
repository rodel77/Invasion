package xyz.rodeldev.invasion.messages;

import org.bukkit.command.CommandSender;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.StringConfig;
import xyz.rodeldev.invasion.utils.Util;

public class Messenger {
	static Main pl;
	
	static {
		pl = Main.getInstance();
	}
	
	public static void sendMessage(CommandSender sender, StringConfig node){
		sender.sendMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(node)));
	}
	
	public static void sendMessage(CommandSender sender, StringConfig msg, String arg1, String arg1t){
		sender.sendMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+pl.getString(msg).replace(arg1, arg1t)));
	}
	
	public static void sendMessageRaw(CommandSender sender, String msg){
		sender.sendMessage(Util.translate(pl.getString(StringConfig.HEADER)+" "+msg));
	}
}
