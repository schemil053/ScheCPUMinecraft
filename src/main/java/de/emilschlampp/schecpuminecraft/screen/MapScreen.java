package de.emilschlampp.schecpuminecraft.screen;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

public class MapScreen extends MapRenderer {
    private ScheCPUScreen screen;

    public MapScreen(ScheCPUScreen screen) {
        this.screen = screen;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if(screen != null) {
            player.sendMessage(screen.toString());
            canvas.drawImage(0, 0, screen.getImage());
        }
    }
}
