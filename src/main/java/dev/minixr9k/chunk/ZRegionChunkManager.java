package dev.minixr9k.chunk;

import com.github.luben.zstd.Zstd;
import dev.minixr9k.MiniCore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ZRegionChunkManager {

    private static final int REGION_SIZE = 32;
    private static final int CHUNKS_PER_REGION = REGION_SIZE * REGION_SIZE;

    public static void saveWorld(Map<String, Chunk> chunks, File saveDir) throws IOException {
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        Map<String, Map<String, Chunk>> regionsMap = new HashMap<>();

        for (Chunk chunk : chunks.values()) {
            int rx = chunk.getX() >> 5;
            int rz = chunk.getZ() >> 5;
            String regionKey = rx + "," + rz;

            regionsMap.computeIfAbsent(regionKey, k -> new HashMap<>())
                    .put(chunk.getX() + "," + chunk.getZ(), chunk);
        }

        for (Map.Entry<String, Map<String, Chunk>> entry : regionsMap.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);

            File regionFile = new File(saveDir, "r." + rx + "." + rz + ".zregion");
            saveRegion(regionFile, rx, rz, entry.getValue());
        }
    }

    private static void saveRegion(File file, int rx, int rz, Map<String, Chunk> regionChunks) throws IOException {
        ByteBuf headerBuf = Unpooled.buffer(CHUNKS_PER_REGION * 4);
        ByteBuf uncompressedDataBuf = Unpooled.buffer();

        int[] offsets = new int[CHUNKS_PER_REGION];

        try {
            int currentOffset = 0;

            for (int cz = 0; cz < REGION_SIZE; cz++) {
                for (int cx = 0; cx < REGION_SIZE; cx++) {
                    int globalCX = (rx << 5) + cx;
                    int globalCZ = (rz << 5) + cz;
                    int index = cz * REGION_SIZE + cx;

                    Chunk chunk = regionChunks.get(globalCX + "," + globalCZ);

                    if (chunk == null) {
                        offsets[index] = -1;
                        continue;
                    }

                    offsets[index] = currentOffset;
                    int startIdx = uncompressedDataBuf.writerIndex();

                    uncompressedDataBuf.writeInt(chunk.getX());
                    uncompressedDataBuf.writeInt(chunk.getZ());
                    uncompressedDataBuf.writeBoolean(chunk.isEmpty());

                    // 1. Сохраняем блоки секций
                    short[][] sections = chunk.getSections();
                    for (int i = 0; i < 24; i++) {
                        short[] section = sections[i];
                        if (section == null) {
                            uncompressedDataBuf.writeBoolean(false);
                        } else {
                            uncompressedDataBuf.writeBoolean(true);
                            for (short block : section) {
                                uncompressedDataBuf.writeShort(block);
                            }
                        }
                    }

                    // 2. Сохраняем свет секций (Block Light)
                    byte[][] blockLightSections = chunk.getBlockLightSections();
                    for (int i = 0; i < 24; i++) {
                        byte[] lightSec = blockLightSections[i];
                        if (lightSec == null) {
                            uncompressedDataBuf.writeBoolean(false);
                        } else {
                            uncompressedDataBuf.writeBoolean(true);
                            uncompressedDataBuf.writeBytes(lightSec);
                        }
                    }

                    currentOffset += (uncompressedDataBuf.writerIndex() - startIdx);
                }
            }

            for (int offset : offsets) {
                headerBuf.writeInt(offset);
            }

            byte[] headerBytes = new byte[headerBuf.readableBytes()];
            headerBuf.readBytes(headerBytes);

            byte[] uncompressedBytes = new byte[uncompressedDataBuf.readableBytes()];
            uncompressedDataBuf.readBytes(uncompressedBytes);

            byte[] compressedBytes = Zstd.compress(uncompressedBytes, 3);

            byte[] finalFileBytes = new byte[headerBytes.length + compressedBytes.length];
            System.arraycopy(headerBytes, 0, finalFileBytes, 0, headerBytes.length);
            System.arraycopy(compressedBytes, 0, finalFileBytes, headerBytes.length, compressedBytes.length);

            Files.write(file.toPath(), finalFileBytes);
            MiniCore.getLogger().info("[Core/World] Успешно сохранен регион: " + file.getName() + " | Размер: " + finalFileBytes.length + " байт");

        } finally {
            headerBuf.release();
            uncompressedDataBuf.release();
        }
    }

    public static Map<String, Chunk> loadWorld(File saveDir) throws IOException {
        Map<String, Chunk> loadedChunks = new HashMap<>();
        if (!saveDir.exists()) return loadedChunks;

        File[] files = saveDir.listFiles((dir, name) -> name.startsWith("r.") && name.endsWith(".zregion"));
        if (files == null) return loadedChunks;

        for (File file : files) {
            Map<String, Chunk> regionChunks = loadRegion(file);
            loadedChunks.putAll(regionChunks);
        }

        return loadedChunks;
    }

    private static Map<String, Chunk> loadRegion(File file) throws IOException {
        Map<String, Chunk> regionChunks = new HashMap<>();
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        if (fileBytes.length < 4096) {
            throw new IOException("Файл ZRegion региона слишком мал или поврежден: " + file.getName());
        }

        ByteBuf headerBuf = Unpooled.wrappedBuffer(fileBytes, 0, 4096);
        int[] offsets = new int[CHUNKS_PER_REGION];
        for (int i = 0; i < CHUNKS_PER_REGION; i++) {
            offsets[i] = headerBuf.readInt();
        }
        headerBuf.release();

        byte[] compressedBytes = new byte[fileBytes.length - 4096];
        System.arraycopy(fileBytes, 4096, compressedBytes, 0, compressedBytes.length);

        long decompressedSize = Zstd.decompressedSize(compressedBytes);
        byte[] uncompressedBytes = Zstd.decompress(compressedBytes, (int) decompressedSize);

        ByteBuf uncompressedBuf = Unpooled.wrappedBuffer(uncompressedBytes);

        try {
            for (int i = 0; i < CHUNKS_PER_REGION; i++) {
                int offset = offsets[i];
                if (offset == -1) continue;

                uncompressedBuf.readerIndex(offset);

                int cx = uncompressedBuf.readInt();
                int cz = uncompressedBuf.readInt();
                boolean isEmpty = uncompressedBuf.readBoolean();

                Chunk chunk = new Chunk(cx, cz, isEmpty);

                // 1. Загружаем блоки секций
                short[][] sections = chunk.getSections();
                for (int s = 0; s < 24; s++) {
                    boolean hasSection = uncompressedBuf.readBoolean();
                    if (hasSection) {
                        short[] sectionBlocks = new short[4096];
                        for (int b = 0; b < 4096; b++) {
                            sectionBlocks[b] = uncompressedBuf.readShort();
                        }
                        sections[s] = sectionBlocks;
                    } else {
                        sections[s] = null;
                    }
                }

                // 2. Загружаем свет секций (Block Light)
                byte[][] blockLightSections = chunk.getBlockLightSections();
                for (int s = 0; s < 24; s++) {
                    boolean hasLightSection = uncompressedBuf.readBoolean();
                    if (hasLightSection) {
                        byte[] lightSec = new byte[2048];
                        uncompressedBuf.readBytes(lightSec);
                        blockLightSections[s] = lightSec;
                    } else {
                        blockLightSections[s] = null;
                    }
                }

                regionChunks.put(cx + "," + cz, chunk);
            }
        } finally {
            uncompressedBuf.release();
        }

        MiniCore.getLogger().info("[Core/World] Загружен регион: " + file.getName() + " | Найдено чанков: " + regionChunks.size());
        return regionChunks;
    }
}