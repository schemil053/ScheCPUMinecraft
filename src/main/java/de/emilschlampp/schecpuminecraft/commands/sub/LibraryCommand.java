package de.emilschlampp.schecpuminecraft.commands.sub;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.commands.SubCommand;
import de.emilschlampp.schecpuminecraft.util.HighLangLibraryManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LibraryCommand implements SubCommand {
    @Override
    public void exec(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§a/schecpu library <list/reload>");
            return;
        }

        if(args[0].equals("list")) {
            sender.sendMessage("§aHighlang-Bibliotheken:");
            for (String allLibrary : ScheCPUMinecraft.getInstance().getLangLibraryManager().getAllLibraries()) {
                sender.sendMessage("§7 - §e"+allLibrary);
            }
        }
        if(args[0].equals("reload")) {
            ScheCPUMinecraft.getInstance().getLangLibraryManager().reload();
            sender.sendMessage("§6Neugeladen!");
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 1) {
            return Arrays.asList("list", "reload");
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "library";
    }
}
