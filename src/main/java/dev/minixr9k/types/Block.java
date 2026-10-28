package dev.minixr9k.types;

import dev.minixr9k.features.World;

public class Block {

    private final String type;
    private final Location location;

    public Block(String type, Location location) {
        this.type = type;
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String newType) {
        World.placeBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), newType);
    }

    public Location getLocation() {
        return location;
    }
}
