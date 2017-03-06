package mx.com.rodel.messages;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Titles {
	private static Class<? extends AbstractTitle> clazz = null;
	
	private String title;
	private String subtitle;
	
	public Titles() {
		this("");
	}
	
	public Titles(String title){
		this(title, "");
	}
	
	public Titles(String title, String subtitle){
		this.title = title;
		this.subtitle = subtitle;
	}
	
	public void send(Player player){
		setup();
		
		try {
			Object t = clazz.getConstructor(String.class, String.class).newInstance(title, subtitle);
			Method m = t.getClass().getMethod("send", Player.class);
			m.invoke(t, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setup(){
		if(Bukkit.getVersion().contains("1.11")){
			clazz = Titles111.class;
		}else{
			clazz = Titles110.class;
		}
	}
}
