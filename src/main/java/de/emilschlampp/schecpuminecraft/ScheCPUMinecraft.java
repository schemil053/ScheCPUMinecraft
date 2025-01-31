package de.emilschlampp.schecpuminecraft;

import de.emilschlampp.scheCPU.compile.Compiler;
import de.emilschlampp.scheCPU.dissassembler.Decompiler;
import de.emilschlampp.scheCPU.emulator.Instruction;
import de.emilschlampp.scheCPU.emulator.ProcessorEmulator;
import de.emilschlampp.schecpuminecraft.commands.ScheCPUCommand;
import de.emilschlampp.schecpuminecraft.commands.sub.*;
import de.emilschlampp.schecpuminecraft.listener.CPUMainListener;
import de.emilschlampp.schecpuminecraft.schemilapi.APIHolder;
import de.emilschlampp.schecpuminecraft.schemilapi.util.ModuleType;
import de.emilschlampp.schecpuminecraft.tasks.CPUParticleRunnable;
import de.emilschlampp.schecpuminecraft.tasks.CPURunnable;
import de.emilschlampp.schecpuminecraft.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScheCPUMinecraft extends JavaPlugin {
    private static ScheCPUMinecraft instance;

    private ProgramStore programStore;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        APIHolder.init(this);

        APIHolder.enableModule(ModuleType.PAGED_INVENTORY);
        APIHolder.enableModule(ModuleType.SIMPLE_INVENTORY);

        RuntimeValues.load();

        try {
            Class.forName(Compiler.class.getName());
            Class.forName(ProgramStore.class.getName());
            Class.forName(ProgramBlockData.class.getName());
            Class.forName(Decompiler.class.getName());
            Class.forName(ProcessorEmulator.class.getName());
            Class.forName(IOFace.class.getName());
            Class.forName(Instruction.class.getName());
        } catch (Throwable throwable) {
            new DebugTest(this);
            return;
        }

        getCommand("schecpu").setExecutor(new ScheCPUCommand()
                .register(new CPUExamplesCommand())
                .register(new GetCodingBookCommand())
                .register(new GetCPUBlockCommand())
                .register(new GetCPUControlCommand())
                .register(new ListCPUCommand())
        );

        getServer().getPluginManager().registerEvents(new CPUMainListener(), this);

        new DebugTest(this);

        programStore = new ProgramStore();

        for (World world : Bukkit.getWorlds()) {
            programStore.load(world);
        }

        new CPURunnable().runTaskTimer(this, 1, 1);
        if(RuntimeValues.PARTICLES) {
            new CPUParticleRunnable().runTaskTimerAsynchronously(this, 15, 15);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for (World world : Bukkit.getWorlds()) {
            programStore.saveAll(world);
        }
    }

    public ProgramStore getProgramStore() {
        return programStore;
    }

    public static ScheCPUMinecraft getInstance() {
        return instance;
    }
}
