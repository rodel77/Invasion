package main.java.mx.com.rodel.utils;

import java.util.Random;


public class RandomHelper {
	public int range(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	public static boolean MinAndMaxThan(int minthan, int maxthan, int real){
		System.out.println("Ha salido "+real+" puede ser menor a "+minthan+" pero mayor de "+maxthan+"?");
		if(real<minthan && real>maxthan){
			System.out.println("Si lo fue");
			return true;
		}else{
			System.out.println("no lo fue");
			return false;
		}
	}
}
