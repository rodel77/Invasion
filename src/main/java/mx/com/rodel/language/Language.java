package main.java.mx.com.rodel.language;

import java.util.HashMap;

public class Language {
	private String english;
	private String france;
	private String german;
	private String spanish;
	private HashMap<String, String> langs = new HashMap<>();
	
	public Language(String english, String france, String german, String spanish){
		this.english = english;
		this.france = france;
		this.german = german;
		this.spanish = spanish;
		langs.put("english", english);
		langs.put("france", france);
		langs.put("german", german);
		langs.put("spanish", spanish);
	}
	
	public String getFrance() {
		return france;
	}
	
	public void setFrance(String france) {
		this.france = france;
	}
	
	public String getGerman() {
		return german;
	}
	
	public void setGerman(String german) {
		this.german = german;
	}
	
	public String getSpanish() {
		return spanish;
	}
	
	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}
	
	public String getLanguage(Languages language){
		return langs.get(language.toString().toLowerCase());
	}
}
