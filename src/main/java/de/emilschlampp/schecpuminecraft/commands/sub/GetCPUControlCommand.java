package de.emilschlampp.schecpuminecraft.commands.sub;

import de.emilschlampp.schecpuminecraft.commands.SubCommand;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GetCPUControlCommand implements SubCommand {
    @Override
    public void exec(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            toPlayer(sender).getInventory().addItem(new ItemBuilder(Material.STICK).setDisplayName("§cCPU-Bedienfeld").build());
        }
    }

    @Override
    public List<String> tab(CommandSender sender,  String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "getcpucontrol";
    }
}
