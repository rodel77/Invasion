package mx.com.rodel.invasion.boss;

public enum AbilityActions {
	WAIT("Wait", "wait", "wait (Wait one secound. TIP: You can repeat it for wait more secounds)", true), 
	DAMAGE("Damage", "damage:{0}", "damage:{damage} (Damage all players in invasion)", true), 
	SUMMON("Summon", "summon:{0}:{1}", "summon:{entity (id http://minecraft.gamepedia.com/Data_values/Entity_IDs )}:{amount} (Summon entities)", true),
	SUMMON_EQUIPED("Summon Equiped", "summon_equiped:{0}:{1}:{2}:{3}:{4}:{5}", "summon_equiped:{entity (id http://minecraft.gamepedia.com/Data_values/Entity_IDs )}:{amount}:{helmet}:{chestplate}:{leggings}:{boots}", true),
	MOVE("Move", "move:{0}:{1}:{2}", "move:{x}:{y}:{z} (Throw player in direction)", true),
	MESSAGE("Message", "message:{0}", "message:{message} (Send message to all players in invasion)", true),
	SOUND("Sound", "sound:{0}:{1}", "sound:{sound_name}:{pitch} (Play sound)", true),
	TELEPORT("Teleport", "teleport:{0}", "teleport:{location (player, center)} (Teleport to random player or center)", true),
	HEAL("Heal", "heal:{0}", "heal:{heal} (Regenerate boss heal)", true),
	BE_A_PRETTY_BUTTERFLY("be_a_pretty_butterfly", "be_a_pretty_butterfly", "MAKE THE MOST HARDCORE BOSS 01001010 01110101 01101110 01100101 00100000 00110100", true),
	END("End", "end", "end", false);
	
	private String name;
	private String syntaxis;
	private String semanticSyntaxis;
	private boolean canBeUsed;
	
	AbilityActions(String name, String syntaxis, String semanticSyntaxis, boolean canBeUsed){
		this.name = name;
		this.syntaxis = syntaxis;
		this.semanticSyntaxis = semanticSyntaxis;
		this.canBeUsed = canBeUsed;
	}

	public String getName() {
		return name;
	}

	public String getSyntaxis() {
		return syntaxis;
	}

	public String getSemanticSyntaxis() {
		return semanticSyntaxis;
	}
	
	public static AbilityActions searchBySyntaxis(String key){
		AbilityActions result = null;
		for (AbilityActions ab : values()) {
			String start = ab.syntaxis.split(":")[0];
			
			if(key.startsWith(start)){
				result = ab;
			}
		}
		return result;
	}
	
	public boolean canBeUsed(){
		return canBeUsed;
	}
}
