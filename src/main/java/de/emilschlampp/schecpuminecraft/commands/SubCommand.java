package de.emilschlampp.schecpuminecraft.commands;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public interface SubCommand {
    void exec(CommandSender sender, String[] args);
    List<String> tab(CommandSender sender, String[] args);
    String getName();

    default boolean canUse(CommandSender sender) {
        return sender.hasPermission(ScheCPUMinecraft.getInstance().getName().toLowerCase(Locale.ROOT)+".subcommand."+getName().toLowerCase(Locale.ROOT));
    }

    default boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    default Player toPlayer(CommandSender sender) {
        if(!isPlayer(sender)) {
            return null;
        }
        return (Player) sender;
    }
}
