package de.emilschlampp.schecpuminecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;

public class DebugTest extends BukkitRunnable {
    public static boolean debug = false;
    private JavaPlugin plugin;

    public DebugTest(JavaPlugin plugin) {
        this.plugin = plugin;
        if(plugin.getResource("debug") != null) {
            debug = true;
            try {
                Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
                getFileMethod.setAccessible(true);
                file = (File) getFileMethod.invoke(plugin);
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
            runTaskTimerAsynchronously(plugin, 10, 10);
        }
    }
    File file;

    String checa = null;

    @Override
    public void run() {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            String checksum = new BigInteger(1, hash).toString(16);
            if(checa == null) {
                checa = checksum;
                return;
            } else {
                if(checa.equals(checksum)) {
                    return;
                }
            }
        } catch (Exception exception) {

        }


        broadcast("§aReceived UPDATE from "+file.getName()+"!");
        broadcast("§aUpdating...");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcast("§cReloading...");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload "+plugin.getName());
        }, 20*5);

        cancel();


    }

    private void broadcast(String msg) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(plugin.getName()+".admin")).forEach(p -> p.sendMessage(msg));
    }
}
