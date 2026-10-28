package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

public class ClientboundSetBorderSize implements MinecraftPacket {

    private final double size;

    public ClientboundSetBorderSize(double size) {
        this.size = size;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeDouble(size);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x53;
    }
}
