package dev.minixr9k.packets.play;

import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundParticle implements MinecraftPacket {

    private final String type;
    private final boolean longDistance;
    private final boolean alwaysVisible;
    private final double x;
    private final double y;
    private final double z;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final float maxSpeed;
    private final int count;

    public ClientboundParticle(String type, boolean longDistance, boolean alwaysVisible, double x, double y, double z, float offsetX, float offsetY, float offsetZ, float maxSpeed, int count) {
        this.type = type;
        this.longDistance = longDistance;
        this.alwaysVisible = alwaysVisible;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.maxSpeed = maxSpeed;
        this.count = count;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        int particleId = Registry.getId(type, RegistryType.PARTICLES, protocolVersion);

        out.writeBoolean(longDistance);
        out.writeBoolean(alwaysVisible);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(offsetX);
        out.writeFloat(offsetY);
        out.writeFloat(offsetZ);
        out.writeFloat(maxSpeed);
        out.writeInt(count);
        writeVarInt(out, particleId);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x29;
    }
}
