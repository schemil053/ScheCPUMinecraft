package de.emilschlampp.schecpuminecraft.listener;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import de.emilschlampp.schecpuminecraft.compiler.CPUCompiler;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.InventoryUtil;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.ItemBuilder;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI.SimpleButton;
import de.emilschlampp.schecpuminecraft.schemilapi.inventory.simpleGUI.SimpleGUI;
import de.emilschlampp.schecpuminecraft.util.CodeType;
import de.emilschlampp.schecpuminecraft.util.IOFace;
import de.emilschlampp.schecpuminecraft.util.ProgramBlockData;
import de.emilschlampp.schecpuminecraft.util.ProgramStore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class CPUMainListener implements Listener {
    private final Map<UUID, Long> uuidLongMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCPUPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }
        if(!event.getItemInHand().isSimilar(new ItemBuilder(Material.GOLD_BLOCK).setDisplayName("§cCPU").build())) {
            return;
        }
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        if(programStore.getForBlock(event.getBlock()) == null) {
            programStore.putNewBlock(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        if(programStore.getForBlock(event.getBlock()) != null) {
            programStore.deleteCPU(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak2(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        if(programStore.getForBlock(event.getBlock()) != null) {
            programStore.deleteCPU(event.getBlock());
            if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.setDropItems(false);
                Bukkit.getScheduler().runTaskLater(ScheCPUMinecraft.getInstance(), () -> {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0, 0.5), new ItemBuilder(Material.GOLD_BLOCK).setDisplayName("§cCPU").build());
                }, 1);
            }
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        for (IOFace value : IOFace.values()) {
            ProgramBlockData blockData = programStore.getForBlock(event.getBlock().getRelative(value.toBlockFace().getOppositeFace()));
            if(blockData == null) {
                continue;
            }
            if(blockData.getEmulator() == null) {
                continue;
            }
            if(blockData.getEmulator().getIo()[value.getIOConfigID()] != 1 && blockData.getEmulator().getIo()[value.getIOConfigID()] != 3) {
                continue;
            }
            event.setNewCurrent(event.getOldCurrent());
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract2(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.isCancelled()) {
            return;
        }
        if(event.getItem() == null) {
            return;
        }
        if(!event.getItem().isSimilar(new ItemBuilder(Material.STICK).setDisplayName("§cCPU-Bedienfeld").build())) {
            return;
        }

        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        if(programStore.getForBlock(event.getClickedBlock()) == null) {
            return;
        }

        SimpleGUI gui = new SimpleGUI("§cSteuerung", 3);
        gui.setButton(10, new SimpleButton("§cHerunterfahren ⏹", Material.RED_DYE, inventoryClickEvent -> {
            ProgramBlockData data = programStore.getForBlock(event.getClickedBlock());
            if(data == null) {
                return;
            }
            if(data.getEmulator() != null) {
                Arrays.fill(data.getEmulator().getIo(), 0);
                Arrays.fill(data.getEmulator().getMemory(), 0);
                Arrays.fill(data.getEmulator().getRegister(), 0);

                data.getEmulator().setJmp(data.getEmulator().getInstructions().length+1);
            }
        }));
        gui.setButton(12, new SimpleButton("§cProgrammiersprache ändern", Material.CLOCK, inventoryClickEvent -> {
            ProgramBlockData data = programStore.getForBlock(event.getClickedBlock());
            if(data == null) {
                return;
            }
            SimpleGUI programGUI = new SimpleGUI("§cProgrammiersprache", 3);
            programGUI.setButton(11, new SimpleButton((data.getCodeType() == CodeType.BASE64ASM ? "§a" : "§c")+"Base64 (Compiled Schessembler)", Material.REDSTONE_LAMP, inventoryClickEvent1 -> {
                data.setCodeType(CodeType.BASE64ASM);

                ProgramBlockData fdata = programStore.getForBlock(event.getClickedBlock());
                if(fdata == null) {
                    return;
                }
                gui.open(event.getPlayer());
            }));
            programGUI.setButton(13, new SimpleButton((data.getCodeType() == CodeType.SCHESSEMBLER ? "§a" : "§c")+"Schessembler", Material.END_CRYSTAL, inventoryClickEvent1 -> {
                data.setCodeType(CodeType.SCHESSEMBLER);

                ProgramBlockData fdata = programStore.getForBlock(event.getClickedBlock());
                if(fdata == null) {
                    return;
                }
                gui.open(event.getPlayer());
            }));
            programGUI.setButton(15, new SimpleButton((data.getCodeType() == CodeType.HIGHLANG ? "§a" : "§c")+"Highlang", Material.COMPARATOR, inventoryClickEvent1 -> {
                data.setCodeType(CodeType.HIGHLANG);

                ProgramBlockData fdata = programStore.getForBlock(event.getClickedBlock());
                if(fdata == null) {
                    return;
                }
                gui.open(event.getPlayer());
            }));
            programGUI.open(event.getPlayer());
        }));
        gui.setButton(14, new SimpleButton("§aNeustarten \uD83D\uDD01", Material.GREEN_DYE, inventoryClickEvent -> {
            ProgramBlockData data = programStore.getForBlock(event.getClickedBlock());
            if(data == null) {
                return;
            }
            if(data.getEmulator() != null) {
                Arrays.fill(data.getEmulator().getIo(), 0);
                Arrays.fill(data.getEmulator().getMemory(), 0);
                Arrays.fill(data.getEmulator().getRegister(), 0);

                data.getEmulator().setJmp(0);
            }
        }));
        gui.setButton(16, new SimpleButton("§6Hardware", Material.REDSTONE_LAMP, inventoryClickEvent -> {
            SimpleGUI hwGUI = new SimpleGUI("§aHardware", 3);

            ProgramBlockData fdata = programStore.getForBlock(event.getClickedBlock());

            if(fdata == null) {
                return;
            }


            hwGUI.setButton(13, new SimpleButton(InventoryUtil.createItem(Material.REPEATER, "§6CPU-Kommunikation", "§aAktueller Kanal: "+fdata.getCommunicationChannel()), i2 -> {
                event.getPlayer().closeInventory();
                ScheCPUMinecraft.getInstance().getPromptManager().prompt(event.getPlayer(), fdata::setCommunicationChannel);
            }));

            hwGUI.open(event.getPlayer());
        }));
        gui.open(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.isCancelled()) {
            return;
        }
        if(!event.getPlayer().hasPermission("schecpuminecraft.action.getbook")) {
            return;
        }
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        if(event.getItem() == null) {
            if(!event.getPlayer().isSneaking()) {
                return;
            } else {
                ProgramBlockData programBlockData = programStore.getForBlock(event.getClickedBlock());
                if(programBlockData != null) {
                    if(uuidLongMap.containsKey(event.getPlayer().getUniqueId())) {
                        if(uuidLongMap.get(event.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                            return;
                        }
                    }
                    uuidLongMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis()+500);

                    String src = programBlockData.getSource();
                    if (src == null && programBlockData.getCompiled() != null) {
                        src = CPUCompiler.decompile(programBlockData.getCompiled());
                        programBlockData.setSource(src).setCodeType(CodeType.SCHESSEMBLER);
                    }

                    if (src == null) {
                        event.getPlayer().sendMessage("§cKein Programm gefunden.");
                        return;
                    }

                    List<String> pages = getBookContent(src);

                    event.getPlayer().getInventory().addItem(
                            new ItemBuilder(Material.WRITABLE_BOOK)
                                    .setDisplayName("§cCode-Editor")
                                    .setPages(pages.toArray(new String[0]))
                                    .build()
                    );

                }
                return;
            }
        }
        if(!event.getItem().hasItemMeta()) {
            return;
        }
        if(!(event.getItem().getItemMeta() instanceof BookMeta)) {
            return;
        }
        String code = "";
        String[] p = ((BookMeta) event.getItem().getItemMeta()).getPages().toArray(new String[0]);

        for (String s : p) {
            code+="\n"+s;
        }

        if(!code.isEmpty()) {
            code = code.substring(1);
        }

        ProgramBlockData programBlockData = programStore.getForBlock(event.getClickedBlock());

        if(programBlockData == null) {
            return;
        }

        programBlockData.setSource(code);
        try {
            programBlockData.tryToCompileSource(true, true);
            event.getPlayer().sendMessage("§aCompiled.");
        } catch (Throwable throwable) {
            event.getPlayer().sendMessage("§cFehler: "+throwable.getMessage());
            if(throwable.getCause() != null) {
                event.getPlayer().sendMessage("§eDesc: "+throwable.getCause());
                //throwable.getCause().printStackTrace();
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        event.blockList().removeIf(b -> programStore.getForBlock(b) != null);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        ProgramStore programStore = ScheCPUMinecraft.getInstance().getProgramStore();
        event.blockList().removeIf(b -> programStore.getForBlock(b) != null);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        ScheCPUMinecraft.getInstance().getProgramStore().load(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        ScheCPUMinecraft.getInstance().getProgramStore().unload(event.getWorld());
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        ScheCPUMinecraft.getInstance().getProgramStore().saveAll(event.getWorld());
    }


    public static List<String> getBookContent(String src) {
        int maxCharactersPerPage = 256;
        List<String> pages = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        /*for (String line : src.split("\n", -1)) {
            if (current.length() + line.length() + 1 > maxCharactersPerPage || current.toString().split("\n", -1).length > 14) {
                pages.add(current.toString());
                current.setLength(0);
            }
            if (current.length() > 0) {
                current.append("\n");
            }
            current.append(line);
        }*/
        for(String line : src.split("\n", -1)) {

            if (current.length() > 0) {
                current.append("\n");
            }

            String[] sp = current.toString().split("\n");
            if(sp.length >= 12) {
                pages.add(current.toString());
                current.setLength(0);
            }
            int lines = 0;
            for (String s : sp) {
                lines++;
                if(s.length() >= 19) {
                    lines++;
                }
                int c = 0;
                for (String string : s.split(" ")) {
                    if(c + string.length()+1 > 20) {
                        lines++;
                        c=0;
                    }
                    c += string.length()+1;
                }
            }
            if(lines >= 14) {
                pages.add(current.toString());
                current.setLength(0);
            }

            current.append(line);
        }

        if (current.length() > 0) {
            pages.add(current.toString());
        }
        return pages;
    }
}
