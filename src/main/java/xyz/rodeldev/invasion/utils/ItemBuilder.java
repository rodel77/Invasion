package xyz.rodeldev.invasion.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
	public static ItemStack buildItem(Material material, String name, List<String> lore, boolean glow){
		return buildItem(material, name, lore, glow, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack buildItem(Material material, String name, List<String> lore, boolean glow, byte data){
		ItemStack item = new ItemStack(material, 1, (short) 0, (byte) 0);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Util.translate(name));
		meta.setLore(setLoreColor(lore));
		if(glow){
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static List<String> setLoreColor(List<String> lore){
		List<String> newLore = new ArrayList<>();
		for(String old : lore){
			newLore.add(Util.translate(old));
		}
		return newLore;
	}
}
