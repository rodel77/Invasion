package main.java.mx.com.rodel.boss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import main.java.mx.com.rodel.Main;

public class Ability {
	private String name;
	private List<AbilityAction> actions;
	
	public Ability(){}
	
	public Ability(String name, List<AbilityAction> actions){
		this.name = name;
		this.actions = actions;
	}
	
	public Ability(String name, AbilityAction... actions){
		this(name, Arrays.asList(actions));
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<AbilityAction> getActions() {
		return actions;
	}

	public void setInConfig(Main pl){
		FileConfiguration config = pl.getBossAbility();
		try {
			List<String> actions = new ArrayList<>();
			
			for(AbilityAction action : this.actions){
				actions.add(action.toString());
			}
			
			config.set(name+".actions", actions);
			config.save(pl.bossP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Ability fromConfig(Main pl, String name){
		FileConfiguration config = pl.getBossAbility();
		if(config.contains(name+".actions")){
			List<AbilityAction> actions = new ArrayList<>();
			
			for(String action : config.getStringList(name+".actions")){
				if(AbilityActions.searchBySyntaxis(action)!=null){
					actions.add(new AbilityAction().fromString(action));
				}else{
					pl.getLogger().log(Level.WARNING, "Skipping action "+'"'+"{0}"+'"'+" line {1} in ability "+'"'+"{2}"+'"', new String[] {action, (actions.size()+1)+"", name});
				}
			}
		
			this.name = name;
			this.actions = actions;		
			return this;
		}else{
			String result = "Unable to find "+'"'+"probability"+'"'+" key ";
			pl.getLogger().log(Level.WARNING, "Error loading boss ability "+'"'+"{0}"+'"'+" ({1})", new String[] {name, result});
			return null;
		}
	}
}
