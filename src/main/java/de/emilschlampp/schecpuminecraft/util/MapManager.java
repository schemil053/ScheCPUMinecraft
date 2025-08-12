package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.util.FolderIOUtil;
import de.emilschlampp.schecpuminecraft.schemilapi.APIHolder;
import de.emilschlampp.schecpuminecraft.screen.MapScreen;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapManager {
    private final int VERSION = 1;
    private final ProgramStore programStore;
    private final Map<World, Map<String, Integer>> mapData = new HashMap<>();

    public MapManager(ProgramStore programStore) {
        this.programStore = programStore;
    }

    public void registerMaps(World world) {
        Map<String, Integer> stringIntegerMap = mapData.get(world);

        stringIntegerMap.forEach((a, b) -> {
            ProgramBlockData data = programStore.getByLocationString(world, a);

            if(data != null) {
                if (data.getScreen() != null) {
                    MapView map = Bukkit.getMap(b); // TODO: Find a better way of doing this
                    for (MapRenderer renderer : new ArrayList<>(map.getRenderers())) {
                        map.removeRenderer(renderer);
                    }
                    map.addRenderer(new MapScreen(data.getScreen()));
                }
            }
        });
    }

    public void register(World world, String s) {
        ProgramBlockData data = programStore.getByLocationString(world, s);

        if(data != null) {
            if (data.getScreen() != null) {
                int id = get(world, s);

                MapView map = Bukkit.getMap(id); // TODO: Find a better way of doing this
                for (MapRenderer renderer : new ArrayList<>(map.getRenderers())) {
                    map.removeRenderer(renderer);
                }
                map.addRenderer(new MapScreen(data.getScreen()));
            }
        }
    }

    public Integer get(World world, Location s) {
        return get(world, s.getBlockX()+";"+s.getBlockY()+";"+s.getBlockZ());
    }

    public Integer get(World world, String s) {
        if(!mapData.containsKey(world)) {
            mapData.put(world, new HashMap<>());
        }

        Map<String, Integer> stringChannelDataMap = mapData.get(world);

        if(!stringChannelDataMap.containsKey(s)) {
            stringChannelDataMap.put(s, Bukkit.createMap(world).getId());
        }

        return stringChannelDataMap.get(s);
    }

    public void saveAll(World world) {
        File worldsFolder = new File(APIHolder.folder, "worlds");
        worldsFolder.mkdirs();

        File fw = new File(worldsFolder, world.getName()+".mapdata");

        if(!mapData.containsKey(world)) {
            fw.delete();
            return;
        }
        Set<String> data = programStore.getUsedBlocks(world);

        Map<String, Integer> write = new HashMap<>(mapData.get(world));

        write.entrySet().removeIf(a -> !data.contains(a.getKey()));

        if(write.isEmpty()) {
            fw.delete();
            return;
        }

        if(!fw.isFile()) {
            try {
                if(fw.getParentFile() != null) {
                    fw.getParentFile().mkdirs();
                }
                fw.createNewFile();
            } catch (IOException e) {

            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(fw);

            FolderIOUtil.writeInt(fos, VERSION);

            FolderIOUtil.writeInt(fos, write.size());

            write.forEach((key, value) -> {
                try {
                    FolderIOUtil.writeString(fos, key);
                    FolderIOUtil.writeInt(fos, value);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void load(World world) {
        File worldsFolder = new File(APIHolder.folder, "worlds");

        HashMap<String, Integer> value = new HashMap<>();
        mapData.put(world, value);

        File fw = new File(worldsFolder, world.getName()+".mapdata");

        if(!fw.isFile()) {
            return;
        }

        try {
            FileInputStream inputStreamn = new FileInputStream(fw);

            int fileVersion = FolderIOUtil.readInt(inputStreamn);

            if(fileVersion == 1) {
                int channelAmount = FolderIOUtil.readInt(inputStreamn);

                for (int i = 0; i < channelAmount; i++) {
                    String name = FolderIOUtil.readString(inputStreamn);
                    value.put(name, FolderIOUtil.readInt(inputStreamn));
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
