[1mdiff --git a/.gitignore b/.gitignore[m
[1mindex a031ac2..aec06bf 100644[m
[1m--- a/.gitignore[m
[1m+++ b/.gitignore[m
[36m@@ -11,3 +11,5 @@[m
 # virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml[m
 hs_err_pid*[m
 /bin/[m
[32m+[m[32m.classpath[m
[32m+[m[32m.project[m
[1mdiff --git a/src/main/java/mkremins/fanciful/FancyMessage.java b/src/main/java/mkremins/fanciful/FancyMessage.java[m
[1mdeleted file mode 100644[m
[1mindex 3e27d0d..0000000[m
[1m--- a/src/main/java/mkremins/fanciful/FancyMessage.java[m
[1m+++ /dev/null[m
[36m@@ -1,848 +0,0 @@[m
[31m-package main.java.mkremins.fanciful;[m
[31m-[m
[31m-import static main.java.mkremins.fanciful.TextualComponent.rawText;[m
[31m-[m
[31m-import java.io.IOException;[m
[31m-import java.io.StringWriter;[m
[31m-import java.lang.reflect.Constructor;[m
[31m-import java.lang.reflect.Field;[m
[31m-import java.lang.reflect.InvocationTargetException;[m
[31m-import java.lang.reflect.Method;[m
[31m-import java.lang.reflect.Modifier;[m
[31m-import java.util.ArrayList;[m
[31m-import java.util.Arrays;[m
[31m-import java.util.HashMap;[m
[31m-import java.util.Iterator;[m
[31m-import java.util.List;[m
[31m-import java.util.Map;[m
[31m-import java.util.logging.Level;[m
[31m-[m
[31m-import org.bukkit.Achievement;[m
[31m-import org.bukkit.Bukkit;[m
[31m-import org.bukkit.ChatColor;[m
[31m-import org.bukkit.Material;[m
[31m-import org.bukkit.Statistic;[m
[31m-import org.bukkit.Statistic.Type;[m
[31m-import org.bukkit.command.CommandSender;[m
[31m-import org.bukkit.configuration.serialization.ConfigurationSerializable;[m
[31m-import org.bukkit.configuration.serialization.ConfigurationSerialization;[m
[31m-import org.bukkit.entity.EntityType;[m
[31m-import org.bukkit.entity.Player;[m
[31m-import org.bukkit.inventory.ItemStack;[m
[31m-[m
[31m-import com.google.gson.JsonArray;[m
[31m-import com.google.gson.JsonElement;[m
[31m-import com.google.gson.JsonObject;[m
[31m-import com.google.gson.JsonParser;[m
[31m-import com.google.gson.stream.JsonWriter;[m
[31m-[m
[31m-import main.java.net.amoebaman.util.ArrayWrapper;[m
[31m-import main.java.net.amoebaman.util.Reflection;[m
[31m-[m
[31m-/**[m
[31m- * Represents a formattable message. Such messages can use elements such as colors, formatting codes, hover and click data, and other features provided by the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Tellraw#Raw_JSON_Text">JSON message formatter</a>.[m
[31m- * This class allows plugins to emulate the functionality of the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Commands#tellraw">tellraw command</a>.[m
[31m- * <p>[m
[31m- * This class follows the builder pattern, allowing for method chaining.[m
[31m- * It is set up such that invocations of property-setting methods will affect the current editing component,[m
[31m- * and a call to {@link #then()} or {@link #then(Object)} will append a new editing component to the end of the message,[m
[31m- * optionally initializing it with text. Further property-setting method calls will affect that editing component.[m
[31m- * </p>[m
[31m- */[m
[31m-public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable {[m
[31m-[m
[31m-	static{[m
[31m-		ConfigurationSerialization.registerClass(FancyMessage.class);[m
[31m-	}[m
[31m-[m
[31m-	private List<MessagePart> messageParts;[m
[31m-	private String jsonString;[m
[31m-	private boolean dirty;[m
[31m-[m
[31m-	private static Constructor<?> nmsPacketPlayOutChatConstructor;[m
[31m-[m
[31m-        @Override[m
[31m-	public FancyMessage clone() throws CloneNotSupportedException{[m
[31m-		FancyMessage instance = (FancyMessage)super.clone();[m
[31m-		instance.messageParts = new ArrayList<MessagePart>(messageParts.size());[m
[31m-		for(int i = 0; i < messageParts.size(); i++){[m
[31m-			instance.messageParts.add(i, messageParts.get(i).clone());[m
[31m-		}[m
[31m-		instance.dirty = false;[m
[31m-		instance.jsonString = null;[m
[31m-		return instance;[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Creates a JSON message with text.[m
[31m-	 * @param firstPartText The existing text in the message.[m
[31m-	 */[m
[31m-	public FancyMessage(final String firstPartText) {[m
[31m-		this(rawText(firstPartText));[m
[31m-	}[m
[31m-[m
[31m-	public FancyMessage(final TextualComponent firstPartText) {[m
[31m-		messageParts = new ArrayList<MessagePart>();[m
[31m-		messageParts.add(new MessagePart(firstPartText));[m
[31m-		jsonString = null;[m
[31m-		dirty = false;[m
[31m-[m
[31m-		if(nmsPacketPlayOutChatConstructor == null){[m
[31m-			try {[m
[31m-				nmsPacketPlayOutChatConstructor = Reflection.getNMSClass("PacketPlayOutChat").getDeclaredConstructor(Reflection.getNMSClass("IChatBaseComponent"));[m
[31m-				nmsPacketPlayOutChatConstructor.setAccessible(true);[m
[31m-			} catch (NoSuchMethodException e) {[m
[31m-				Bukkit.getLogger().log(Level.SEVERE, "Could not find Minecraft method or constructor.", e);[m
[31m-			} catch (SecurityException e) {[m
[31m-				Bukkit.getLogger().log(Level.WARNING, "Could not access constructor.", e);[m
[31m-			}[m
[31m-		}[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Creates a JSON message without text.[m
[31m-	 */[m
[31m-	public FancyMessage() {[m
[31m-		this((TextualComponent)null);[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Sets the text of the current editing component to a value.[m
[31m-	 * @param text The new text of the current editing component.[m
[31m-	 * @return This builder instance.[m
[31m-	 */[m
[31m-	public FancyMessage text(String text) {[m
[31m-		MessagePart latest = latest();[m
[31m-		latest.text = rawText(text);[m
[31m-		dirty = true;[m
[31m-		return this;[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Sets the text of the current editing component to a value.[m
[31m-	 * @param text The new text of the current editing component.[m
[31m-	 * @return This builder instance.[m
[31m-	 */[m
[31m-	public FancyMessage text(TextualComponent text) {[m
[31m-		MessagePart latest = latest();[m
[31m-		latest.text = text;[m
[31m-		dirty = true;[m
[31m-		return this;[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Sets the color of the current editing component to a value.[m
[31m-	 * @param color The new color of the current editing component.[m
[31m-	 * @return This builder instance.[m
[31m-	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).[m
[31m-	 */[m
[31m-	public FancyMessage color(final ChatColor color) {[m
[31m-		if (!color.isColor()) {[m
[31m-			throw new IllegalArgumentException(color.name() + " is not a color");[m
[31m-		}[m
[31m-		latest().color = color;[m
[31m-		dirty = true;[m
[31m-		return this;[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Sets the stylization of the current editing component.[m
[31m-	 * @param styles The array of styles to apply to the editing component.[m
[31m-	 * @return This builder instance.[m
[31m-	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.[m
[31m-	 */[m
[31m-	public FancyMessage style(ChatColor... styles) {[m
[31m-		for (final ChatColor style : styles) {[m
[31m-			if (!style.isFormat()) {[m
[31m-				throw new IllegalArgumentException(style.name() + " is not a style");[m
[31m-			}[m
[31m-		}[m
[31m-		latest().styles.addAll(Arrays.asList(styles));[m
[31m-		dirty = true;[m
[31m-		return this;[m
[31m-	}[m
[31m-[m
[31m-	/**[m
[31m-	 * Set the behavior of the current editing component to instruct the client to open a file on the client side filesystem when the currently edited part of the {@code FancyMessage} is clicked.[m
[31m-	 * @param path The path of the file on the client filesystem.[m
[31m-	 * @return This builder instance.[m
[31m-	 */[m
[31m-	public FancyMessage file(final String path) {[m
[31m-		o