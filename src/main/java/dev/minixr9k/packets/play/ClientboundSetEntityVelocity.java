package dev.minixr9k.packets.play;

import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundSetEntityVelocity implements MinecraftPacket {

    private final int entityId;
    private final short velocityX;
    private final short velocityY;
    private final short velocityZ;

    public ClientboundSetEntityVelocity(int entityId, double velX, double velY, double velZ) {
        this.entityId = entityId;
        this.velocityX = (short) (Math.max(-3.9, Math.min(3.9, velX)) * 8000.0);
        this.velocityY = (short) (Math.max(-3.9, Math.min(3.9, velY)) * 8000.0);
        this.velocityZ = (short) (Math.max(-3.9, Math.min(3.9, velZ)) * 8000.0);
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, entityId);
        out.writeShort(velocityX);
        out.writeShort(velocityY);
        out.writeShort(velocityZ);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x5E;
    }
}
