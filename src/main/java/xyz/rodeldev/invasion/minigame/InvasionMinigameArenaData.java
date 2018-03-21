package xyz.rodeldev.invasion.minigame;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public interface InvasionMinigameArenaData {
	Location getArenaSpawn();
	
	List<Location> getMobSpawns();
	
	UUID getWorld();
}
