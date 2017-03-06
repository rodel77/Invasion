package mx.com.rodel.invasion.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FormulaHelper {
	public static Object getDamage(String f, int lvl){
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine resolve = manager.getEngineByName("js");
			String fomula = f;
			resolve.put("lvl", lvl);
			return resolve.eval(fomula);
		} catch (ScriptException e) {
			return null;
		}
	}
	
	public static Object calculate(String f, String arg, int equal){
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine resolve = manager.getEngineByName("js");
			String fomula = f;
			resolve.put(arg, equal);
			return resolve.eval(fomula);
		} catch (ScriptException e) {
			return null;
		}
	}
	
	public static int calculateInt(String f, String arg, int equal){
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine resolve = manager.getEngineByName("js");
			String fomula = f;
			resolve.put(arg, equal);
			return Math.round(Float.parseFloat(resolve.eval(fomula).toString()));
		} catch (ScriptException e) {
			return -999;
		}
	}
	
	public static double calculateDouble(String f, String arg, int equal){
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine resolve = manager.getEngineByName("js");
			String fomula = f;
			resolve.put(arg, equal);
			return Double.parseDouble(resolve.eval(fomula).toString());
		} catch (ScriptException e) {
			return -999;
		}
	}
}
