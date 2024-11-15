package de.emilschlampp.schecpuminecraft.schemilapi.inventory;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    private ItemStack stack;

    public ItemBuilder(Material mat) {
        this.stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        this.stack = new ItemStack(mat, 1, sh);
    }

    public ItemMeta getItemMeta() {
        return this.stack.getItemMeta();
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)this.stack.getItemMeta();
        meta.setColor(color);
        setItemMeta((ItemMeta)meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet())
                meta.removeEnchant(enchantment);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addAbilityDamageModifier(double action, AttributeModifier.Operation operation, EquipmentSlot slot) {
        return addAbilityModifier(action, Attribute.GENERIC_ATTACK_DAMAGE, operation, slot);
    }

    public ItemBuilder addAbilityDamageModifier(double action, AttributeModifier.Operation operation) {
        return addAbilityModifier(action, Attribute.GENERIC_ATTACK_DAMAGE, operation);
    }

    public ItemBuilder addAbilityModifier(double action, Attribute attribute, AttributeModifier.Operation operation, EquipmentSlot slot) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.addAttributeModifier(attribute,
                new AttributeModifier(UUID.nameUUIDFromBytes(attribute.name().getBytes()), attribute.name(),
                        action, operation, slot));
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addAbilityModifier(double action,  Attribute attribute, AttributeModifier.Operation operation) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.addAttributeModifier(attribute,
                new AttributeModifier(UUID.nameUUIDFromBytes(attribute.name().getBytes()), attribute.name(),
                        action, operation));
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = this.stack.getItemMeta();
        meta.setCustomModelData(data);
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        if(!(this.stack.getItemMeta() instanceof BannerMeta)) {
            return this;
        }
        BannerMeta meta = (BannerMeta)this.stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta((ItemMeta)meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        if(!(this.stack.getItemMeta() instanceof SkullMeta)) {
            return this;
        }
        SkullMeta meta = (SkullMeta)this.stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta((ItemMeta)meta);
        return this;
    }


    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }


    public ItemBuilder setLore(String... lore) {
        List<String> loreList = Arrays.asList(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(new ItemFlag[] { flag });
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setPages(String... pages) {
        if(!(getItemMeta() instanceof BookMeta)) {
            return this;
        }
        BookMeta meta = (BookMeta) getItemMeta();
        meta.setPages(pages);
        setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }
}

