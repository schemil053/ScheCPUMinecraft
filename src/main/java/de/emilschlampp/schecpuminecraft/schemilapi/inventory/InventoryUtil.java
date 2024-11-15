package de.emilschlampp.schecpuminecraft.schemilapi.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryUtil {
    public static ItemStack createItem(Material material, String name, boolean enchanted, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        if(enchanted) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        }
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createItem(Material material, String name, int size, boolean enchanted, String... lore) {
        ItemStack itemStack = new ItemStack(material, size);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        if(enchanted) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        }
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ItemStack createHead(String owner, String name, int size, String... lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, size);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setOwner(owner);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createHead(String owner, String name, String... lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setOwner(owner);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createHead(UUID owner, String name, int size, String... lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, size);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createHead(UUID owner, String name, String... lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ItemStack createItem(Material material, String name, int size, String... lore) {
        ItemStack itemStack = new ItemStack(material, size);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createPotion(List<PotionEffect> potionEffects) {
        ItemStack is = new ItemStack(Material.POTION);
        PotionMeta im = (PotionMeta) is.getItemMeta();

        potionEffects.forEach(p -> {
            im.addCustomEffect(p, true);
        });


        is.setItemMeta(im);

        return is;
    }

    public static ItemStack createSplashPotion(List<PotionEffect> potionEffects) {
        ItemStack is = new ItemStack(Material.SPLASH_POTION);
        PotionMeta im = (PotionMeta) is.getItemMeta();

        potionEffects.forEach(p -> {
            im.addCustomEffect(p, true);
        });


        is.setItemMeta(im);

        return is;
    }

    public static boolean moveItemInventory(Inventory from, Inventory to, ItemStack itemStack) {
        if(from.containsAtLeast(itemStack, itemStack.getAmount())) {
            from.removeItem(itemStack.clone());
            if(to != null) {
                to.addItem(itemStack.clone());
            }
            return true;
        }
        return false;
    }
}
