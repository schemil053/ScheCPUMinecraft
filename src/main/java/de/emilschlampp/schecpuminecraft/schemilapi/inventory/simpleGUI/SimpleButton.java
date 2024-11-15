package de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class SimpleButton {
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> onClick;

    public SimpleButton(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public SimpleButton(String name, Material material) {
        this(createItem(material, name));
    }

    public SimpleButton(ItemStack itemStack, Consumer<InventoryClickEvent> onClick) {
        this.itemStack = itemStack;
        this.onClick = onClick;
    }

    public SimpleButton(String name, Material material, Consumer<InventoryClickEvent> onClick) {
        this(createItem(material, name), onClick);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SimpleButton setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public Consumer<InventoryClickEvent> getOnClick() {
        return onClick;
    }

    public SimpleButton setOnClick(Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleButton that = (SimpleButton) o;
        return Objects.equals(itemStack, that.itemStack) && Objects.equals(onClick, that.onClick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack, onClick);
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
