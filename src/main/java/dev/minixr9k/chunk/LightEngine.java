package dev.minixr9k.chunk;

import dev.minixr9k.MiniCore;
import dev.minixr9k.features.World;
import dev.minixr9k.registries.BlockRegistry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class LightEngine {

    private static final int[][] DIRS = {
            {1, 0, 0}, {-1, 0, 0},
            {0, 1, 0}, {0, -1, 0},
            {0, 0, 1}, {0, 0, -1}
    };

    private static final Map<String, Integer> EMISSION_MAP = new HashMap<>();
    private static final Map<String, Integer> ATTENUATION_MAP = new HashMap<>();

    static {
        registerEmission("minecraft:glowstone", 15);
        registerEmission("minecraft:jack_o_lantern", 15);
        registerEmission("minecraft:redstone_lamp[lit=true]", 15);
        registerEmission("minecraft:lava", 15);
        registerEmission("minecraft:pearlescent_froglight", 15);
        registerEmission("minecraft:ochre_froglight", 15);
        registerEmission("minecraft:verdant_froglight", 15);
        registerEmission("minecraft:lantern", 15);
        registerEmission("minecraft:shroomlight", 15);
        registerEmission("minecraft:campfire", 15);
        registerEmission("minecraft:fire", 15);
        registerEmission("minecraft:sea_lantern", 15);
        registerEmission("minecraft:beacon", 15);
        registerEmission("minecraft:torch", 14);
        registerEmission("minecraft:wall_torch", 14);
        registerEmission("minecraft:end_rod", 14);
        registerEmission("minecraft:soul_torch", 10);
        registerEmission("minecraft:soul_wall_torch", 10);
        registerEmission("minecraft:soul_campfire", 10);
        registerEmission("minecraft:soul_lantern", 10);
        registerEmission("minecraft:redstone_torch", 7);
        registerEmission("minecraft:redstone_wall_torch", 7);
        registerEmission("minecraft:enchanting_table", 7);
        registerEmission("minecraft:ender_chest", 7);
        registerEmission("minecraft:magma_block", 3);

        registerEmission("minecraft:*_candle[candles=1,lit=true]", 3);
        registerEmission("minecraft:*_candle[candles=2,lit=true]", 6);
        registerEmission("minecraft:*_candle[candles=3,lit=true]", 9);
        registerEmission("minecraft:*_candle[candles=4,lit=true]", 12);

        registerAttenuation("minecraft:tinted_glass", 15); // пожирает весь свет

        // Пропускают насквозь
        registerAttenuation("minecraft:glass", 1);
        registerAttenuation("minecraft:*_glass", 1);
        registerAttenuation("minecraft:*_glass_pane", 1);
        registerAttenuation("minecraft:*_door", 1);
        registerAttenuation("minecraft:*_trapdoor", 1);
        registerAttenuation("minecraft:*_button", 1);
        registerAttenuation("minecraft:*_pressure_plate", 1);
        registerAttenuation("minecraft:*_wall", 1);
        registerAttenuation("minecraft:iron_bars", 1);
        registerAttenuation("minecraft:*_chain", 1);
        registerAttenuation("minecraft:*_sign", 1);
        registerAttenuation("minecraft:*_head", 1);
        registerAttenuation("minecraft:*_bed", 1);
        registerAttenuation("minecraft:*_banner", 1);
//        registerAttenuation("minecraft:*_carpet", 1);
        registerAttenuation("minecraft:sea_pickle", 1);
        registerAttenuation("minecraft:*_leaves", 1);
        registerAttenuation("minecraft:*_fence", 1);
        registerAttenuation("minecraft:*_fence", 1);
        registerAttenuation("minecraft:*_fence_gate", 1);
        registerAttenuation("minecraft:potted_dead_bush", 1);
        registerAttenuation("minecraft:flower_pot", 1);
        registerAttenuation("minecraft:repeater", 1);
        registerAttenuation("minecraft:large_fern", 1);
        registerAttenuation("minecraft:*_grass", 1);
        registerAttenuation("minecraft:slime_block", 1);
        registerAttenuation("minecraft:honey_block", 1);
    }

    public static void registerEmission(String blockName, int level) {
        if (!blockName.contains(":")) blockName = "minecraft:" + blockName;
        EMISSION_MAP.put(blockName, Math.max(0, Math.min(15, level)));
    }

    public static void registerAttenuation(String blockName, int attenuation) {
        if (!blockName.contains(":")) blockName = "minecraft:" + blockName;
        ATTENUATION_MAP.put(blockName, Math.max(0, Math.min(15, attenuation)));
    }

    private record LightNode(int x, int y, int z, int level) {}

    public static int getBlockEmission(short blockId) {
        String fullState = BlockRegistry.getBlockName(blockId, 772);
        if (fullState == null) return 0;
        return matchRegistry(fullState, EMISSION_MAP, 0);
    }

    public static int getLightAttenuation(short blockId) {
        if (blockId == 0) return 1; // Воздух пропускает свет

        String fullState = BlockRegistry.getBlockName(blockId, 772);
        if (fullState == null) return 15; // Неизвестный блок глушит свет полностью

        // По умолчанию твердые блоки глушат свет (15), но ищем совпадение в картах
        return matchRegistry(fullState, ATTENUATION_MAP, 15);
    }

    /**
     * Интеллектуальный поиск по шаблонам (поддерживает '*' и свойства в скобках '[...]')
     */
    private static int matchRegistry(String actualState, Map<String, Integer> map, int defaultValue) {
        // 1. Сначала проверяем точное совпадение
        if (map.containsKey(actualState)) {
            return map.get(actualState);
        }

        String actualBase = actualState;
        String actualProps = "";
        int bracketIdx = actualState.indexOf('[');
        if (bracketIdx != -1) {
            actualBase = actualState.substring(0, bracketIdx);
            actualProps = actualState.substring(bracketIdx + 1, actualState.length() - 1);
        }

        int bestMatchValue = -1;
        int maxSpecificity = -1;

        // ИСПРАВЛЕНИЕ: используем keySet() вместо getKeySet()
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String pattern = entry.getKey();

            String patternBase = pattern;
            String patternProps = "";
            int pBracketIdx = pattern.indexOf('[');
            if (pBracketIdx != -1) {
                patternBase = pattern.substring(0, pBracketIdx);
                patternProps = pattern.substring(pBracketIdx + 1, pattern.length() - 1);
            }

            if (wildcardMatch(actualBase, patternBase)) {
                boolean propsMatch = true;
                int specificity = 0;

                if (!patternProps.isEmpty()) {
                    specificity = patternProps.split(",").length;
                    for (String propRule : patternProps.split(",")) {
                        if (!actualProps.contains(propRule.trim())) {
                            propsMatch = false;
                            break;
                        }
                    }
                }

                if (propsMatch && specificity >= maxSpecificity) {
                    maxSpecificity = specificity;
                    bestMatchValue = entry.getValue();
                }
            }
        }

        return bestMatchValue != -1 ? bestMatchValue : defaultValue;
    }

    private static boolean wildcardMatch(String text, String pattern) {
        if (pattern.equals("*")) return true;

        String regex = pattern
                .replace(".", "\\.")
                .replace("*", ".*");

        return text.matches(regex);
    }

    public static void recalculateBlockLight(Chunk chunk) {
        recalculateAreaLight(chunk.getX(), chunk.getZ());
    }

    public static void recalculateAreaLight(int centerChunkX, int centerChunkZ) {
        // 1. Очищаем старый свет во всех 9 чанках области (3x3)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Chunk c = World.getChunkAt(centerChunkX + dx, centerChunkZ + dz);
                if (c != null) {
                    byte[][] lightSections = c.getBlockLightSections();
                    for (int s = 0; s < lightSections.length; s++) {
                        lightSections[s] = null;
                    }
                }
            }
        }

        // 2. Собираем ВСЕ актуальные источники света из этих 3x3 чанков
        Queue<LightNode> queue = new ArrayDeque<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Chunk c = World.getChunkAt(centerChunkX + dx, centerChunkZ + dz);
                if (c == null) continue;

                short[][] sections = c.getSections();
                for (int s = 0; s < sections.length; s++) {
                    if (sections[s] == null) continue;
                    int sectionWorldY = (s * 16) - 64;

                    for (int localY = 0; localY < 16; localY++) {
                        for (int localZ = 0; localZ < 16; localZ++) {
                            for (int localX = 0; localX < 16; localX++) {
                                int index = (localY << 8) | (localZ << 4) | localX;
                                short blockId = sections[s][index];

                                int emission = getBlockEmission(blockId);
                                if (emission > 0) {
                                    int worldX = (c.getX() << 4) + localX;
                                    int worldY = sectionWorldY + localY;
                                    int worldZ = (c.getZ() << 4) + localZ;

                                    c.setBlockLight(worldX, worldY, worldZ, emission);
                                    queue.add(new LightNode(worldX, worldY, worldZ, emission));
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Запускаем BFS распространение света
        while (!queue.isEmpty()) {
            LightNode node = queue.poll();

            for (int[] dir : DIRS) {
                int nx = node.x + dir[0];
                int ny = node.y + dir[1];
                int nz = node.z + dir[2];

                int targetCx = nx >> 4;
                int targetCz = nz >> 4;
                Chunk targetChunk = World.getChunkAt(targetCx, targetCz);
                if (targetChunk == null) continue;

                int neighborLight = targetChunk.getBlockLight(nx, ny, nz);

                if (neighborLight < node.level - 1) {
                    short neighborBlockId = targetChunk.getBlockAt(nx, ny, nz);
                    int attenuation = getLightAttenuation(neighborBlockId);

                    // Если блок непрозрачный (attenuation >= 15) — свет дальше не идет
                    if (attenuation >= 15) {
                        continue;
                    }

                    int newLevel = node.level - attenuation;

                    if (newLevel > 0) {
                        targetChunk.setBlockLight(nx, ny, nz, newLevel);
                        queue.add(new LightNode(nx, ny, nz, newLevel));
                    }
                }
            }
        }
    }

    public static void recalculateEntireWorld() {
        Map<String, Chunk> loadedChunks = World.getLoadedChunks();
        if (loadedChunks.isEmpty()) return;

        MiniCore.getLogger().info("[LightEngine] Запуск глобального пересчета света для " + loadedChunks.size() + " чанков мира...");
        long start = System.currentTimeMillis();

        // Сначала очищаем свет во всех чанках
        for (Chunk chunk : loadedChunks.values()) {
            byte[][] lightSections = chunk.getBlockLightSections();
            for (int s = 0; s < lightSections.length; s++) {
                lightSections[s] = null;
            }
        }

        // Собираем все источники света со всех чанков мира в одну очередь
        Queue<LightNode> queue = new ArrayDeque<>();
        for (Chunk chunk : loadedChunks.values()) {
            short[][] sections = chunk.getSections();
            for (int s = 0; s < sections.length; s++) {
                if (sections[s] == null) continue;
                int sectionWorldY = (s * 16) - 64;

                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        for (int localX = 0; localX < 16; localX++) {
                            int index = (localY << 8) | (localZ << 4) | localX;
                            short blockId = sections[s][index];

                            int emission = getBlockEmission(blockId);
                            if (emission > 0) {
                                int worldX = (chunk.getX() << 4) + localX;
                                int worldY = sectionWorldY + localY;
                                int worldZ = (chunk.getZ() << 4) + localZ;

                                chunk.setBlockLight(worldX, worldY, worldZ, emission);
                                queue.add(new LightNode(worldX, worldY, worldZ, emission));
                            }
                        }
                    }
                }
            }
        }

        // Запускаем единый BFS обход для всего мира
        while (!queue.isEmpty()) {
            LightNode node = queue.poll();

            for (int[] dir : DIRS) {
                int nx = node.x + dir[0];
                int ny = node.y + dir[1];
                int nz = node.z + dir[2];

                int targetCx = nx >> 4;
                int targetCz = nz >> 4;
                Chunk targetChunk = World.getChunkAt(targetCx, targetCz);
                if (targetChunk == null) continue;

                int neighborLight = targetChunk.getBlockLight(nx, ny, nz);

                if (neighborLight < node.level - 1) {
                    short neighborBlockId = targetChunk.getBlockAt(nx, ny, nz);
                    int attenuation = getLightAttenuation(neighborBlockId);

                    if (attenuation >= 15) {
                        continue;
                    }

                    int newLevel = node.level - attenuation;
                    if (newLevel > 0) {
                        targetChunk.setBlockLight(nx, ny, nz, newLevel);
                        queue.add(new LightNode(nx, ny, nz, newLevel));
                    }
                }
            }
        }

        // Сохраняем обновленный мир на диск, чтобы свет зафиксировался в .zregion файлах
        World.saveWorld();

        MiniCore.getLogger().info("[LightEngine] Глобальный пересчет света завершен за " + (System.currentTimeMillis() - start) + " мс!");
    }
}