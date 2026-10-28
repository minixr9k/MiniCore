package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.Player;

public class PlayerSneakEvent extends Event implements Cancellable {

    private final Player player;
    private boolean isSneaking;
    private boolean cancelled;

    public PlayerSneakEvent(Player player, boolean isSneaking) {
        this.player = player;
    }

    public Player getPlayer() { return player; }

    public boolean isSneaking() {
        return isSneaking;
    }

    public void setSneaking(boolean sneaking) {
        isSneaking = sneaking;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
