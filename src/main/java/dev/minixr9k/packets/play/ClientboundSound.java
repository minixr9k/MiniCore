package dev.minixr9k.packets.play;

import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeString;
import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundSound implements MinecraftPacket {

    private final String sound;
    private final float range;
    private final int category;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final float volume;
    private final float pitch;
    private final long seed;

    public ClientboundSound(String sound, int category, int posX, int posY, int posZ, float volume, float pitch, long seed) {
        this.sound = sound;
        this.range = 0;
        this.category = category;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    public ClientboundSound(String sound, float range, int category, int posX, int posY, int posZ, float volume, float pitch, long seed) {
        this.sound = sound;
        this.range = range;
        this.category = category;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        int soundId = Registry.getId(sound, RegistryType.SOUNDS, protocolVersion) + 1;
        writeVarInt(out, soundId);
        if (soundId == 0) {
            writeString(out, sound);
            if (range == 0)
                out.writeBoolean(false);
            else {
                out.writeBoolean(true);
                out.writeFloat(range);
            }
        }
        writeVarInt(out, category);
        out.writeInt(posX * 8);
        out.writeInt(posY * 8);
        out.writeInt(posZ * 8);
        out.writeFloat(volume);
        out.writeFloat(pitch);
        out.writeLong(seed);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x6E;
    }
}
