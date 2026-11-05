package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;
import static dev.minixr9k.utils.ProtocolUtils.writeVarLong;

public class ClientboundUpdateTime implements MinecraftPacket {

    private final long worldAge;
    private final long time;
    private final boolean isTimeIncreasing;

    public ClientboundUpdateTime(long worldAge, long time, boolean isTimeIncreasing) {
        this.worldAge = worldAge;
        this.time = time;
        this.isTimeIncreasing = isTimeIncreasing;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeLong(worldAge);
        out.writeLong(time);
        out.writeBoolean(isTimeIncreasing);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        if (protocolVersion >= 773)
            return 0x6F;
        return 0x6A;
    }
}
