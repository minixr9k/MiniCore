package dev.minixr9k.packets.configuration;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeString;

public class ClientboundPluginMessage implements MinecraftPacket {

    private final String channel;
    private final ByteBuf data;

    public ClientboundPluginMessage(String channel, ByteBuf data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {}

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeString(out, channel);
        if (data != null && data.isReadable()) {
            out.writeBytes(data);
            data.release();
        }
    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x01;
    }
}
