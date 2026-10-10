package dev.minixr9k.network.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PlayerKeepAliveEvent;
import dev.minixr9k.packets.play.ClientboundKeepAlive;
import dev.minixr9k.scheduler.CoreTask;
import dev.minixr9k.types.Player;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class KeepAliveHandler {

    private final Player player;

    private long lastKeepAliveSentTime;
    private long lastKeepAliveId;
    private boolean keepAlivePending = false;

    private CoreTask keepAliveTickTask;
    private CoreTask keepAliveTimeoutTask;

    public KeepAliveHandler(Player player) {
        this.player = player;
    }

    public void startKeepAliveLoop(ChannelHandlerContext ctx) {
        this.keepAliveTickTask = MiniCore.getScheduler().runTaskTimer(task -> {
            if (!ctx.channel().isActive()) {
                task.cancel();
                return;
            }

            this.lastKeepAliveId = System.currentTimeMillis();
            EventBus.getInstance().callEvent(new PlayerKeepAliveEvent(player));
            new ClientboundKeepAlive().send(ctx, player.getProtocolVersion(), player);
            this.keepAlivePending = true;
            this.lastKeepAliveSentTime = System.currentTimeMillis();
        }, 100L, 300L);

        this.keepAliveTimeoutTask = MiniCore.getScheduler().runTaskTimer(task -> {
            if (!ctx.channel().isActive()) {
                task.cancel();
                return;
            }

            if (keepAlivePending && (System.currentTimeMillis() - lastKeepAliveSentTime > 30_000)) {
                this.keepAlivePending = false;
                System.out.println("[Core/Play] " + player.getUsername() + " disconnected (keepalive timeout)");
                ctx.close();
                task.cancel();
            }
        }, 20L, 20L);
    }

    public void handleKeepAliveResponse(ByteBuf in) {
        long timestamp = in.readLong();
        player.setPing((int) (System.currentTimeMillis() - timestamp));
        if (timestamp == lastKeepAliveId) {
            this.keepAlivePending = false;
        }
    }

    public void closeTasks() {
        if (keepAliveTickTask != null) {
            keepAliveTickTask.cancel();
            keepAliveTickTask = null;
        }
        if (keepAliveTimeoutTask != null) {
            keepAliveTimeoutTask.cancel();
            keepAliveTimeoutTask = null;
        }
    }
}
