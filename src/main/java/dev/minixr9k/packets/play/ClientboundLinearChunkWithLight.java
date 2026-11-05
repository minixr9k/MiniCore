package dev.minixr9k.packets.play;

import dev.minixr9k.chunk.Chunk;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class ClientboundLinearChunkWithLight implements MinecraftPacket {
    private final Chunk chunk;

    // Передаем сюда наш готовый объект чанка из ОЗУ
    public ClientboundLinearChunkWithLight(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        out.writeInt(chunk.getX());
        out.writeInt(chunk.getZ());

        // Heightmaps (пустой NBT)
        writeVarInt(out, 0);

        ByteBuf chunkDataBuf = out.alloc().buffer();
        try {
            short[][] sections = chunk.getSections();

            for (int i = 0; i < sections.length; i++) {
                short[] sectionBlocks = sections[i];

                // === Пустая секция ===
                if (sectionBlocks == null) {
                    chunkDataBuf.writeShort(0);           // non-air count
                    chunkDataBuf.writeByte(0);            // bits per entry = 0 (single value palette)
                    chunkDataBuf.writeByte(0);            // block state = air
                    // Биомы
                    chunkDataBuf.writeByte(0);            // bits per entry = 0
                    writeVarInt(chunkDataBuf, 0);         // plains
                    continue;
                }

                // === Собираем палитру ===
                List<Integer> palette = new ArrayList<>();
                palette.add(0); // air всегда на 0 индексе

                int nonAirCount = 0;
                for (short blockId : sectionBlocks) {
                    if (blockId != 0) nonAirCount++;
                    int id = blockId & 0xFFFF; // на всякий случай
                    if (!palette.contains(id)) {
                        palette.add(id);
                    }
                }

                chunkDataBuf.writeShort(nonAirCount);

                int paletteSize = palette.size();
                int bitsPerEntry = calculateBpe(paletteSize);

                // Записываем BPE и саму палитру (Indirect)
                chunkDataBuf.writeByte(bitsPerEntry);
                writeVarInt(chunkDataBuf, paletteSize);
                for (int id : palette) {
                    writeVarInt(chunkDataBuf, id);
                }

                // Упаковываем блоки в соответствии с выбранным BPE
                packBlocks(chunkDataBuf, sectionBlocks, palette, bitsPerEntry);

                // Биомы (пока singular)
                chunkDataBuf.writeByte(0);      // bits per entry = 0
                writeVarInt(chunkDataBuf, 0);   // plains
            }

            // Завершающие байты (по протоколу)
            chunkDataBuf.writeByte(0);
            chunkDataBuf.writeByte(0);
            chunkDataBuf.writeByte(0);
            writeVarInt(chunkDataBuf, 0);

            // Записываем размер и сами данные
            writeVarInt(out, chunkDataBuf.readableBytes());
            out.writeBytes(chunkDataBuf);

        } finally {
            if (chunkDataBuf.refCnt() > 0) chunkDataBuf.release();
        }

        // Block entities
        List<BlockEntityData> blockEntities = collectBlockEntities(chunk);
        writeVarInt(out, blockEntities.size());

        for (BlockEntityData be : blockEntities) {
            // Упаковываем X (4 бита) и Z (4 бита) в один байт, а Y (short) отдельно
            int xz = ((be.localX & 0xF) << 4) | (be.localZ & 0xF);
            out.writeByte(xz);
            out.writeShort(be.worldY);

            // ID типа блок-сущности в реестре (например, сундук = 7 или в зависимости от версии версий)
            // Для совместимости можно использовать VarInt ID типа сундука
            writeVarInt(out, be.typeId);

            // NBT данные (для сундуков обычно достаточно пустого CompoundTag: 0x0A, затем 0x00 для закрытия)
            out.writeByte(0x0A); // TAG_Compound открытие
            out.writeByte(0x00); // Конец тега
        }

        // Light (упрощённо)
        for (int i = 0; i < 4; i++) {
            writeVarInt(out, 1);
            out.writeLong(0L);
        }
        writeVarInt(out, 0); // Sky light arrays
        writeVarInt(out, 0); // Block light arrays
    }

    private record BlockEntityData(int localX, int worldY, int localZ, int typeId) {}

    private List<BlockEntityData> collectBlockEntities(Chunk chunk) {
        List<BlockEntityData> list = new ArrayList<>();
        short[][] sections = chunk.getSections();

        for (int s = 0; s < sections.length; s++) {
            short[] section = sections[s];
            if (section == null) continue;
            int sectionWorldY = (s * 16) - 64;

            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localX = 0; localX < 16; localX++) {
                        int index = (localY << 8) | (localZ << 4) | localX;
                        short blockId = (short) (section[index] & 0xFFFF);

                        // Получаем строковое имя блока через BlockRegistry
                        String name = dev.minixr9k.registries.BlockRegistry.getBlockName(blockId, 763);
                        if (name == null) continue;

                        int typeId = -1;

                        // Сундуки и печки
                        if ((name.contains("chest") && !name.contains("ender_chest")) ||
                                name.contains("ender_chest") ||
                                name.contains("furnace")) {
                            typeId = 7;
                        }
                        // Все виды табличек (обычные, настенные, висячие и т.д.)
                        else if (name.contains("sign") || name.contains("hanging_sign")) {
                            typeId = 7;
                        }
                        // Все виды флагов / баннеров (баннеры на полу и на стенах)
                        else if (name.contains("banner")) {
                            typeId = 7;
                        }

                        if (typeId != -1) {
                            int worldY = sectionWorldY + localY;
                            list.add(new BlockEntityData(localX, worldY, localZ, typeId));
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {}

    @Override
    public int getPacketId(int protocolVersion) {
        if (protocolVersion >= 773) return 0x2C;
        else if (protocolVersion >= 770) return 0x27;
        else if (protocolVersion <= 767) return 0x27;
        else return 0x28;
    }

    private int calculateBpe(int paletteSize) {
        if (paletteSize <= 16) return 4;   // до 16 блоков
        if (paletteSize <= 32) return 5;   // до 32 блоков
        if (paletteSize <= 64) return 6;   // до 64 блоков
        if (paletteSize <= 128) return 7;  // до 128 блоков
        return 8;                          // до 256 блоков
    }

    private void packBlocks(ByteBuf buf, short[] sectionBlocks, List<Integer> palette, int bitsPerEntry) {
        int entriesPerLong = 64 / bitsPerEntry;
        long currentLong = 0;
        int countInLong = 0;

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int index = (y << 8) | (z << 4) | x;
                    int blockId = sectionBlocks[index] & 0xFFFF;

                    long paletteIndex = palette.indexOf(blockId);
                    if (paletteIndex == -1) paletteIndex = 0; // fallback на air

                    currentLong |= (paletteIndex << (countInLong * bitsPerEntry));

                    if (++countInLong == entriesPerLong) {
                        buf.writeLong(currentLong);
                        currentLong = 0;
                        countInLong = 0;
                    }
                }
            }
        }

        // Дописываем остаток, если есть
        if (countInLong > 0) {
            buf.writeLong(currentLong);
        }
    }
}
