package main.java.mx.com.rodel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import main.java.mx.com.rodel.Main;

public class UpdateChecker {
	public static final String VERSION_URL = "http://dl.rodel.com.mx/version";
	public static final String CHANGELOG_URL = "http://dl.rodel.com.mx/changelog.html";
	
	public static String getLast(Main pl) throws IOException{
		URL vu = new URL(VERSION_URL);
		URLConnection connection = vu.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String version = br.readLine();
		br.close();
		return version;
	}
	
	public static List<String> getChangeLog(Main pl) throws IOException{
		List<String> log = new ArrayList<String>();
		URL vu = new URL(CHANGELOG_URL);
		URLConnection connection = vu.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = "";
		while((line = br.readLine()) != null){
			log.add(line);
		}
		br.close();
		return log;
	}
}
