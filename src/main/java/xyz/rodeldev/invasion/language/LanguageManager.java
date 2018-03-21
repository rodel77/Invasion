package xyz.rodeldev.invasion.language;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.config.StringConfig;

public class LanguageManager {
	Main pl;
	
	public LanguageManager(Main pl){
		this.pl = pl;
	}
	
	public void setLanguage(Languages language){
		for(StringConfig strings : StringConfig.values()){
			pl.getConfig().set("strings."+strings.toString().toLowerCase().replace("_", "."), strings.getLanguage().getLanguage(language));
		}
		pl.saveConfig();
	}
}
