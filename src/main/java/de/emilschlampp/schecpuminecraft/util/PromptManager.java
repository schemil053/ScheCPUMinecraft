package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PromptManager implements Listener {
    private final Map<Player, Consumer<String>> chatPrompt = new HashMap<>();
    private final Map<Player, Consumer<ItemStack>> itemPrompt = new HashMap<>();

    public void prompt(Player player, Consumer<String> input) {
        player.sendMessage(RuntimeValues.PREFIX+"Eingabe in den Chat:");
        chatPrompt.put(player, input);
    }

    public void promptItem(Player player, Consumer<ItemStack> input) {
        player.closeInventory();
        player.sendMessage(RuntimeValues.PREFIX+"Klicke auf ein Item!");
        itemPrompt.put(player, input);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Consumer<ItemStack> in = itemPrompt.remove(event.getWhoClicked());

        if(in != null) {
            event.setCancelled(true);
            in.accept(event.getCurrentItem());
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Consumer<String> in = chatPrompt.remove(event.getPlayer());

        if(in != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(ScheCPUMinecraft.getInstance(), () -> {
               in.accept(event.getMessage());
            });
        }
    }

}
