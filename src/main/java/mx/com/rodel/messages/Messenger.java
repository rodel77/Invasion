package main.java.mx.com.rodel.messages;

import org.bukkit.command.CommandSender;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.config.StringConfig;
import main.java.mx.com.rodel.utils.Util;

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
