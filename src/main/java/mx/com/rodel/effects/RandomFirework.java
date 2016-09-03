package main.java.mx.com.rodel.effects;


import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class RandomFirework {		
	public static void spawn(Location location){
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        Random r = new Random();

        int rt = r.nextInt(5) + 1;
        Type type = Type.BALL;
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;

        int r2 = r.nextInt(256);
        int b2 = r.nextInt(256);
        int g2 = r.nextInt(256);
        Color c1 = Color.fromRGB(r2, g2, b2);

        r2 = r.nextInt(256);
        b2 = r.nextInt(256);
        g2 = r.nextInt(256);
        Color c2 = Color.fromRGB(r2, g2, b2);


        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

        fwm.addEffect(effect);
        
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);

        fw.setFireworkMeta(fwm);        	
	}
	
	public static Firework spawnAndGet(Location location){
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        Random r = new Random();

        int rt = r.nextInt(5) + 1;
        Type type = Type.BALL;
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;

        int r2 = r.nextInt(256);
        int b2 = r.nextInt(256);
        int g2 = r.nextInt(256);
        Color c1 = Color.fromRGB(r2, g2, b2);

        r2 = r.nextInt(256);
        b2 = r.nextInt(256);
        g2 = r.nextInt(256);
        Color c2 = Color.fromRGB(r2, g2, b2);


        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

        fwm.addEffect(effect);
        
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);

        fw.setFireworkMeta(fwm);      
        
        return fw;
	}
}