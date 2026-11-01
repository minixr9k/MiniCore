package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.Player;

public class PlayerMiddleClickEvent extends Event implements Cancellable {

    private final Player player;
    private final int x;
    private final int y;
    private final int z;
    private final boolean includeData;
    private boolean cancelled;

    public PlayerMiddleClickEvent(Player player, int x, int y, int z, boolean includeData) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.includeData = includeData;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isIncludeData() {
        return includeData;
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
