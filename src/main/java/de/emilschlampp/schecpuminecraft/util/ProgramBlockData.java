package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.emulator.ProcessorEmulator;
import de.emilschlampp.scheCPU.util.EmulatorSandboxRestrictions;
import de.emilschlampp.scheCPU.util.FolderIOUtil;
import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.compiler.CPUCompiler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.material.Redstone;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ProgramBlockData {
    public static final int VERSION = 2;
    private String source;
    private byte[] compiled;
    private Location location;
    private ProcessorEmulator emulator;
    private File file;
    private String broadcastBuffer = "";
    private String communicationChannel = "";
    private boolean forceLoaded = false;
    private CodeType codeType = CodeType.SCHESSEMBLER;

    public ProgramBlockData() {
    }

    public ProgramBlockData(File file) {
        this.file = file;
        try(GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
            int version = FolderIOUtil.readInt(inputStream);

            if(version == 1 || version == 2) {
                if (FolderIOUtil.readBoolean(inputStream)) {
                    this.source = new String(FolderIOUtil.readByteArray(inputStream), StandardCharsets.UTF_8);
                }
                if (FolderIOUtil.readBoolean(inputStream)) {
                    this.compiled = FolderIOUtil.readByteArray(inputStream);
                }
                if (FolderIOUtil.readBoolean(inputStream)) {
                    try {
                        this.emulator = new ProcessorEmulator(FolderIOUtil.readByteArray(inputStream));
                    } catch (Throwable throwable) {
                        tryToCompileSource(false, false);
                    }
                } else {
                    tryToCompileSource(false, false);
                }
                if (FolderIOUtil.readBoolean(inputStream)) {
                    try {
                        this.broadcastBuffer = FolderIOUtil.readString(inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (FolderIOUtil.readBoolean(inputStream)) {
                    InputStream in = new ByteArrayInputStream(FolderIOUtil.readByteArray(inputStream));
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
                this.forceLoaded = FolderIOUtil.readBoolean(inputStream);
                if (FolderIOUtil.readBoolean(inputStream)) {
                    this.codeType = CodeType.valueOf(FolderIOUtil.readString(inputStream));
                }

                if(version == 2) {
                    communicationChannel = FolderIOUtil.readString(inputStream);
                } else {
                    communicationChannel = "";
                }
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void save() {
        if (this.file == null) {
            return;
        }
        try (GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(file))) {
            FolderIOUtil.writeInt(outputStream, VERSION);
            FolderIOUtil.writeBoolean(outputStream, this.source != null);
            if (this.source != null) {
                FolderIOUtil.writeByteArray(outputStream, this.source.getBytes(StandardCharsets.UTF_8));
            }

            FolderIOUtil.writeBoolean(outputStream, this.compiled != null);
            if (this.compiled != null) {
                FolderIOUtil.writeByteArray(outputStream, this.compiled);
            }

            FolderIOUtil.writeBoolean(outputStream, this.emulator != null);
            if (this.emulator != null) {
                FolderIOUtil.writeByteArray(outputStream, this.emulator.saveState());
            }

            FolderIOUtil.writeBoolean(outputStream, this.broadcastBuffer != null);
            if (this.broadcastBuffer != null) {
                try {
                    FolderIOUtil.writeString(outputStream, this.broadcastBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            FolderIOUtil.writeBoolean(outputStream, this.location != null);
            if (this.location != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    FolderIOUtil.writeString(out, this.location.getWorld().getName());
                    FolderIOUtil.writeInt(out, this.location.getBlockX());
                    FolderIOUtil.writeInt(out, this.location.getBlockY());
                    FolderIOUtil.writeInt(out, this.location.getBlockZ());
                } catch (Throwable throwable) {

                }
                FolderIOUtil.writeByteArray(outputStream, out.toByteArray());
            }

            FolderIOUtil.writeBoolean(outputStream, this.forceLoaded);

            FolderIOUtil.writeBoolean(outputStream, this.codeType != null);
            if (this.codeType != null) {
                FolderIOUtil.writeString(outputStream, this.codeType.name());
            }

            FolderIOUtil.writeString(outputStream, communicationChannel);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
                int ioPortValue = emulator.getIo()[value.getIOConfigID()];
                if (ioPortValue == 0) {
                    emulator.getIo()[value.getIOValueID()] = isActive(location.getBlock().getRelative(value.toBlockFace())) ? 1 : 0;
                }
                if (ioPortValue == 2) {
                    emulator.getIo()[value.getIOValueID()] = getLevel(location.getBlock().getRelative(value.toBlockFace()));
                }
            }

            if(!communicationChannel.isEmpty()) {
                ChannelData channelData = ScheCPUMinecraft.getInstance().getProgramStore().getChannelStore().get(getLocation().getWorld(), communicationChannel);

                int[] data = channelData.getData();
                int[] io = emulator.getIo();

                System.arraycopy(data, 0, io, 160, data.length);
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
        if(emulator.getIo()[131] != 0) {
            broadcastBuffer = "";
            emulator.getIo()[131] = 0;
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

        if(!communicationChannel.isEmpty()) {
            ChannelData channelData = ScheCPUMinecraft.getInstance().getProgramStore().getChannelStore().get(getLocation().getWorld(), communicationChannel);

            int[] data = channelData.getData();
            int[] io = emulator.getIo();

            System.arraycopy(io, 160, data, 0, data.length);
        }
    }

    public boolean isForceLoaded() {
        return false; //TODO 22.03.2025 Add way to perma load cpus
    }

    public File getFile() {
        return file;
    }

    public ProgramBlockData setFile(File file) {
        this.file = file;
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

    public String getCommunicationChannel() {
        return communicationChannel;
    }

    public ProgramBlockData setCommunicationChannel(String communicationChannel) {
        this.communicationChannel = communicationChannel;
        return this;
    }
}
