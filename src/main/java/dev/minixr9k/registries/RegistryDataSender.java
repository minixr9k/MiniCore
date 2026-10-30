package dev.minixr9k.registries;

import dev.minixr9k.registries.data.MobVariant;
import dev.minixr9k.registries.data.WolfVariant;
import dev.minixr9k.utils.NbtUtils;
import dev.minixr9k.utils.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegistryDataSender {

    public void sendOverworldDimensionRegistry(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07); // ID пакета registry_data в Configuration

            // 1. Имя реестра (Identifier: VarInt длина + строка)
            writeString(pBuf, "minecraft:dimension_type");

            // 2. Количество записей (Prefixed Array) -> отправляем 1 запись
            writeVarInt(pBuf, 1);

            // 3. Имя записи (Identifier)
            writeString(pBuf, "minecraft:overworld");

            // 4. Флаг присутствия NBT (Prefixed Optional NBT) -> true
            pBuf.writeBoolean(true);

            ByteBuf nbtBuf = ctx.alloc().buffer();
            try {
                // Корневой Compound (В сетевом NBT имя ПУСТОЕ = short 0)
                nbtBuf.writeByte(10); // TAG_Compound
//
//
//                // --- Начинка element ---
//
//                // "piglin_safe": false (В NBT булеаны пишутся как TAG_Byte = 1)
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "piglin_safe");
                nbtBuf.writeByte(0);
//
//                // "natural": true
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "natural");
                nbtBuf.writeByte(1);
//
//                // "ambient_light": 0.0 (TAG_Float = 5)
                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "ambient_light");
                nbtBuf.writeFloat(0.0f);
//
//                // "monster_spawn_block_light_limit": 0 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "monster_spawn_block_light_limit");
                nbtBuf.writeInt(0);
//
//                // "infiniburn": "#minecraft:infiniburn_overworld" (TAG_String = 8)
                nbtBuf.writeByte(8);
                writeNBTString(nbtBuf, "infiniburn");
                writeNBTString(nbtBuf, "#minecraft:infiniburn_overworld");
//
//                // "respawn_anchor_works": false
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "respawn_anchor_works");
                nbtBuf.writeByte(0);
//
//                // "has_skylight": true
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "has_skylight");
                nbtBuf.writeByte(1);
//
//                // "bed_works": true
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "bed_works");
                nbtBuf.writeByte(1);
//
//                // "effects": "minecraft:overworld"
                nbtBuf.writeByte(8);
                writeNBTString(nbtBuf, "effects");
                writeNBTString(nbtBuf, "minecraft:overworld");
//                writeNBTString(nbtBuf, "minecraft:the_nether");
//
//                // "has_raids": true
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "has_raids");
                nbtBuf.writeByte(1);
//
//                // "logical_height": 384
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "logical_height");
                nbtBuf.writeInt(384);
//
//                // "coordinate_scale": 1.0 (TAG_Double = 6)
                nbtBuf.writeByte(6);
                writeNBTString(nbtBuf, "coordinate_scale");
                nbtBuf.writeDouble(1.0);
//
//                // "cloud_height": 192 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "cloud_height");
                nbtBuf.writeInt(192);
//
//                // "monster_spawn_light_level" - в 1.21.x это Compound (интервал uniform)
                nbtBuf.writeByte(10);
                writeNBTString(nbtBuf, "monster_spawn_light_level");

                nbtBuf.writeByte(3); // min_inclusive
                writeNBTString(nbtBuf, "min_inclusive");
                nbtBuf.writeInt(0);

                nbtBuf.writeByte(3); // max_inclusive
                writeNBTString(nbtBuf, "max_inclusive");
                nbtBuf.writeInt(7);

                nbtBuf.writeByte(8); // type
                writeNBTString(nbtBuf, "type");
                writeNBTString(nbtBuf, "minecraft:uniform");

                nbtBuf.writeByte(0); // Закрываем monster_spawn_light_level

//                // "attributes" - в 1.21.x это Compound (интервал uniform)
                nbtBuf.writeByte(10);
                writeNBTString(nbtBuf, "attributes");

                nbtBuf.writeByte(8); // minecraft:visual/fog_color (если не записывать то облаков не будет)
                writeNBTString(nbtBuf, "minecraft:visual/cloud_color");
                writeNBTString(nbtBuf, "#ccffffff");

                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "minecraft:visual/cloud_height");
                nbtBuf.writeFloat(192.33f);

                nbtBuf.writeByte(8); // minecraft:visual/fog_color
                writeNBTString(nbtBuf, "minecraft:visual/fog_color");
                writeNBTString(nbtBuf, "#c0d8ff");

                nbtBuf.writeByte(8); // minecraft:visual/sky_color
                writeNBTString(nbtBuf, "minecraft:visual/sky_color");
                writeNBTString(nbtBuf, "#78a7ff");

                // необязательные настройки
                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "minecraft:visual/fog_start_distance");
                nbtBuf.writeFloat(10f);

                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "minecraft:visual/fog_end_distance");
                nbtBuf.writeFloat(48f);
                //

                nbtBuf.writeByte(0); // Закрываем attributes
//
//                // "min_y": -64
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "min_y");
                nbtBuf.writeInt(-64);
//
//                // "has_ceiling": false
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "has_ceiling");
                nbtBuf.writeByte(0);
//
//                // "ultrawarm": false
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "ultrawarm");
                nbtBuf.writeByte(0);
//
//                // "height": 384
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "height");
                nbtBuf.writeInt(384);
//
//                // --- Закрываем структуру ---
                nbtBuf.writeByte(0); // закрыть корневой compound (TAG_End)

                // Записываем NBT-буфер в основной
                pBuf.writeBytes(nbtBuf);

            } finally {
                nbtBuf.release();
            }

            sendPacket(ctx, pBuf);
//            System.out.println("Sent dimension_type: overworld with full NBT");

        } finally {
            // Буфер гарантированно удалится и при успехе, и при ошибке
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendBiomeRegistry(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07); // ID пакета registry_data в Configuration

            // 1. Имя реестра (Identifier: VarInt длина + строка)
            writeString(pBuf, "minecraft:worldgen/biome");

            // 2. Количество записей (Prefixed Array) -> отправляем 1 запись
            writeVarInt(pBuf, 1);

            // 3. Имя записи (Identifier)
            writeString(pBuf, "minecraft:plains");

            // 4. Флаг присутствия NBT (Prefixed Optional NBT) -> true
            pBuf.writeBoolean(true);

            ByteBuf nbtBuf = ctx.alloc().buffer();
            try {
                // Корневой Compound (В сетевом NBT имя ПУСТОЕ = short 0)
                nbtBuf.writeByte(10); // TAG_Compound

                // "has_precipitation": true (TAG_Byte = 1)
                nbtBuf.writeByte(1);
                writeNBTString(nbtBuf, "has_precipitation");
                nbtBuf.writeByte(1);

                // "temperature": 0.8 (TAG_Float = 5)
                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "temperature");
                nbtBuf.writeFloat(0.8f);

                // "downfall": 0.4 (TAG_Float = 5)
                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "downfall");
                nbtBuf.writeFloat(0.4f);

                // "effects" - Compound
                nbtBuf.writeByte(10);
                writeNBTString(nbtBuf, "effects");

                // "sky_color": 7907327 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "sky_color");
                nbtBuf.writeInt(7907327);

                // "fog_color": 12638463 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "fog_color");
                nbtBuf.writeInt(12638463);

                // "water_color": 4159204 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "water_color");
                nbtBuf.writeInt(4159204);

                // "water_fog_color": 329011 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "water_fog_color");
                nbtBuf.writeInt(329011);

                // "mood_sound" - Compound
                nbtBuf.writeByte(10);
                writeNBTString(nbtBuf, "mood_sound");

                // "sound": "minecraft:ambient.cave" (TAG_String = 8)
                nbtBuf.writeByte(8);
                writeNBTString(nbtBuf, "sound");
                writeNBTString(nbtBuf, "minecraft:ambient.cave");

                // "tick_delay": 6000 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "tick_delay");
                nbtBuf.writeInt(6000);

                // "block_search_extent": 8 (TAG_Int = 3)
                nbtBuf.writeByte(3);
                writeNBTString(nbtBuf, "block_search_extent");
                nbtBuf.writeInt(8);

                // "offset": 2.0 (TAG_Double = 6)
                nbtBuf.writeByte(6);
                writeNBTString(nbtBuf, "offset");
                nbtBuf.writeDouble(2.0);

                nbtBuf.writeByte(0); // Закрываем mood_sound

                // "music_volume": 1.0 (TAG_Float = 5)
                nbtBuf.writeByte(5);
                writeNBTString(nbtBuf, "music_volume");
                nbtBuf.writeFloat(1.0f);

                nbtBuf.writeByte(0); // Закрываем effects

                // "temperature_modifier": "none" (TAG_String = 8) - необязательно, но можно добавить
                // nbtBuf.writeByte(8);
                // writeNBTString(nbtBuf, "temperature_modifier");
                // writeNBTString(nbtBuf, "none");

                nbtBuf.writeByte(0); // Закрываем корневой compound (TAG_End)

                // Записываем NBT-буфер в основной
                pBuf.writeBytes(nbtBuf);

            } finally {
                nbtBuf.release();
            }

            sendPacket(ctx, pBuf);

        } finally {
            // Буфер гарантированно удалится и при успехе, и при ошибке
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

//    public void sendWolfVariants(ChannelHandlerContext ctx) {
//        ByteBuf pBuf = ctx.alloc().buffer();
//
//        try {
//            writeVarInt(pBuf, 0x07);
//
//            writeString(pBuf, "minecraft:wolf_variant");
//
//            String[] variants = {"ashen",
//                                "black",
//                                "chestnut",
//                                "pale",
//                                "rusty",
//                                "snowy",
//                                "spotted",
//                                "striped",
//                                "woods"};
//
//            writeVarInt(pBuf, variants.length);
//
//            for (String variant : variants) {
//
//                writeString(pBuf, "minecraft:" + variant);
//                pBuf.writeBoolean(true);
//
//                ByteBuf nbtBuf = ctx.alloc().buffer();
//                try {
//                    NbtUtils.writeTagCompound(nbtBuf);
//                    NbtUtils.writeTagCompound(nbtBuf);
//                    ProtocolUtils.writeNBTString(nbtBuf, "assets");
//
//                    NbtUtils.writeString(nbtBuf, "tame", "minecraft:entity/wolf/" + "wolf_" + variant + "_tame");
//                    NbtUtils.writeString(nbtBuf, "angry", "minecraft:entity/wolf/" + "wolf_" + variant + "_angry");
//                    NbtUtils.writeString(nbtBuf, "wild", "minecraft:entity/wolf/" + "wolf_" + variant);
//
//                    NbtUtils.writeTagEnd(nbtBuf);
//                    NbtUtils.writeTagEnd(nbtBuf);
//
//                    pBuf.writeBytes(nbtBuf);
//
//                } finally {
//                    nbtBuf.release();
//                }
//            }
//
//            sendPacket(ctx, pBuf);
//
//        } finally {
//            if (pBuf != null && pBuf.refCnt() > 0) {
//                pBuf.release();
//            }
//        }
//    }

    public void sendWolfVariants(ChannelHandlerContext ctx, List<WolfVariant> variants) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:wolf_variant");

            writeVarInt(pBuf, variants.size());

            for (WolfVariant variant : variants) {

                writeString(pBuf, "minecraft:" + variant.name());
                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    NbtUtils.writeTagCompound(nbtBuf);
                    NbtUtils.writeTagCompound(nbtBuf);
                    ProtocolUtils.writeNBTString(nbtBuf, "assets");

                    NbtUtils.writeString(nbtBuf, "tame", variant.tame());
                    NbtUtils.writeString(nbtBuf, "angry", variant.angry());
                    NbtUtils.writeString(nbtBuf, "wild", variant.wild());

                    NbtUtils.writeTagEnd(nbtBuf);
                    NbtUtils.writeTagEnd(nbtBuf);

                    pBuf.writeBytes(nbtBuf);

                } finally {
                    nbtBuf.release();
                }
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendZombieNautilusVariants(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:zombie_nautilus_variant");

            String[] variants = {
                    "minecraft:temperate",
                    "minecraft:warm"};

            writeVarInt(pBuf, variants.length);

            for (String variant : variants) {

                writeString(pBuf, variant);

                pBuf.writeBoolean(false);
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendTimeLine(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:timeline");

            String[] variants = {
                    "minecraft:day",
                    "minecraft:early_game",
                    "minecraft:moon",
                    "minecraft:villager_schedule"};

            writeVarInt(pBuf, variants.length);

            for (String variant : variants) {
                writeString(pBuf, variant);

                pBuf.writeBoolean(false);
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendWolfSoundVariants(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();
        try {
            writeVarInt(pBuf, 0x07);
            writeString(pBuf, "minecraft:wolf_sound_variant");

            String[] variants = {
                    "angry",
                    "big",
                    "cute",
                    "grumpy",
                    "puglin",
                    "sad"
            };

            writeVarInt(pBuf, variants.length);
            for (String variant : variants) {
                writeString(pBuf, "minecraft:" + variant);
                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    NbtUtils.writeTagCompound(nbtBuf);

                    NbtUtils.writeString(nbtBuf, "hurt_sound", "minecraft:entity.wolf_" + variant + ".hurt");

                    NbtUtils.writeString(nbtBuf, "ambient_sound", "minecraft:entity.wolf_" + variant + ".ambient");

                    NbtUtils.writeString(nbtBuf, "death_sound", "minecraft:entity.wolf_" + variant + ".death");

                    NbtUtils.writeString(nbtBuf, "whine_sound", "minecraft:entity.wolf_" + variant + ".whine");

                    NbtUtils.writeString(nbtBuf, "pant_sound", "minecraft:entity.wolf_" + variant + ".pant");

                    NbtUtils.writeString(nbtBuf, "growl_sound", "minecraft:entity.wolf_" + variant + ".growl");

                    NbtUtils.writeTagEnd(nbtBuf);

                    pBuf.writeBytes(nbtBuf);

                } finally {
                    nbtBuf.release();
                }
            }

            sendPacket(ctx, pBuf);
        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendMobVariant(ChannelHandlerContext ctx, String identifier, List<MobVariant> variants) {
        ByteBuf pBuf = ctx.alloc().buffer();
        try {
            writeVarInt(pBuf, 0x07);
            writeString(pBuf, identifier);

            writeVarInt(pBuf, variants.size());
            for (MobVariant variant : variants) {
                writeString(pBuf, "minecraft:" + variant.name());
                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    NbtUtils.writeTagCompound(nbtBuf);

                    NbtUtils.writeString(nbtBuf, "asset_id", variant.texture());

                    NbtUtils.writeTagEnd(nbtBuf);

                    pBuf.writeBytes(nbtBuf);

                } finally {
                    nbtBuf.release();
                }
            }

            sendPacket(ctx, pBuf);
        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendPaintingVariants(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();
        try {
            // 1. ID пакета Registry Data (Configuration)
            writeVarInt(pBuf, 0x07);

            // 2. Идентификатор реестра
            writeString(pBuf, "minecraft:painting_variant");

//            String[] variants = {
//                    "minecraft:alban", "minecraft:aztec", "minecraft:aztec2", "minecraft:backyard",
//                    "minecraft:baroque", "minecraft:bomb", "minecraft:bouquet", "minecraft:burning_skull",
//                    "minecraft:bust", "minecraft:cavebird", "minecraft:changing", "minecraft:cotan",
//                    "minecraft:courbet", "minecraft:creebet", "minecraft:dennis", "minecraft:donkey_kong",
//                    "minecraft:earth", "minecraft:endboss", "minecraft:fern", "minecraft:fighters",
//                    "minecraft:finding", "minecraft:fire", "minecraft:graham", "minecraft:humble",
//                    "minecraft:kebab", "minecraft:lowmist", "minecraft:match", "minecraft:meditative",
//                    "minecraft:orb", "minecraft:owlemons", "minecraft:passage", "minecraft:pigscene",
//                    "minecraft:plant", "minecraft:pointer", "minecraft:pond", "minecraft:pool",
//                    "minecraft:prairie_ride", "minecraft:sea", "minecraft:skeleton", "minecraft:skull_and_roses",
//                    "minecraft:stage", "minecraft:sunflowers", "minecraft:sunset", "minecraft:tides",
//                    "minecraft:unpacked", "minecraft:void", "minecraft:wanderer", "minecraft:wasteland",
//                    "minecraft:water", "minecraft:wind", "minecraft:wither"
//            };

            String[] variants = { // достаточно 1 картины :3
                    "fire"
            };

            writeVarInt(pBuf, variants.length);

            for (String variant : variants) {
                writeString(pBuf, "minecraft:" + variant);

                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    nbtBuf.writeByte(10); // TAG_Compound

                    nbtBuf.writeByte(8);
                    writeNBTString(nbtBuf, "asset_id");
                    writeNBTString(nbtBuf, "minecraft:" + variant);

                    nbtBuf.writeByte(3);
                    writeNBTString(nbtBuf, "width");
                    nbtBuf.writeInt(2);

                    nbtBuf.writeByte(3);
                    writeNBTString(nbtBuf, "height");
                    nbtBuf.writeInt(2);

                    nbtBuf.writeByte(10);
                    writeNBTString(nbtBuf, "title");

                    nbtBuf.writeByte(8);
                    writeNBTString(nbtBuf, "translate");
                    writeNBTString(nbtBuf, "painting.minecraft.fire.title");

                    nbtBuf.writeByte(8);
                    writeNBTString(nbtBuf, "color");
                    writeNBTString(nbtBuf, "yellow");


                    nbtBuf.writeByte(0);

                    nbtBuf.writeByte(0); // закрыть корневой compound (TAG_End)

                    pBuf.writeBytes(nbtBuf);

                } finally {
                    nbtBuf.release();
                }
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    // это пиз*ец
    public void sendDamageTypes(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();
        try {
            // 1. ID пакета Registry Data (Configuration)
            writeVarInt(pBuf, 0x07);

            // 2. Идентификатор реестра
            writeString(pBuf, "minecraft:damage_type");

            // Создаем отдельные массивы для каждого поля
            String[] names = {
                    "minecraft:arrow", "minecraft:bad_respawn_point", "minecraft:cactus", "minecraft:campfire",
                    "minecraft:cramming", "minecraft:dragon_breath", "minecraft:drown", "minecraft:dry_out",
                    "minecraft:ender_pearl", "minecraft:explosion", "minecraft:fall", "minecraft:falling_anvil",
                    "minecraft:falling_block", "minecraft:falling_stalactite", "minecraft:fireball", "minecraft:fireworks",
                    "minecraft:fly_into_wall", "minecraft:freeze", "minecraft:generic", "minecraft:generic_kill",
                    "minecraft:hot_floor", "minecraft:in_fire", "minecraft:in_wall", "minecraft:indirect_magic",
                    "minecraft:lava", "minecraft:lightning_bolt", "minecraft:mace_smash", "minecraft:magic",
                    "minecraft:mob_attack", "minecraft:mob_attack_no_aggro", "minecraft:mob_projectile", "minecraft:on_fire",
                    "minecraft:out_of_world", "minecraft:outside_border", "minecraft:player_attack", "minecraft:player_explosion",
                    "minecraft:sonic_boom", "minecraft:spit", "minecraft:stalagmite", "minecraft:starve",
                    "minecraft:sting", "minecraft:sweet_berry_bush", "minecraft:thorns", "minecraft:thrown",
                    "minecraft:trident", "minecraft:unattributed_fireball", "minecraft:wind_charge", "minecraft:wither",
                    "minecraft:wither_skull"
            };

            String[] scalings = {
                    "when_caused_by_living_non_player", "always", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "always", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "always",
                    "always", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player", "when_caused_by_living_non_player",
                    "when_caused_by_living_non_player"
            };

            float[] exhaustions = {
                    0.1f, 0.1f, 0.1f, 0.1f, 0.0f, 0.0f, 0.0f, 0.1f,
                    0.0f, 0.1f, 0.0f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f,
                    0.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.1f, 0.0f, 0.0f,
                    0.1f, 0.1f, 0.1f, 0.0f, 0.1f, 0.1f, 0.1f, 0.0f,
                    0.0f, 0.0f, 0.1f, 0.1f, 0.0f, 0.1f, 0.0f, 0.0f,
                    0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.0f,
                    0.1f
            };

            String[] messageIds = {
                    "arrow", "badRespawnPoint", "cactus", "inFire", "cramming", "dragonBreath", "drown", "dryout",
                    "fall", "explosion", "fall", "anvil", "fallingBlock", "fallingStalactite", "fireball", "fireworks",
                    "flyIntoWall", "freeze", "generic", "genericKill", "hotFloor", "inFire", "inWall", "indirectMagic",
                    "lava", "lightningBolt", "mace_smash", "magic", "mob", "mob", "mob", "onFire",
                    "outOfWorld", "outsideBorder", "player", "explosion.player", "sonic_boom", "mob", "stalagmite", "starve",
                    "sting", "sweetBerryBush", "thorns", "thrown", "trident", "onFire", "mob", "wither",
                    "witherSkull"
            };

            // Опциональные поля
            String[] deathMessageTypes = {
                    null, "intentional_game_design", null, null, null, null, null, null,
                    "fall_variants", null, "fall_variants", null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null,
                    null
            };

            String[] effects = {
                    null, null, null, "burning", null, null, "drowning", null,
                    null, null, null, null, null, null, "burning", null,
                    null, "freezing", null, null, "burning", "burning", null, null,
                    "burning", null, null, null, null, null, null, "burning",
                    null, null, null, null, null, null, null, null,
                    null, "poking", "thorns", null, null, "burning", null, null,
                    null
            };

            // 3. Записываем количество элементов
            writeVarInt(pBuf, names.length);

            for (int i = 0; i < names.length; i++) {
                // Имя элемента (Identifier)
                writeString(pBuf, names[i]);

                // Отправляем NBT (true)
                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    // Корневой Compound
                    nbtBuf.writeByte(10); // TAG_Compound

                    // "scaling" - обязательное поле (TAG_String = 8)
                    nbtBuf.writeByte(8);
                    writeNBTString(nbtBuf, "scaling");
                    writeNBTString(nbtBuf, scalings[i]);

                    // "exhaustion" - обязательное поле (TAG_Float = 5)
                    nbtBuf.writeByte(5);
                    writeNBTString(nbtBuf, "exhaustion");
                    nbtBuf.writeFloat(exhaustions[i]);

                    // "message_id" - обязательное поле (TAG_String = 8)
                    nbtBuf.writeByte(8);
                    writeNBTString(nbtBuf, "message_id");
                    writeNBTString(nbtBuf, messageIds[i]);

                    // "death_message_type" - опциональное поле (TAG_String = 8)
                    if (deathMessageTypes[i] != null) {
                        nbtBuf.writeByte(8);
                        writeNBTString(nbtBuf, "death_message_type");
                        writeNBTString(nbtBuf, deathMessageTypes[i]);
                    }

                    // "effects" - опциональное поле (TAG_String = 8)
                    if (effects[i] != null) {
                        nbtBuf.writeByte(8);
                        writeNBTString(nbtBuf, "effects");
                        writeNBTString(nbtBuf, effects[i]);
                    }

                    nbtBuf.writeByte(0); // Закрываем корневой compound

                    // Записываем NBT-буфер в основной
                    pBuf.writeBytes(nbtBuf);

                } finally {
                    nbtBuf.release();
                }
            }

            sendPacket(ctx, pBuf);

        } finally {
            // Буфер гарантированно удалится и при успехе, и при ошибке
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    private void writeNBTString(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    private void writeString(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    // Для NBT строк - short (2 байта) длина!
    private void writeVarInt(ByteBuf buf, int value) {
        while ((value & 0xFFFFFF80) != 0) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value & 0x7F);
    }

    private void sendPacket(ChannelHandlerContext ctx, ByteBuf buf) {
        ByteBuf finalBuf = ctx.alloc().buffer();
        writeVarInt(finalBuf, buf.readableBytes());
        finalBuf.writeBytes(buf);
        ctx.writeAndFlush(finalBuf);
    }
}