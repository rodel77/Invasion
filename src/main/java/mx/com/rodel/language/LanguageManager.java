package main.java.mx.com.rodel.language;

import main.java.mx.com.rodel.Main;
import main.java.mx.com.rodel.config.StringConfig;

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
