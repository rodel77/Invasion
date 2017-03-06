package mx.com.rodel.invasion.language;

import mx.com.rodel.invasion.Main;
import mx.com.rodel.invasion.config.StringConfig;

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
