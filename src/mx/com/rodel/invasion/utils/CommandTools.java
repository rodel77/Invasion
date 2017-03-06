package mx.com.rodel.invasion.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTools {
	public static Player toPlayer(CommandSender sender){
		return (Player) sender;
	}
}
