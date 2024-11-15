package de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class SimpleGUIListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if(holder != null) {
            if(holder instanceof SimpleGUI) {
                ((SimpleGUI) holder).handleClick(event);
            }
        }
    }
}
