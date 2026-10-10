package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;

public class PacketReceiveEvent extends Event implements Cancellable {

    private final MinecraftPacket packet;
    private final Player player;

    private boolean cancelled;

    public PacketReceiveEvent(MinecraftPacket packet, Player player) {
        this.packet = packet;
        this.player = player;
    }

    public MinecraftPacket getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
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
