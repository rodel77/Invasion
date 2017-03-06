package mx.com.rodel.invasion.utils;

import java.util.ArrayList;

public class ReadeableArray {
	private ArrayList<String> strings = new ArrayList<>();
	
	public ReadeableArray(){}
	
	public ReadeableArray(String... lines){
		addLines(lines);
	}
	
	public void addLines(String... lines){
		for(String string : lines){
			addLine(string);
		}
	}
	
	public void addLine(String line){
		strings.add(line+"\n");
	}
	
	public void addSpace(){
		strings.add("\n");
	}
	
	public String read(){
		StringBuilder read = new StringBuilder();
		for(String string : strings){
			read.append(string);
		}
		return read.toString();
	}
}
