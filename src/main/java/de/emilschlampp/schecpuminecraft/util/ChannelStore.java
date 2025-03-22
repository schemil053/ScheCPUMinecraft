package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.util.FolderIOUtil;
import de.emilschlampp.schecpuminecraft.schemilapi.APIHolder;
import org.bukkit.World;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChannelStore {
    private final int VERSION = 1;
    private final ProgramStore programStore;
    private final Map<World, Map<String, ChannelData>> channelData = new HashMap<>();


    public ChannelStore(ProgramStore programStore) {
        this.programStore = programStore;
    }

    public ChannelData get(World world, String channel) {
        if(!channelData.containsKey(world)) {
            channelData.put(world, new HashMap<>());
        }

        Map<String, ChannelData> stringChannelDataMap = channelData.get(world);

        if(!stringChannelDataMap.containsKey(channel)) {
            stringChannelDataMap.put(channel, new ChannelData());
        }

        return stringChannelDataMap.get(channel);
    }

    public void saveAll(World world) {
        if(!channelData.containsKey(world)) {
            return;
        }
        Set<String> data = programStore.getUsedChannels(world);

        Map<String, ChannelData> write = new HashMap<>(channelData.get(world));

        write.entrySet().removeIf(a -> !data.contains(a.getKey()));


        File worldsFolder = new File(APIHolder.folder, "worlds");
        worldsFolder.mkdirs();

        File fw = new File(worldsFolder, world+".channeldata");
        try {
            FileOutputStream fos = new FileOutputStream(fw);

            FolderIOUtil.writeInt(fos, VERSION);

            FolderIOUtil.writeInt(fos, write.size());

            write.forEach((key, value) -> {
                try {
                    FolderIOUtil.writeString(fos, key);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    value.write(outputStream);
                    FolderIOUtil.writeByteArray(fos, outputStream.toByteArray());
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

        HashMap<String, ChannelData> value = new HashMap<>();
        channelData.put(world, value);

        File fw = new File(worldsFolder, world+".channeldata");

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
                    ByteArrayInputStream in = new ByteArrayInputStream(FolderIOUtil.readByteArray(inputStreamn));
                    value.put(name, new ChannelData(in));
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
