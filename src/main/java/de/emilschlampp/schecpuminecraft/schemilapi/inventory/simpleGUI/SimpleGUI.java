package de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI;

import de.emilschlampp.schecpuminecraft.schemilapi.inventory.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SimpleGUI implements InventoryHolder {
    private final Inventory inventory;
    private final Map<Integer, SimpleButton> simpleButtonMap;

    private boolean useBackground;
    private ItemStack backgroundItem = InventoryUtil.createItem(Material.BLACK_STAINED_GLASS_PANE, "§0", false);

    public SimpleGUI(String title, int rows) {
        inventory = Bukkit.createInventory(this, rows*9, title);
        simpleButtonMap = new HashMap<>();
    }

    public SimpleGUI(String title) {
        this(title, 6);
    }

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public SimpleGUI setButton(int slot, SimpleButton button) {
        if(button == null) {
            simpleButtonMap.remove(slot);
        } else {
            simpleButtonMap.put(slot, button);
        }
        update();
        return this;
    }

    public SimpleGUI update() {
        for(int i = 0; i<inventory.getSize(); i++) {
            SimpleButton button = simpleButtonMap.get(i);
            if(button == null) {
                if(useBackground) {
                    inventory.setItem(i, backgroundItem);
                } else {
                    inventory.setItem(i, null);
                }
                continue;
            }
            inventory.setItem(i, button.getItemStack());
        }
        return this;
    }

    public ItemStack getBackgroundItem() {
        return backgroundItem;
    }

    public SimpleGUI setBackgroundItem(ItemStack backgroundItem) {
        this.backgroundItem = backgroundItem;
        update();
        return this;
    }

    public boolean isUseBackground() {
        return useBackground;
    }

    public SimpleGUI setUseBackground(boolean useBackground) {
        this.useBackground = useBackground;
        update();
        return this;
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if(simpleButtonMap.containsKey(event.getRawSlot())) {
            SimpleButton button = simpleButtonMap.get(event.getRawSlot());
            if(button != null) {
                if(button.getOnClick() != null) {
                    button.getOnClick().accept(event);
                }
            }
        }
    }
}
