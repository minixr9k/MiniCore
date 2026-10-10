package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

public class ClientboundUnloadChunk implements MinecraftPacket {

    private final int chunkX;
    private final int chunkZ;

    public ClientboundUnloadChunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeInt(chunkZ);
        out.writeInt(chunkX);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x21;
    }
}
