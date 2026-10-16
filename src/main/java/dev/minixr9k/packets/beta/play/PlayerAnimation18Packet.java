package dev.minixr9k.packets.beta.play;

import dev.minixr9k.utils.BetaPacket;
import io.netty.buffer.ByteBuf;

public class PlayerAnimation18Packet implements BetaPacket {

    private final int entityId;
    private final int animationId;

    public PlayerAnimation18Packet(int entityId, int animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeInt(entityId);
        out.writeByte(animationId);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x12;
    }
}
