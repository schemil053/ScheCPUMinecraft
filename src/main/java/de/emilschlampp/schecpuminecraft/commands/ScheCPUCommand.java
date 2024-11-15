package de.emilschlampp.schecpuminecraft.commands;

import de.emilschlampp.schecpuminecraft.util.RuntimeValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheCPUCommand implements TabExecutor {
    private final List<SubCommand> subCommands = new ArrayList<>();

    public ScheCPUCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(RuntimeValues.PREFIX+"Verfügbare Befehle:");
            for (SubCommand subCommand : getSubCommands()) {
                if(subCommand.canUse(sender)) {
                    sender.sendMessage(RuntimeValues.PREFIX+"  "+subCommand.getName());
                }
            }
        }
        if(args.length >= 1) {
            SubCommand cmd = getCommand(args[0]);
            if(cmd != null) {
                if(cmd.canUse(sender)) {
                    cmd.exec(sender, removeFirstElement(args));
                } else {
                    sender.sendMessage(RuntimeValues.PREFIX+"Berechtigungsfehler.");
                }
            } else {
                sender.sendMessage(RuntimeValues.PREFIX+"Befehl nicht gefunden!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            return getSubCommands().stream().filter(s -> s.canUse(sender)).map(SubCommand::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if(args.length >= 2) {
            SubCommand cmd = getCommand(args[0]);
            if(cmd != null) {
                if(cmd.canUse(sender)) {
                    return cmd.tab(sender, removeFirstElement(args));
                }
            }
        }
        return List.of();
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    public ScheCPUCommand register(SubCommand command) {
        subCommands.add(command);
        return this;
    }

    public SubCommand getCommand(String name) {
        for (SubCommand subCommand : subCommands) {
            if(subCommand.getName().equals(name)) {
                return subCommand;
            }
        }
        return null;
    }

    private static String[] removeFirstElement(String[] arr) {
        if (arr.length == 0) {
            return arr;
        }
        String[] newArr = new String[arr.length - 1];
        System.arraycopy(arr, 1, newArr, 0, arr.length - 1);
        return newArr;
    }
}
