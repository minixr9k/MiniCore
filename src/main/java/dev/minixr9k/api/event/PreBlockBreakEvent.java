package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.Block;
import dev.minixr9k.types.Player;

public class PreBlockBreakEvent extends Event implements Cancellable {

    private final Player player;
    private final Block block;
    private final int status;
    private final byte face;
    private final int sequence;
    private boolean cancelled;

    public PreBlockBreakEvent(Player player, Block block, int status, byte face, int sequence) {
        this.player = player;
        this.block = block;
        this.status = status;
        this.face = face;
        this.sequence = sequence;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public int getStatus() {
        return status;
    }

    public byte getFace() {
        return face;
    }

    public int getSequence() {
        return sequence;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

}
