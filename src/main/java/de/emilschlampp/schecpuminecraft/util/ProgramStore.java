package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.schecpuminecraft.schemilapi.APIHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProgramStore {
    private final Map<World, Map<Location, ProgramBlockData>> prgStore = new HashMap<>();

    public void deleteCPU(Block block) {
        prgStore.getOrDefault(block.getWorld(), new HashMap<>()).remove(block.getLocation());
        File worldsFolder = new File(APIHolder.folder, "worlds");
        File worldFolder = new File(worldsFolder, block.getWorld().getName());
        File locFile = new File(worldFolder, block.getX()+"_"+block.getY()+"_"+block.getZ()+".ed.gz");
        locFile.delete();
    }

    public ProgramBlockData getForBlock(Block block) {
        return prgStore.getOrDefault(block.getWorld(), new HashMap<>()).get(block.getLocation());
    }

    public ProgramBlockData putNewBlock(Block block) {
        if(!prgStore.containsKey(block.getWorld())) {
            prgStore.put(block.getWorld(), new HashMap<>());
        }
        prgStore.get(block.getWorld()).put(block.getLocation(), new ProgramBlockData().setLocation(block.getLocation()));

        return getForBlock(block);
    }

    public void saveAll(World world) {
        File worldsFolder = new File(APIHolder.folder, "worlds");

        File worldFolder = new File(worldsFolder, world.getName());

        if(!prgStore.containsKey(world)) {
            return;
        }

        worldFolder.mkdirs();
        prgStore.get(world).forEach(((location, programBlockData) -> {
            if(programBlockData.getFile() == null) {
                File locFile = new File(worldFolder, location.getBlockX()+"_"+location.getBlockY()+"_"+location.getBlockZ()+".ed.gz");
                programBlockData.setFile(locFile);
            }
            programBlockData.save();
        }));
    }

    public void load(World world) {
        File worldsFolder = new File(APIHolder.folder, "worlds");

        File worldFolder = new File(worldsFolder, world.getName());

        if(!worldFolder.isDirectory()) {
            return;
        }

        prgStore.put(world, new HashMap<>());
        Map<Location, ProgramBlockData> programBlockDataMap = prgStore.get(world);

        File[] fileList = worldFolder.listFiles();

        if(fileList == null) {
            return;
        }

        for (File file : fileList) {
            if(!file.isFile()) {
                continue;
            }
            try {
                ProgramBlockData data = new ProgramBlockData(file);

                if(data.getLocation() == null) {
                    String[] split = file.getName().split("_", 3);
                    data.setLocation(new Location(world, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                }

                if(data.getLocation() != null) {
                    programBlockDataMap.put(data.getLocation(), data);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                System.out.println(file.getName());
            }
        }
    }

    public Map<World, Map<Location, ProgramBlockData>> getPrgStore() {
        return prgStore;
    }

    public void unload(World world) {
        saveAll(world);
        prgStore.remove(world);
    }
}
