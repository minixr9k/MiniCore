package dev.minixr9k.utils;

import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PacketSendEvent;
import dev.minixr9k.types.Player;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public interface MinecraftPacket {
    void write(ByteBuf out, int protocolVersion);

    void read(ByteBuf in, int protocolVersion);

    int getPacketId(int protocolVersion);

    default void send(ChannelHandlerContext ctx, int protocolVersion, Player player) {

        PacketSendEvent packetSendEvent = new PacketSendEvent(this, player);

        EventBus.getInstance().callEvent(packetSendEvent);

        if (packetSendEvent.isCancelled()) return;

        ByteBuf data = ctx.alloc().buffer();
        try {
            writeVarInt(data, getPacketId(protocolVersion));
            this.write(data, protocolVersion);

            int packetLength = data.readableBytes();

            // Выделяем финальный буфер [Длина] + [Данные]
            ByteBuf packet = ctx.alloc().buffer();

            writeVarInt(packet, packetLength);
            packet.writeBytes(data);

            ctx.writeAndFlush(packet);

        } finally {
            data.release();
        }
    }

    default void send(ChannelHandlerContext ctx, int protocolVersion) {
        send(ctx, protocolVersion, null);
    }
}
