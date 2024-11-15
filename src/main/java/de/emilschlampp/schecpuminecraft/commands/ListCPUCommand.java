package de.emilschlampp.schecpuminecraft.commands;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.Button;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.PagedPane;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCPUCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        String mode;

        if(args.length == 1) {
            mode = args[0];
        } else {
            mode = "world";
        }

        PagedPane pagedPane = new PagedPane("CPUs");

        ScheCPUMinecraft.getInstance().getProgramStore().getPrgStore().forEach(((world, locationProgramBlockDataMap) -> {
            boolean use = false;
            if(mode.equals("world") || mode.equals("world_permaload")) {
                if(world.equals(((Player) sender).getWorld())) {
                    use = true;
                }
            } else if(mode.equals("all") || mode.equals("all_permaload")) {
                use = true;
            }
            if(use && Bukkit.getWorld(world.getUID()) != null) {
                locationProgramBlockDataMap.forEach(((location, programBlockData) -> {
                    if(mode.endsWith("_permaload")) {
                        if(!programBlockData.isForceLoaded()) {
                            return;
                        }
                    }
                    pagedPane.addButton(new Button(new ItemBuilder(Material.GOLD_BLOCK).setDisplayName("§e"+location.getWorld().getName()+" §a"+
                            location.getBlockX()+" "+location.getBlockY()+" "+location.getBlockZ()).build(), inventoryClickEvent -> {
                        if(location.isWorldLoaded()) {
                            inventoryClickEvent.getWhoClicked().teleport(location.getBlock().getLocation().add(0.5, 0, 0.5));
                        }
                    }));
                }));
            }
        }));

        pagedPane.open(((Player) sender));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of("all", "world", "world_permaload", "all_permaload");
    }
}
