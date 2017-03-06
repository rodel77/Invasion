package mx.com.rodel.invasion.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class InvasionUpdatedData {
	public static String getJsonValue(String key, boolean key2){
		if(key2){
			try {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(new InputStreamReader(new URL("http://invasion.rodel.com.mx/data.json").openStream()));
				JSONObject json = (JSONObject) obj; 
				return json.get(key)+"";
			} catch (IOException e) {
				return "Error getting invasion data";
			} catch (ParseException e){
				return "Error parsing invasion json data";
			}
		}else{
			return "";
		}
	}
	
	public static List<String> getChangelog(boolean key){
		if(key){
			try {
				List<String> log = new ArrayList<String>();
				URL vu = new URL("http://invasion.rodel.com.mx/changelog.txt");
				URLConnection connection = vu.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = "";
				while((line = br.readLine()) != null){
					log.add(line);
				}
				br.close();
				return log;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return new ArrayList<String>();
		}
	}
	
	public static ArrayList<String> getVersions(){
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new InputStreamReader(new URL("http://invasion.rodel.com.mx/data.json").openStream()));
			JSONObject json = (JSONObject) obj; 
			JSONArray array = (JSONArray) json.get("versions");
			
			ArrayList<String> vs = new ArrayList<>();
			
			for(Object string : array){
				JSONObject obj2 = (JSONObject) string;
				vs.add(obj2.get("version").toString());
			}
			
			return vs;
		} catch (IOException e) {
			return null;
		} catch (ParseException e){
			return null;
		}
	}
}
