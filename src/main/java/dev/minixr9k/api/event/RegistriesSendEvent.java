package dev.minixr9k.api.event;

import dev.minixr9k.api.Event;
import dev.minixr9k.registries.data.MobVariant;
import dev.minixr9k.registries.data.WolfVariant;
import dev.minixr9k.types.Player;

import java.util.List;

public class RegistriesSendEvent extends Event {

    private final Player player;

    private final List<WolfVariant> wolfVariants;
    private final List<MobVariant> catVariants;
    private final List<MobVariant> chickenVariants;
    private final List<MobVariant> cowVariants;
    private final List<MobVariant> frogVariants;
    private final List<MobVariant> pigVariants;
    private final List<MobVariant> zombieNautilusVariants;

    public RegistriesSendEvent(Player player, List<WolfVariant> wolfVariants, List<MobVariant> catVariants, List<MobVariant> chickenVariants, List<MobVariant> cowVariants, List<MobVariant> frogVariants, List<MobVariant> pigVariants, List<MobVariant> zombieNautilusVariants) {
        this.player = player;
        this.wolfVariants = wolfVariants;
        this.catVariants = catVariants;
        this.chickenVariants = chickenVariants;
        this.cowVariants = cowVariants;
        this.frogVariants = frogVariants;
        this.pigVariants = pigVariants;
        this.zombieNautilusVariants = zombieNautilusVariants;
    }

    public Player getPlayer() {
        return player;
    }

    public List<WolfVariant> getWolfVariants() {
        return wolfVariants;
    }

    public List<MobVariant> getCatVariants() {
        return catVariants;
    }

    public List<MobVariant> getChickenVariants() {
        return chickenVariants;
    }

    public List<MobVariant> getCowVariants() {
        return cowVariants;
    }

    public List<MobVariant> getFrogVariants() {
        return frogVariants;
    }

    public List<MobVariant> getPigVariants() {
        return pigVariants;
    }

    public List<MobVariant> getZombieNautilusVariants() {
        return zombieNautilusVariants;
    }
}
