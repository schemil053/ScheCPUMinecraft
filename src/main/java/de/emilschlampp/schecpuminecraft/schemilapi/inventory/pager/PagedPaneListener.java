package de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class PagedPaneListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof PagedPane) {
            ((PagedPane) holder).onClick(event);
        }
    }
}
