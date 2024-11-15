package de.emilschlampp.schecpuminecraft.tasks;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import org.bukkit.scheduler.BukkitRunnable;

public class CPURunnable extends BukkitRunnable {
    @Override
    public void run() {
        ScheCPUMinecraft.getInstance().getProgramStore().getPrgStore().forEach(((world, locationProgramBlockDataMap) -> {
            locationProgramBlockDataMap.forEach((location, programBlockData) -> {
                if(location.getChunk().isLoaded() || programBlockData.isForceLoaded()) {
                    programBlockData.tick();
                }
            });
        }));
    }
}
