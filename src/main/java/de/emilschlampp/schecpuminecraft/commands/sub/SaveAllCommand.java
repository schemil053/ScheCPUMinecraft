package de.emilschlampp.schecpuminecraft.commands.sub;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.commands.SubCommand;
import de.emilschlampp.schecpuminecraft.util.RuntimeValues;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SaveAllCommand implements SubCommand {
    @Override
    public void exec(CommandSender sender, String[] args) {
        sender.sendMessage(RuntimeValues.PREFIX+"Speichern...");
        for (World world : Bukkit.getWorlds()) {
            ScheCPUMinecraft.getInstance().getProgramStore().saveAll(world);
        }
        sender.sendMessage(RuntimeValues.PREFIX+"Speichern abgeschlossen!");
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "saveall";
    }
}
