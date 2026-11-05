package dev.minixr9k.chunk;

import dev.minixr9k.config.Configuration;

public class Chunk {
    private final int x;
    private final int z;
    private boolean isEmpty;

    private final short[][] sections = new short[24][];
    // 4 бита на блок -> 2048 байт на секцию (4096 блоков / 2)
    private final byte[][] blockLightSections = new byte[24][];

    public Chunk(int x, int z, boolean isEmpty) {
        this.x = x;
        this.z = z;
        this.isEmpty = isEmpty;

        if (!isEmpty) {
            generateTerrain();
            // Считаем свет сразу при генерации чанка
            LightEngine.recalculateBlockLight(this);
        }
    }

    private void generateTerrain() {
        String worldType = Configuration.get().world.type;
        switch (worldType.toUpperCase()) {
            case "FLAT" -> generateFlatTerrain();
            case "FLAT/OLD" -> generateFlatOldTerrain();
            case "FLAT/MINICORE" -> generateDefaultTerrain();
            default -> generateFlatTerrain();
        }
    }

    private void generateFlatTerrain() {
        int sectionTarget = 0;
        int startWorldY = (sectionTarget * 16) - 64;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                setBlockAt(localX, startWorldY, localZ, (short) 85);
                for (int y = 1; y <= 2; y++) {
                    setBlockAt(localX, startWorldY + y, localZ, (short) 10);
                }
                setBlockAt(localX, startWorldY + 3, localZ, (short) 9);
            }
        }
    }

    private void generateFlatOldTerrain() {
        int sectionTarget = 0;
        int startWorldY = (sectionTarget * 16);

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                setBlockAt(localX, startWorldY, localZ, (short) 85);
                for (int y = 1; y <= 2; y++) {
                    setBlockAt(localX, startWorldY + y, localZ, (short) 10);
                }
                setBlockAt(localX, startWorldY + 3, localZ, (short) 9);
            }
        }
    }

    private void generateDefaultTerrain() {
        int sectionTarget = 7;
        int startWorldY = (sectionTarget * 16) - 64;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int y = 0; y <= 11; y++) {
                    setBlockAt(localX, startWorldY + y, localZ, (short) 1);
                }
                for (int y = 12; y <= 14; y++) {
                    setBlockAt(localX, startWorldY + y, localZ, (short) 10);
                }
                setBlockAt(localX, startWorldY + 15, localZ, (short) 9);
            }
        }
    }

    public void setBlockAt(int worldX, int worldY, int worldZ, short blockId) {
        int internalY = worldY + 64;
        if (internalY < 0 || internalY >= 384) return;

        int sectionIndex = internalY >> 4;
        int localY = internalY & 15;
        int localX = worldX & 15;
        int localZ = worldZ & 15;

        if (blockId != 0 && isEmpty) {
            this.isEmpty = false;
        }

        if (sections[sectionIndex] == null) {
            if (blockId == 0) return;
            sections[sectionIndex] = new short[4096];
        }

        int blockIndex = (localY << 8) | (localZ << 4) | localX;
        sections[sectionIndex][blockIndex] = blockId;
    }

    public short getBlockAt(int worldX, int worldY, int worldZ) {
        if (isEmpty) return 0;

        int internalY = worldY + 64;
        if (internalY < 0 || internalY >= 384) return 0;

        int sectionIndex = internalY >> 4;
        if (sections[sectionIndex] == null) return 0;

        int localY = internalY & 15;
        int localX = worldX & 15;
        int localZ = worldZ & 15;

        int blockIndex = (localY << 8) | (localZ << 4) | localX;
        return sections[sectionIndex][blockIndex];
    }

    /**
     * Получить уровень Block Light (0-15) по абсолютным координатам
     */
    public int getBlockLight(int worldX, int worldY, int worldZ) {
        int internalY = worldY + 64;
        if (internalY < 0 || internalY >= 384) return 0;

        int sectionIndex = internalY >> 4;
        byte[] lightSection = blockLightSections[sectionIndex];
        if (lightSection == null) return 0;

        int localY = internalY & 15;
        int localX = worldX & 15;
        int localZ = worldZ & 15;

        int blockIndex = (localY << 8) | (localZ << 4) | localX;
        int byteIndex = blockIndex >> 1;
        byte b = lightSection[byteIndex];

        if ((blockIndex & 1) == 0) {
            return b & 0x0F;
        } else {
            return (b >> 4) & 0x0F;
        }
    }

    /**
     * Установить уровень Block Light (0-15) по абсолютным координатам
     */
    public void setBlockLight(int worldX, int worldY, int worldZ, int level) {
        int internalY = worldY + 64;
        if (internalY < 0 || internalY >= 384) return;

        int sectionIndex = internalY >> 4;
        int localY = internalY & 15;
        int localX = worldX & 15;
        int localZ = worldZ & 15;

        if (blockLightSections[sectionIndex] == null) {
            blockLightSections[sectionIndex] = new byte[2048];
        }

        byte[] lightSection = blockLightSections[sectionIndex];
        int blockIndex = (localY << 8) | (localZ << 4) | localX;
        int byteIndex = blockIndex >> 1;

        level = Math.max(0, Math.min(15, level));

        byte current = lightSection[byteIndex];
        if ((blockIndex & 1) == 0) {
            lightSection[byteIndex] = (byte) ((current & 0xF0) | (level & 0x0F));
        } else {
            lightSection[byteIndex] = (byte) ((current & 0x0F) | ((level & 0x0F) << 4));
        }
    }

    public int getX() { return x; }
    public int getZ() { return z; }
    public boolean isEmpty() { return isEmpty; }
    public short[][] getSections() { return sections; }
    public byte[][] getBlockLightSections() { return blockLightSections; }
}