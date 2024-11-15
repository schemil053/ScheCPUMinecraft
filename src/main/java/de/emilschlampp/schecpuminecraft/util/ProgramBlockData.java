package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.emulator.ProcessorEmulator;
import de.emilschlampp.scheCPU.util.EmulatorSandboxRestrictions;
import de.emilschlampp.schecpuminecraft.compiler.CPUCompiler;
import de.emilschlampp.schetty.folderCompress.FileSystem;
import de.emilschlampp.schetty.folderCompress.FolderIOUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.material.Redstone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ProgramBlockData {
    private String source;
    private byte[] compiled;
    private Location location;
    private ProcessorEmulator emulator;
    private FileSystem fileSystem;
    private String broadcastBuffer = "";
    private boolean forceLoaded = false;
    private CodeType codeType = CodeType.SCHESSEMBLER;

    public ProgramBlockData() {
    }

    public ProgramBlockData(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        if (fileSystem.isFileNode("/source")) {
            this.source = new String(fileSystem.getNode("/source").getContent(), StandardCharsets.UTF_8);
        }
        if (fileSystem.isFileNode("/compiled")) {
            this.compiled = fileSystem.getNode("/compiled").getContent();
        }
        if (fileSystem.isFileNode("/state")) {
            try {
                this.emulator = new ProcessorEmulator(fileSystem.getNode("/state").getContent());
            } catch (Throwable throwable) {
                tryToCompileSource(false, false);
            }
        } else {
            tryToCompileSource(false, false);
        }
        if (fileSystem.isFileNode("/broadcastBuffer")) {
            try {
                this.broadcastBuffer = FolderIOUtil.readString(fileSystem.getNode("/broadcastBuffer").openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (fileSystem.isFileNode("/location")) {
            InputStream in = fileSystem.getNode("/location").openStream();
            try {
                this.location = new Location(
                        Bukkit.getWorld(FolderIOUtil.readString(in)),
                        FolderIOUtil.readInt(in),
                        FolderIOUtil.readInt(in),
                        FolderIOUtil.readInt(in)
                );
            } catch (Throwable throwable) {

            }
        }
        if (fileSystem.isFileNode("/forceload")) {
            this.forceLoaded = true;
        }
        if (fileSystem.isFileNode("/codetype")) {
            this.codeType = CodeType.valueOf(fileSystem.getNode("/codetype").openScanner().nextLine());
        }
    }

    public void save() {
        if (this.fileSystem == null) {
            return;
        }
        if (this.source == null) {
            this.fileSystem.getOrCreateFileNode("/source").delete();
        } else {
            this.fileSystem.getOrCreateFileNode("/source").setContent(this.source.getBytes(StandardCharsets.UTF_8));
        }
        if (this.compiled == null) {
            this.fileSystem.getOrCreateFileNode("/compiled").delete();
        } else {
            this.fileSystem.getOrCreateFileNode("/compiled").setContent(this.compiled);
        }
        if (this.emulator == null) {
            this.fileSystem.getOrCreateFileNode("/state").delete();
        } else {
            this.fileSystem.getOrCreateFileNode("/state").setContent(this.emulator.saveState());
        }
        this.fileSystem.getOrCreateFileNode("/broadcastBuffer").delete();
        if (this.broadcastBuffer != null) {
            try {
                FolderIOUtil.writeString(this.fileSystem.getOrCreateFileNode("/broadcastBuffer").openWriter().asStream(), this.broadcastBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.fileSystem.getOrCreateFileNode("/location").delete();
        if (this.location != null) {
            OutputStream out = this.fileSystem.getOrCreateFileNode("/location").openWriter().asStream();
            try {
                FolderIOUtil.writeString(out, this.location.getWorld().getName());
                FolderIOUtil.writeInt(out, this.location.getBlockX());
                FolderIOUtil.writeInt(out, this.location.getBlockY());
                FolderIOUtil.writeInt(out, this.location.getBlockZ());
            } catch (Throwable throwable) {

            }
        }
        if (this.forceLoaded) {
            this.fileSystem.getOrCreateFileNode("/forceload");
        } else {
            this.fileSystem.getOrCreateFileNode("/forceload").delete();
        }
        if (this.codeType != null) {
            this.fileSystem.getOrCreateFileNode("/codetype").delete();
            this.fileSystem.getOrCreateFileNode("/codetype").openWriter().println(this.codeType.name());
        } else {
            this.fileSystem.getOrCreateFileNode("/codetype").delete();
        }

        this.fileSystem.saveAll();
    }


    public void tryToCompileSource(boolean force, boolean allowThrow) {
        if (compiled == null || force) {
            if (source != null) {
                try {
                    compiled = CPUCompiler.compile(this.codeType, source);
                } catch (Throwable throwable) {
                    if (allowThrow) {
                        throw throwable;
                    }
                }
            }
        }
        if (compiled != null) {
            emulator = new ProcessorEmulator(512, 128, compiled).setRestrictions(new EmulatorSandboxRestrictions().setAllowOutput(RuntimeValues.CPU$ALLOW_SYSOUT));
        }
    }

    public String getSource() {
        return source;
    }

    public ProgramBlockData setSource(String source) {
        this.source = source;
        return this;
    }

    public byte[] getCompiled() {
        return compiled;
    }

    public ProgramBlockData setCompiled(byte[] compiled) {
        this.compiled = compiled;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public ProgramBlockData setLocation(Location location) {
        this.location = location;
        return this;
    }

    public ProcessorEmulator getEmulator() {
        return emulator;
    }

    public ProgramBlockData setEmulator(ProcessorEmulator emulator) {
        this.emulator = emulator;
        return this;
    }

    public void tick() {
        if (emulator == null) {
            return;
        }
        try {
            int cmds = 1;
            if (emulator.getIo()[128] != 0) {
                cmds = emulator.getIo()[129];
            }

            cmds = Math.min(cmds, RuntimeValues.CPU$MAX_MULTI_EXEC);

            for (int i = 0; i < cmds; i++) {
                if (emulator.canExecute()) {
                    updateIOIn();
                    emulator.execute();
                    updateIOOut();
                }
            }
        } catch (Throwable throwable) {
            if(RuntimeValues.CPU$PRINT_ERROR) {
                throwable.printStackTrace();
            }
            if(RuntimeValues.CPU$DISABLE_ON_ERROR) {
                emulator.setJmp(emulator.getInstructions().length+1);
            }
        }
    }

    private void updateIOIn() {
        if (location != null) {
            for (IOFace value : IOFace.values()) {
                int downWire = emulator.getIo()[value.getIOConfigID()];
                if (downWire == 0) {
                    emulator.getIo()[value.getIOValueID()] = isActive(location.getBlock().getRelative(value.toBlockFace())) ? 1 : 0;
                }
                if (downWire == 2) {
                    emulator.getIo()[value.getIOValueID()] = getLevel(location.getBlock().getRelative(value.toBlockFace()));
                }
            }
        }
    }

    private boolean isActive(Block block) {
        if (block.getBlockData() instanceof RedstoneWire) {
            return ((RedstoneWire) block.getBlockData()).getPower() > 0;
        }
        if (block.getBlockData() instanceof Redstone) {
            return ((Redstone) block.getBlockData()).isPowered();
        }
        return false;
    }

    private int getLevel(Block block) {
        if (block.getBlockData() instanceof RedstoneWire) {
            return ((RedstoneWire) block.getBlockData()).getPower();
        }
        if (block.getBlockData() instanceof Redstone) {
            return ((Redstone) block.getBlockData()).isPowered() ? 15 : 0;
        }
        return 0;
    }

    private void setState(Block block, int state) {
        if (block.getBlockData() instanceof RedstoneWire) {
            RedstoneWire wire = (RedstoneWire) block.getBlockData();
            wire.setPower(state);
            block.setBlockData(wire);
        }
    }

    private void updateIOOut() {
        if(emulator.getIo()[131] == 0) {
            broadcastBuffer = "";
            emulator.getIo()[131] = 1;
        }
        if (emulator.getIo()[130] != 0) {
            if (location != null) {
                if(broadcastBuffer.length() < 255) {
                    broadcastBuffer += ((char) emulator.getIo()[130]);
                }
                if (broadcastBuffer.endsWith("\n")) {
                    String msg = "§bCPU: " + broadcastBuffer;
                    Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().equals(location.getWorld())).filter(p -> p.getLocation().distance(location) < 25).forEach(p -> {
                        p.sendMessage(msg);
                    });
                    broadcastBuffer = "";
                }
                emulator.getIo()[130] = 0;
            }
        }
        for (IOFace value : IOFace.values()) {
            int downWire = emulator.getIo()[value.getIOConfigID()];
            if (downWire == 1) {
                setState(location.getBlock().getRelative(value.toBlockFace()), emulator.getIo()[value.getIOValueID()] > 0 ? 15 : 0);
            }
            if (downWire == 3) {
                setState(location.getBlock().getRelative(value.toBlockFace()), emulator.getIo()[value.getIOValueID()]);
            }
        }
    }

    public boolean isForceLoaded() {
        return false;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public ProgramBlockData setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        return this;
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public ProgramBlockData setCodeType(CodeType codeType) {
        this.codeType = codeType;
        return this;
    }

    public ProgramBlockData setForceLoaded(boolean forceLoaded) {
        this.forceLoaded = forceLoaded;
        return this;
    }

    public String getBroadcastBuffer() {
        return broadcastBuffer;
    }

    public ProgramBlockData setBroadcastBuffer(String broadcastBuffer) {
        this.broadcastBuffer = broadcastBuffer;
        return this;
    }
}
