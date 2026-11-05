package dev.minixr9k.packets.play;

import dev.minixr9k.chunk.Chunk;
import dev.minixr9k.features.World;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundChunkLightUpdate implements MinecraftPacket {

    private final int chunkX;
    private final int chunkZ;

    public ClientboundChunkLightUpdate(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, chunkX);
        writeVarInt(out, chunkZ);

        Chunk chunk = World.getChunkAt(chunkX, chunkZ);
        byte[][] blockLightSections = (chunk != null) ? chunk.getBlockLightSections() : new byte[24][];

        long skyLightMask = 0;
        long blockLightMask = 0;
        long emptySkyLightMask = 0;
        long emptyBlockLightMask = 0;

        // Включаем Sky Light для всех 34 секций
        for (int i = 0; i < 34; i++) {
            skyLightMask |= (1L << i);
        }

        // Проверяем, какие секции содержат Block Light (индексы с 1 по 24 соответствуют массиву)
        int blockLightArraysCount = 0;
        for (int i = 0; i < 34; i++) {
            // Секции 0 и 33 — технические границы мира, основные игровые секции лежат с 1 по 24
            int sectionIdx = i - 1;
            if (sectionIdx >= 0 && sectionIdx < 24 && blockLightSections[sectionIdx] != null) {
                blockLightMask |= (1L << i);
                blockLightArraysCount++;
            } else {
                emptyBlockLightMask |= (1L << i);
            }
        }

        writeMinecraftBitSet(out, skyLightMask);
        writeMinecraftBitSet(out, blockLightMask);
        writeMinecraftBitSet(out, emptySkyLightMask);
        writeMinecraftBitSet(out, emptyBlockLightMask);

        // --- 1. Пишем массивы SKY LIGHT (34 секции) ---
        writeVarInt(out, 34);
        byte[] fullSkyLight = new byte[2048];
//        Arrays.fill(fullSkyLight, (byte) 0xFF);
        Arrays.fill(fullSkyLight, (byte) 0x00); // 0 — полная темнота
        for (int i = 0; i < 34; i++) {
            writeVarInt(out, 2048);
            out.writeBytes(fullSkyLight);
        }

        // --- 2. Пишем массивы BLOCK LIGHT ---
        writeVarInt(out, blockLightArraysCount);
        for (int i = 0; i < 34; i++) {
            int sectionIdx = i - 1;
            if (sectionIdx >= 0 && sectionIdx < 24 && blockLightSections[sectionIdx] != null) {
                writeVarInt(out, 2048);
                out.writeBytes(blockLightSections[sectionIdx]);
            }
        }
    }

    private void writeMinecraftBitSet(ByteBuf out, long mask) {
        if (mask == 0) {
            writeVarInt(out, 0);
        } else {
            writeVarInt(out, 1);
            out.writeLong(mask);
        }
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {}

    @Override
    public int getPacketId(int protocolVersion) {
        if (protocolVersion >= 773) return 0x2F;
        else if (protocolVersion >= 770) return 0x2A;
        else if (protocolVersion <= 767) return 0x2A;
        else return 0x2B;
    }
}