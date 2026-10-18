package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundCloseContainer implements MinecraftPacket {

    private final int windowId;

    public ClientboundCloseContainer(int windowId) {
        this.windowId = windowId;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, windowId);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x11;
    }
}
