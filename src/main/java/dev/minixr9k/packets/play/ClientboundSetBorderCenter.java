package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

public class ClientboundSetBorderCenter implements MinecraftPacket {

    private final double x;
    private final double z;

    public ClientboundSetBorderCenter(double x, double z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeDouble(x);
        out.writeDouble(z);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x51;
    }
}
