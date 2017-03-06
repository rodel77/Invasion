package mx.com.rodel.invasion.utils;


public class AbilityBuilder {
	public static Double castMove(String key){
		if(key.equalsIgnoreCase("R")){
			return Math.random();
		}else{
			return Double.parseDouble(key);
		}
	}
}
