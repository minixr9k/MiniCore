package dev.minixr9k.packets.play;

import dev.minixr9k.types.Entity;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundSetPassengers implements MinecraftPacket {

    private final int entityId;
    private final List<Entity> passengerId;

    public ClientboundSetPassengers(int entityId, List<Entity> passengerId) {
        this.entityId = entityId;
        this.passengerId = passengerId;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, entityId);

        writeVarInt(out, passengerId.size());
        for (Entity en : passengerId) {
            writeVarInt(out, en.getEntityId());
        }
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        if (protocolVersion > 772)
            return 0x69;
        return 0x64;
    }
}
