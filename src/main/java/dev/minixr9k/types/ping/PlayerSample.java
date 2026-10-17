package dev.minixr9k.types.ping;

import java.util.UUID;

public class PlayerSample {
    private final String name;
    private final UUID id;

    public PlayerSample(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}