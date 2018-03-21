package xyz.rodeldev.invasion.config;

import xyz.rodeldev.invasion.Main;
import xyz.rodeldev.invasion.language.Language;

public enum StringConfig {
	//SCOREBOARD
	SCOREBOARD_CONTRIBUTORS(new Language("&7&lContributors {COUNT}", "&7&lAides {COUNT}", "&7&lHelfer {COUNT}", "&7&lAyudantes {COUNT}"), "The message in scoreboard called Contributors (Player in invasion)"),
	SCOREBOARD_TIME(new Language("&6&lTime:", "&6&lTemps", "&6&lZeit:", "&6&lTiempo:"), "The message in scoreboard called Time (Time playing in invasion)"),
	SCOREBOARD_ROUND(new Language("&5&lRound:", "&5&lRonde", "&5&lRonde:", "&5&lRonda:"), "The message in scoreboard called Round (The round in invasion)"),
	SCOREBOARD_BOSSLIVE(new Language("&4&lBoss Live:", "&4&lLa vie la t�te:", "&4&lKopf Leben:", "&4&lVida del jefe"), "The message in scoreboard called Boss Live (Boss Health)"),
	SCOREBOARD_DEFEATED(new Language("&4&lDefeated:", "&4&lMorts:", "&4&lTote:", "&4&lMuertos:"), "The message in scoreboard called Defeated (The mob kill counter)"),
	SCOREBOARD_TITLE(new Language("&2&lInvasion", "&2&lInvasion", "&2&lInvasion", "&2&lInvasi�n"), "The title of scoreboard"),
	SCOREBOARD_TITLEWORLD(new Language("&2&lWorld Invasion", "&2&lWorld Invasion", "&2&lWorld Invasion", "&2&lInvasi�n De Mundo"), "The title of scoreboard"),
	
	//JOIN/LEAVE MESSAGES
	LEAVE(new Language("&2&l{PLAYER} leave from your invasion", "&2&l{PLAYER} est sorti de votre invasion", "&2&l{PLAYER} kam aus Ihrem invasion", "&2&l{PLAYER} salio de tu invasi�n"), "When player leave from your invasion"),
	LEAVEUSER(new Language("&2&lYou leave from invasion", "&2&lVous avez laiss� une invasion", "&2&lSie kam in eine invasiddon", "&2&lSaliste de una invasi�n"), "When you leave from your invasion"),
	JOIN(new Language("&2&l{PLAYER} join in your invasion", "&2&l{PLAYER} entrez votre invasion", "&2&l{PLAYER} geben Sie Ihre invasion", "&2&l{PLAYER} entro en tu invasi�n"), "When player join in your invasion"),
	JOINUSER(new Language("&2&lYou enter in invasion", "&2&lVous avez entr� une invasion", "&2&lSie haben einen invasion", "&2&lEntraste en una invasi�n"), "When you join in invasions"),
	
	//MISC
	HEADER(new Language("&b&l[&2&lInvasion&b&l]", "&b&l[&2&lInvasion&b&l]", "&b&l[&2&lInvasion&b&l]", "&b&l[&2&lInvasi�n&b&l]"), "The header of all invasion messages"),
	AND(new Language("and", "et", "und", "y"), "Ex: player1, player2 ___ player3"),
	HOVER(new Language("[Hover here for see stats]", "[But ici pour stats]", "[Richten sie hier f�r statistik]", "[Apunta aqu� para ver los stats]"), "Is a hover for see your stats"),
	
	//ERRORS
	TOP(new Language("&c&lYou can't have more than 20 invasions", "&c&lVous ne pouvez pas avoir plus de 20 invasions", "&c&lSie k�nnen nicht mehr als 20 invasionen haben", "&c&lNo puedes tener m�s de 20 invasiones"), "When player try to spawn invasion but the server have 20"),
	NEAR(new Language("&c&lYou are near an invasion", "&c&lVous avez une invasion proximit�", "&c&lSie haben eine invasion in der n�he von", "&c&lTienes una invasi�n cerca"), "When player try to spawn invasion but is one nearby"),
	WORLD(new Language("&c&lYou can't have invasions in this world", "&c&lCe monde est libre de invasions", "&c&lDiese welt ist frei von invasionen", "&c&lEste mundo es libre de invasiones"), "When player try to spawn invasion but the world is locked"),
	NOPERMISSION(new Language("&c&lYou can't use this command (Permission needed: {PERMISSION})", "&c&lVous ne pouvez pas utiliser cette commande (Autorisation n�cessaire: {PERMISSION})", "&c&lSie k�nnen diesen Befehl nicht verwenden (Genehmigung erforderlich: {PERMISSION})", "&c&lNo puedes usar este comando (Permiso necesario: {PERMISSION})"), "When player try to use a command without permissions"),
	WORLDININVASION(new Language("&c&lThis world is in an invasion", "&c&lCe monde est dans l'invasion", "&c&lDiese Welt ist in Invasion", "&c&lEste mundo esta en invasion"), "When player try to spawn invasion in world invasion"),
	
	//DEFEAT
	DEFEATGROUP(new Language("Congratulations you defeat the invasion with: {CONTRIBUTORS} in {SECOUNDS} seconds", "F�licitations termin� l'invasion en {SECOUNDS} secondes avec {CONTRIBUTORS}", "Gl�ckwunsch invasion endete in {SECOUNDS} sekunden mit {CONTRIBUTORS}", "Felicidades terminaste la invasi�n en {SECOUNDS} segundos con {CONTRIBUTORS}"), "When player defeat invasion with contributors"),
	DEFEAT(new Language("Congratulations you defeat all invasion in {SECOUNDS} seconds", "F�licitations faisceau fini avec l'invasion en {SECOUNDS} secondes", "Gl�ckwunsch strahl fertig mit der invasion in {SECOUNDS} sekunden", "Felicidades haz terminado con la invasi�n en {SECOUNDS} segundos"), "When player defeat invasion without contributors"),
	
	//START/STARTING
	APPROACHING(new Language("&a&lInvasion is approaching", "&a&lUne invasion est � venir", "&a&lEine invasion kommt", "&a&lUna invasi�n se acerca"), "When invasion is approaching"),
	START(new Language("&2&lInvasion is starting nearby you, defeat all the monsters!", "&2&lUne invasion pr�s de chez vous d�marrez avec elle!", "&2&lEine invasion in ihrer n�he mit ihrem start-up!", "&2&lUna invasi�n inicio cerca de ti termina con ella!"), "When the invasion start"),
	RISING(new Language("&4&lInvasion is rising in {X}, {Y}, {Z} by {PLAYERSPAWN}", "&4&lUne invasion de {PLAYERSPAWN} apparu � {X}, {Y}, {Z}", "&4&lEine invasion erschien in {X}, {Y}, {Z} von {PLAYERSPAWN}", "&4&lUna invasi�n aparecio en {X}, {Y}, {Z} por {PLAYERSPAWN}"), "When the invasion go tostart"),
	DISAPPEAR(new Language("&4&lAn invasion in {X}, {Y}, {Z} is empty. Invasion will disappear in {SECONDS} seconds!", "&4&lUne invasion de {X}, {Y}, {Z} est vide et aller � dispara�tre dans {SECONDS} secondes!", "&4&lEine Invasion in {X}, {Y}, {Z} ist leer und gehen in verschwinden {SECONDS} Sekunden!", "Una invasi�n en {X}, {Y}, {Z} esta vac�a y va a desaparecer en {SECONDS} segundos"), "When invasion is empty"),
	DISAPPEARNOW(new Language("&4&lAn invasion disappeared for inactivity. All mobs have returned to their homes", "&4&lUne invasion disparaissent pour inactivit� tous les monstres viennent tous les monstres retourn�s dans leurs foyers", "&4&lEine Invasion verschwinden f�r Inaktivit�t alle Mobs kommen alle Mobs in ihre Heimat zur�ckgekehrt", "&4&lUna invasion desapareci� por inactividad todos los mobs regresaron a sus casas"), "When invasion disappear"),
	
	//ACTIONS
	KILLBOSS(new Language("&4&l{PLAYER} kill legendary boss {BOSS}", "&4&lLe l�gendaire {BOSS} est mort entre les mains de {PLAYER}", "&4&lDer legend�re {BOSS} starb in den h�nden von {PLAYER}", "&4&lEl legendario {BOSS} ha muerto en manos de {PLAYER}"), "When player killboss"),
	WORLDLOCKED(new Language("&c&lWorld locked", "&c&lMonde Verrouill�", "&c&lWelt Gesperrt", "&c&lMundo Bloqueado"), "When a player lock world for deny invasion"),
	WORLDUNLOCKED(new Language("&c&lWorld unlocked", "Monde D�verrouill�e", "Welt Freigeschaltet", "Mundo Desbloqueado"), "When player unlock a world for allow invasion"),
	
	//BOSSBAR/ACTIONBAR
	ACTIONBAR(new Language("&2&lRound [{CURRENT}/{TOTAL}] {PERCENTAGE}", "&2&lRonde [{CURRENT}/{TOTAL}] {PERCENTAGE}", "&2&lRunde [{CURRENT}/{TOTAL}] {PERCENTAGE}", "&2&lRonda [{CURRENT}/{TOTAL}] {PERCENTAGE}"), "Action Bar message"),
	BOSSBARBOSS(new Language("Boss Live {LIVE}/{MAX} {PERCENTAGE}", "La vie la t�te {LIVE}/{MAX} {PERCENTAGE}", "Kopf Leben {LIVE}/{MAX} {PERCENTAGE}", "Vida del jefe {LIVE}/{MAX} {PERCENTAGE}"), "The boss live"),
	BOSSBAR(new Language("Defeated {KILLED}/{GOAL} {PERCENTAGE}", "Morts {KILLED}/{GOAL} {PERCENTAGE}", "Tote {KILLED}/{GOAL} {PERCENTAGE}", "Muertos {KILLED}/{GOAL} {PERCENTAGE}"), "See the mobs killed in bossbar");

	
	private Language language;
	private String help;
	
	StringConfig(Language language, String help){
		this.language = language;
		this.help = help;
	}
	
	
	public Language getLanguage() {
		return language;
	}

	public String getHelp() {
		return help;
	}
	
	public String getKey(){
		return "strings."+toString().toLowerCase().replace("_", ".");
	}
	
	public String getFromConfig(){
		return Main.getInstance().getString(this);
	}
	
	public static StringConfig getByName(String name){
		StringConfig result = null;
		for(StringConfig string : values()){
			if(string.toString().contains(name)){
				result = string;
			}
		}
		return result;
	}
}
