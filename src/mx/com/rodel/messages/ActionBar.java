package mx.com.rodel.messages;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBar {
	private static Class<?> packetActionBar;
	private static Class<?> nmsPlayer;
	private static Class<?>  nmsPlayerConnection;
	private static Field playerConnection;
	private static Class<?> obcPlayer;
	private static Class<?> chatBaseComponent;
	private static Class<?> nmsChatSerializer;
	private static Method getHandle;
	private static Method sendPacket;
	private String message;
	
	public ActionBar() {
		loadClasses();
	}
	
	public ActionBar(String message) {
		this.message = message;
		loadClasses();
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void send(Player player){
		if(packetActionBar != null){
			try {
				Object handle = getHandle(player);
				Object connection = playerConnection.get(handle);
				Object packet = packetActionBar.getConstructor(chatBaseComponent, Byte.TYPE);
				
				Object serealized;
				serealized = nmsChatSerializer.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', message));
				packet = packetActionBar.getConstructor(chatBaseComponent, Byte.TYPE).newInstance(serealized, (byte)2);
				sendPacket.invoke(connection, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadClasses(){
		if(packetActionBar==null){
			packetActionBar = getNMSClass("PacketPlayOutChat");
			chatBaseComponent = getNMSClass("IChatBaseComponent");
            nmsChatSerializer = getNMSClass("ChatComponentText");
            nmsPlayer = getNMSClass("EntityPlayer");
            nmsPlayerConnection = getNMSClass("PlayerConnection");
            playerConnection = getField(nmsPlayer, "playerConnection");
            sendPacket = getMethod(nmsPlayerConnection, "sendPacket");
            obcPlayer = getOBCClass("entity.CraftPlayer");
            getHandle = getMethod("getHandle", obcPlayer);
		}
	}
	
	private Method getMethod(String name, Class<?> clazz){
		for(Method m : clazz.getMethods()){
			if(m.getName().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	private Object getHandle(Player player) {
        try {
            return getHandle.invoke(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	private String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
    }
	
	  private Class<?> getNMSClass(String className) {
	        String fullName = "net.minecraft.server." + getVersion() + className;
	        Class<?> clazz = null;
	        try {
	            clazz = Class.forName(fullName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return clazz;
	    }

	    private Class<?> getOBCClass(String className) {
	        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
	        Class<?> clazz = null;
	        try {
	            clazz = Class.forName(fullName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return clazz;
	    }


	    private Field getField(Class<?> clazz, String name) {
	        try {
	            Field field = clazz.getDeclaredField(name);
	            field.setAccessible(true);
	            return field;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    private Method getMethod(Class<?> clazz, String name, Class<?>... args) {
	        for (Method m : clazz.getMethods())
	            if (m.getName().equals(name)
	                    && (args.length == 0 || ClassListEqual(args,
	                    m.getParameterTypes()))) {
	                m.setAccessible(true);
	                return m;
	            }
	        return null;
	    }

	    private boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
	        boolean equal = true;
	        if (l1.length != l2.length)
	            return false;
	        for (int i = 0; i < l1.length; i++)
	            if (l1[i] != l2[i]) {
	                equal = false;
	                break;
	            }
	        return equal;
	    }
}
