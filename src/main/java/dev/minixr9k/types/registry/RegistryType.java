package dev.minixr9k.types.registry;

public enum RegistryType {
    PACKETS(""),
    BLOCKS(""),
    ITEMS("minecraft:item"),
    ENTITIES("minecraft:entity_type"),
    EFFECTS("minecraft:mob_effect"),
    ATTRIBUTES("minecraft:attributes"),
    SOUNDS("minecraft:sound_event"),
    DATA_COMPONENTS("minecraft:data_component_type"),
    PARTICLES("minecraft:particle_type")
    ;

    private final String identifier;

    RegistryType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
