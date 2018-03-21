package xyz.rodeldev.invasion.boss;

import java.util.ArrayList;
import java.util.List;

import xyz.rodeldev.invasion.utils.Util;

public class AbilityAction {
	private AbilityActions action;
	private Object[] arguments;
	
	public AbilityAction(){}
	
	public AbilityAction(AbilityActions action, Object... arguments){
		this.action = action;
		this.arguments = arguments;
	}
	

	public AbilityActions getAction() {
		return action;
	}
	
	public void setAction(AbilityActions action) {
		this.action = action;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}	
	
	public AbilityAction fromString(String string){
		String[] split = string.split(":");
		action = AbilityActions.searchBySyntaxis(string);
		
		List<Object> args = new ArrayList<>();
		for (int i = 1; i < split.length; i++) {
			if(split[i].equalsIgnoreCase("false")){
				args.add(false);
			}else if(split[i].equalsIgnoreCase("true")){
				args.add(true);
			}else if(Util.isInt(split[i])){
				args.add(Integer.parseInt(split[i]));
			}else{
				args.add(split[i]);
			}
		}
		
		arguments = args.toArray();
		return this;
	}
	
	@Override
	public String toString() {
		String result = action.getSyntaxis();
		for (int i = 0; i < arguments.length; i++) {
			result = result.replace("{"+i+"}", arguments[i].toString());
		}
		return result;
	}
}
