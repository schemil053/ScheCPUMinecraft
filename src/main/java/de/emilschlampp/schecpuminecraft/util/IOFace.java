package de.emilschlampp.schecpuminecraft.util;

import org.bukkit.block.BlockFace;

public enum IOFace {
    DOWN,
    UP,
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public BlockFace toBlockFace() {
        switch (this) {
            case UP:
                return BlockFace.UP;
            case DOWN:
                return BlockFace.DOWN;
            case EAST:
                return BlockFace.EAST;
            case WEST:
                return BlockFace.WEST;
            case NORTH:
                return BlockFace.NORTH;
            case SOUTH:
                return BlockFace.SOUTH;
        }
        return null;
    }

    public int getIOConfigID() {
        switch (this) {
            case UP:
                return 142;
            case DOWN:
                return 140;
            case EAST:
                return 144;
            case WEST:
                return 146;
            case NORTH:
                return 148;
            case SOUTH:
                return 150;
        }
        return -1;
    }

    public int getIOValueID() {
        if(getIOConfigID() == -1) {
            return -1;
        }
        return getIOConfigID()+1;
    }
}
