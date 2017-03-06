package mx.com.rodel.invasion.mobs;

import org.bukkit.entity.EntityType;

import mx.com.rodel.invasion.invasion.InvasionSpecialEvent;


public enum InvasionMobs {
	//Vanilla invasion mobs
	ZOMBIE(EntityType.ZOMBIE, "&2&lInvasion Zombie", "if(Math.round(Math.random())==1){30}else{20}", 50, false, InvasionSpecialEvent.NONE, false), 
	SKELETON(EntityType.SKELETON, "&7&lInvasion Skeleton", "if(Math.round(Math.random())==1){30}else{20}", 20, false, InvasionSpecialEvent.NONE, false), 
	SPIDER(EntityType.CAVE_SPIDER, "&4&lInvasion Spider", "if(Math.round(Math.random())==1){15}else{10}", 10, true, InvasionSpecialEvent.NONE, false), 
	CREEPER(EntityType.CREEPER, "&a&lInvasion Creeper", "if(Math.round(Math.random())==1){30}else{20}", 10, true, InvasionSpecialEvent.NONE, false), 
	SUMMONER(EntityType.ZOMBIE, "&5&lInvasion Summoner", "50+Math.round(Math.random()*50)", 5, false, InvasionSpecialEvent.NONE, false),
	
	//Independece Day Event Mobs
	SPACE_MONSTER(EntityType.ZOMBIE, "&2&lSpace Monster", "20", 50, false, InvasionSpecialEvent.ID, true),
	SPACE_MONSTER_SHIP(EntityType.SKELETON, "&2&lSpace Monster In Space Ship", "20", 40, false, InvasionSpecialEvent.ID, true);
	
	private EntityType type;
	private String name;
	private String hp;
	private double chance;
	private boolean targetAbility;
	private InvasionSpecialEvent event;
	private boolean onlyInCode;
	
	private InvasionMobs(EntityType type, String name, String hp, double chance, boolean targetAbility, InvasionSpecialEvent event, boolean onlyInCode) {
		this.setType(type);
		this.setName(name);
		this.setHp(hp);
		this.setChance(chance);
		this.setTargetAbility(targetAbility);
		this.setInvasionSpecialEvent(event);
		this.setOnlyInCode(onlyInCode);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public EntityType getType() {
		return type;
	}

	private void setType(EntityType type) {
		this.type = type;
	}

	public String getHp() {
		return hp;
	}

	private void setHp(String hp) {
		this.hp = hp;
	}

	public double getChance() {
		return chance;
	}

	private void setChance(double chance) {
		this.chance = chance;
	}

	public boolean hasTargetAbility() {
		return targetAbility;
	}

	private void setTargetAbility(boolean targetAbility) {
		this.targetAbility = targetAbility;
	}
	
	public InvasionSpecialEvent getInvasionSpecialEvent(){
		return event;
	}
	
	private void setInvasionSpecialEvent(InvasionSpecialEvent event){
		this.event = event;
	}

	public boolean isOnlyInCode() {
		return onlyInCode;
	}

	private void setOnlyInCode(boolean onlyInCode) {
		this.onlyInCode = onlyInCode;
	}
}
