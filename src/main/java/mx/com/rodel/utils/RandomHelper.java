package main.java.mx.com.rodel.utils;

import java.util.Random;


public class RandomHelper {
	public int range(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	public static boolean MinAndMaxThan(int minthan, int maxthan, int real){
		if(real<minthan && real>maxthan){
			return true;
		}else{
			return false;
		}
	}
}
