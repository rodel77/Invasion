package xyz.rodeldev.invasion.config;

public enum ConfigNodes {
	UPDATECHECKER(true, "Check new updates when the server start"),
	METRICS(true, "Enable/Disable metrics http://mcstats.org/plugin/Invasion"),
	RANDOMSPAWNS_ENABLED(false, "Spawn random invasion"),
	RANDOMSPAWMNS_PROBABILITY(0.3, "Each 2.5 secounds it go to throw a number 0.0 - 100.0, if your probability is major than the random number spawn invasion"),
	RANDOMSPAWNWORLD_ENABLED(true, "Spawn random world invasion (Invasion in entire world)"),
	RANDOMSPAWNWORLD_PROBABILITY(0.2, "Each 2.5 secounds it go to throw a number 0.0 - 100.0, if your probability is major than the random number spawn world invasion"),
	BLOCKEDWORLDS(new String[] {"Example1", "Example2"}, "List of your blocked worlds for invasion and world invasion");
	
	private Object value;
	private String help;
	
	ConfigNodes(Object value, String help){
		this.value = value;
		this.help = help;
	}
	
	public String getKey(){
		return "config."+toString().toLowerCase().replace("_", ".");
	}
	
	public Object getValue(){
		return value;
	}
	
	public String getHelp(){
		return help;
	}
}
