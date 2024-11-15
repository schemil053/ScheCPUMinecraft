package de.emilschlampp.schecpuminecraft.commands;

import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GetCodingBookCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            ((Player) sender).getInventory().addItem(new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("§cCode-Editor").setPages("; Write your code here").build());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
