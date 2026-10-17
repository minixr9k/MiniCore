package dev.minixr9k.packets.play;

import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundUpdateAttributes implements MinecraftPacket {

    private final int entityId;
    private final String attribute;
    private final double value;

    public ClientboundUpdateAttributes(int entityId, String attribute, double value) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        int attributeId = Registry.getId(attribute, RegistryType.ATTRIBUTES, protocolVersion);
        writeVarInt(out, entityId);

        writeVarInt(out, 1); // array

        writeVarInt(out, attributeId);
        out.writeDouble(value);
        writeVarInt(out, 0); // array
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x7C;
    }
}
