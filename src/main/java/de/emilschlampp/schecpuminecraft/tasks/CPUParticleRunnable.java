package de.emilschlampp.schecpuminecraft.tasks;

import de.emilschlampp.schecpuminecraft.ScheCPUMinecraft;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class CPUParticleRunnable extends BukkitRunnable {
    @Override
    public void run() {
        ScheCPUMinecraft.getInstance().getProgramStore().getPrgStore().forEach(((world, locationProgramBlockDataMap) -> {
            locationProgramBlockDataMap.forEach((location, programBlockData) -> {
                if(location.getChunk().isLoaded()) {
                    world.spawnParticle(Particle.FIREWORKS_SPARK, location.clone().add(0.5, 1, 0.5), 0);
                }
            });
        }));
    }
}
