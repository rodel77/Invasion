package main.java.mx.com.rodel.utils;

public class Util {
	static String[] colors = new String[] {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§k", "§m", "§n", "§o", "§r", "§l"};

	public static String translate(String x){
		return x.replace("&", "§");
	}
	
	public static boolean isInt(String x){
		try {
			Integer.parseInt(x);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String uncolorize(String x){
		String f = "";
		for(String color : colors){
			f=x.replace(color, "");
		}
		return f;
	}
	
	public static String numberZerorizer(long number){
		if((""+number).length()==1){
			return "0"+number;
		}
		return ""+number;
	}
}
