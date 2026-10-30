package dev.minixr9k.packets.play;

import dev.minixr9k.config.Configuration;
import dev.minixr9k.types.GameMode;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeString;
import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundRespawn implements MinecraftPacket {

    private final String dimension;
    private final int dimensionType;
    private final GameMode gameMode;
    private final byte dataKept;

    public ClientboundRespawn(String dimension, int dimensionType, GameMode gameMode, byte dataKept) {
        this.dimension = dimension;
        this.dimensionType = dimensionType;
        this.gameMode = gameMode;
        this.dataKept = dataKept;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, dimensionType);
        writeString(out, dimension);

        out.writeLong(0L);       // Hashed Seed
        out.writeByte(gameMode.getId());        // Gamemode (1 - Creative)
        out.writeByte(-1);       // Previous Gamemode
        out.writeBoolean(false); // Is Debug
        out.writeBoolean(Configuration.get().world.flatType); // Is Flat
        out.writeBoolean(false); // Has Death Location
        writeVarInt(out, 0);     // Portal Cooldown
        if (protocolVersion > 767)
            writeVarInt(out, 63);    // Sea Level
        out.writeByte(dataKept);
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x4B;
    }
}
