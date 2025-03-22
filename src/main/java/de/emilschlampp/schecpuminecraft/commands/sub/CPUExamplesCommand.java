package de.emilschlampp.schecpuminecraft.commands.sub;

import de.emilschlampp.schecpuminecraft.commands.SubCommand;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.Button;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.PagedPane;
import de.emilschlampp.schecpuminecraft.util.StreamUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static de.emilschlampp.schecpuminecraft.listener.CPUMainListener.getBookContent;

public class CPUExamplesCommand implements SubCommand {
    @Override
    public void exec(CommandSender sender, String[] args) {
        if(!isPlayer(sender)) {
            return;
        }
        Player player = toPlayer(sender);

        PagedPane pagedPane = new PagedPane("§cBeispiele");

        try {
            Scanner scanner = new Scanner(getClass().getResourceAsStream("/examples.txt"));


            while (scanner.hasNextLine()) {
                String read = scanner.nextLine();
                String display = read.split("~", 2)[0];
                String text = read.split("~", 2)[1];

                String content = new String(StreamUtil.readAllBytes(getClass().getResourceAsStream(text)));

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
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "examples";
    }
}
