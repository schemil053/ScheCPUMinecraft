package de.emilschlampp.schecpuminecraft.schemilapi;

import de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager.PagedPaneListener;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI.SimpleGUIListener;
import de.emilschlampp.schecpuminecraft.schemilapi.util.ModuleType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APIHolder {
    public static JavaPlugin plugin;
    public static File folder;
    public static boolean debug = false;

    private static List<ModuleType> enabled = new ArrayList<>();

    public static void enableModule(ModuleType type) {
        if(enabled.contains(type)) {
            return;
        }
        enabled.add(type);
        if(type.equals(ModuleType.PAGED_INVENTORY)) {
            plugin.getServer().getPluginManager().registerEvents(new PagedPaneListener(), plugin);
        }
        if(type.equals(ModuleType.SIMPLE_INVENTORY)) {
            plugin.getServer().getPluginManager().registerEvents(new SimpleGUIListener(), plugin);
        }
    }

    public static void init(JavaPlugin plugin, File datafolder) {
        Objects.requireNonNull(plugin);
        APIHolder.plugin = plugin;
        if(datafolder != null) {
            APIHolder.folder = datafolder;
        }else {
            APIHolder.folder = plugin.getDataFolder();
        }
        if(debug) {
            plugin.getLogger().info("SchemilAPI loaded successfully.");
        }
    }

    public static void init(JavaPlugin plugin) {
        init(plugin, null);
    }
}
