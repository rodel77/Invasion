package xyz.rodeldev.invasion.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InvasionMinigameCommand implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("minigameinvasion")){
			if(args.length>=1 && args[0].equalsIgnoreCase("arena")){
				if(args.length==1){
					
				}
			}
		}
		return true;
	}
}