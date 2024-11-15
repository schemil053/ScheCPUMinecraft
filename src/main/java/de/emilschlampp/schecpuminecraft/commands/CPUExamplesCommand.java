package de.emilschlampp.schecpuminecraft.commands;

import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.Button;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.PagedPane;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Scanner;

import static de.emilschlampp.schecpuminecraft.listener.CPUMainListener.getBookContent;

public class CPUExamplesCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        Player player = ((Player) sender);

        PagedPane pagedPane = new PagedPane("§cBeispiele");

        try {
            Scanner scanner = new Scanner(getClass().getResourceAsStream("/examples.txt"));


            while (scanner.hasNextLine()) {
                String read = scanner.nextLine();
                String display = read.split("~", 2)[0];
                String text = read.split("~", 2)[1];

                String content = new String(getClass().getResourceAsStream(text).readAllBytes());

                List<String> pages = getBookContent(content);

                pagedPane.addButton(
                        new Button(new ItemBuilder(Material.WRITABLE_BOOK)
                                .setDisplayName("§a"+display)
                                .setPages(pages.toArray(new String[0]))
                                .build(),
                                inventoryClickEvent -> {
                            player.getInventory().addItem(new ItemBuilder(Material.WRITABLE_BOOK)
                                    .setDisplayName("§a"+display)
                                    .setPages(pages.toArray(new String[0]))
                                    .build());
                                }
                        )
                );
            }
        } catch (Throwable ignored) {

        }

        pagedPane.open(player);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}
