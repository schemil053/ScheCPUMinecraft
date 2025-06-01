package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.util.ThrowingFunction;
import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.schemilapi.util.ConfigUtil;
import de.emilschlampp.schecpuminecraft.schemilapi.util.SConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HighLangLibraryManager implements ThrowingFunction<String, InputStream, IOException> {
    private static final List<String> INTERNAL_LIBRARIES = Arrays.asList(
            "redstone", "screen"
    );

    private boolean loaded;
    private final Map<String, File> librariesList = new HashMap<>();

    public HighLangLibraryManager() {
        reload();
    }

    public void reload() {
        librariesList.clear();
        SConfig config = ConfigUtil.getConfig("highlanglibs_remap");
        if(loaded) {
            try {
                config.load(config.getFile());
            } catch (Throwable ignored) {

            }
        }
        loaded = true;

        if(!config.getFile().isFile()) {
            config.setDefault("mylib.name", "mylib");
            config.setDefault("mylib.file", "mylib.highlang");
            config.setDefault("mylib.enable", false);
        }

        for (String key : config.getKeys(false)) {
            if(config.isConfigurationSection(key)) {
                ConfigurationSection configurationSection = config.getConfigurationSection(key);

                if(configurationSection != null) {
                    if (configurationSection.getBoolean("enable", false)) {
                        File file = new File(ScheCPUMinecraft.getInstance().getDataFolder(), configurationSection.getString("file", ""));
                        if (file.isFile()) {
                            librariesList.put(configurationSection.getString("name", key), file);
                        } else {
                            ScheCPUMinecraft.getInstance().getLogger().warning("Could not find library "+key+"! (File "+file.getAbsolutePath()+")");
                        }
                    }
                }
            }
        }
    }

    @Override
    public InputStream apply(String input) throws IOException {
        if(librariesList.containsKey(input)) {
            if(!librariesList.get(input).isFile()) {
                return null;
            }
            return Files.newInputStream(librariesList.get(input).toPath());
        }
        return getClass().getResourceAsStream("/lib/"+input+".highlang");
    }

    public List<String> getAllLibraries() {
        List<String> libraries = new ArrayList<>(INTERNAL_LIBRARIES);
        libraries.addAll(librariesList.entrySet().stream().filter(a -> a.getValue().isFile()).map(Map.Entry::getKey).collect(Collectors.toList()));
        return libraries;
    }
}
