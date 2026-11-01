package dev.minixr9k.registries;

import dev.minixr9k.registries.data.MobVariant;
import dev.minixr9k.registries.data.WolfVariant;
import dev.minixr9k.utils.NbtUtils;
import dev.minixr9k.utils.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.writeString;
import static dev.minixr9k.utils.ProtocolUtils.writeVarInt;

public class RegistryDataSender {

    public void sendOverworldDimensionRegistry(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07);
            writeString(pBuf, "minecraft:dimension_type");

            // Количество записей (Prefixed Array)
            writeVarInt(pBuf, 1);
            // Имя записи (Identifier)
            writeString(pBuf, "minecraft:overworld");

            pBuf.writeBoolean(true);

            ByteBuf nbtBuf = ctx.alloc().buffer();
            try {
                NbtUtils.writeTagCompound(nbtBuf);

                NbtUtils.writeByte(nbtBuf, "piglin_safe", 0);
                NbtUtils.writeByte(nbtBuf, "natural", 1);
                NbtUtils.writeFloat(nbtBuf, "ambient_light", 0.0f);
                NbtUtils.writeInt(nbtBuf, "monster_spawn_block_light_limit", 0);
                NbtUtils.writeString(nbtBuf, "infiniburn", "#minecraft:infiniburn_overworld");
                NbtUtils.writeByte(nbtBuf, "respawn_anchor_works", 0);
                NbtUtils.writeByte(nbtBuf, "has_skylight", 1);
                NbtUtils.writeByte(nbtBuf, "bed_works", 1);
                NbtUtils.writeString(nbtBuf, "effects", "minecraft:overworld");
                NbtUtils.writeByte(nbtBuf, "has_raids", 1);
                NbtUtils.writeInt(nbtBuf, "logical_height", 384);
                NbtUtils.writeDouble(nbtBuf, "coordinate_scale", 1.0);
                NbtUtils.writeInt(nbtBuf, "cloud_height", 192);

                NbtUtils.writeTagCompound(nbtBuf, "monster_spawn_light_level");

                NbtUtils.writeInt(nbtBuf, "min_inclusive", 0);
                NbtUtils.writeInt(nbtBuf, "max_inclusive", 7);
                NbtUtils.writeString(nbtBuf, "type", "minecraft:uniform");

                NbtUtils.writeTagEnd(nbtBuf);


                NbtUtils.writeTagCompound(nbtBuf, "attributes");

                NbtUtils.writeString(nbtBuf, "minecraft:visual/cloud_color", "#ccffffff");
                NbtUtils.writeFloat(nbtBuf, "minecraft:visual/cloud_height", 192.33f);
                NbtUtils.writeString(nbtBuf, "minecraft:visual/fog_color", "#c0d8ff");
                NbtUtils.writeString(nbtBuf, "minecraft:visual/sky_color", "#78a7ff");
                NbtUtils.writeFloat(nbtBuf, "minecraft:visual/fog_start_distance", 10f);
                NbtUtils.writeFloat(nbtBuf, "minecraft:visual/fog_end_distance", 48f);

                NbtUtils.writeTagEnd(nbtBuf);

                NbtUtils.writeInt(nbtBuf, "min_y", -64);
                NbtUtils.writeByte(nbtBuf, "has_ceiling", 0);
                NbtUtils.writeByte(nbtBuf, "ultrawarm", 0);
                NbtUtils.writeInt(nbtBuf, "height", 384);

                NbtUtils.writeTagEnd(nbtBuf);

                pBuf.writeBytes(nbtBuf);

            } finally {
                nbtBuf.release();
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

    public void sendBiomeRegistry(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();

        try {
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:worldgen/biome");

            // Количество записей (Prefixed Array)
            writeVarInt(pBuf, 1);

            // Имя записи (Identifier)
            writeString(pBuf, "minecraft:plains");

            pBuf.writeBoolean(true);

            ByteBuf nbtBuf = ctx.alloc().buffer();
            try {
                NbtUtils.writeTagCompound(nbtBuf);

                NbtUtils.writeByte(nbtBuf, "has_precipitation", 1);
                NbtUtils.writeFloat(nbtBuf, "temperature", 0.8f);
                NbtUtils.writeFloat(nbtBuf, "downfall", 0.4f);

                NbtUtils.writeTagCompound(nbtBuf, "effects");

                NbtUtils.writeInt(nbtBuf, "sky_color", 7907327);
                NbtUtils.writeInt(nbtBuf, "fog_color", 12638463);
                NbtUtils.writeInt(nbtBuf, "water_color", 4159204);
                NbtUtils.writeInt(nbtBuf, "water_fog_color", 329011);

                NbtUtils.writeTagCompound(nbtBuf, "mood_sound");

                NbtUtils.writeString(nbtBuf, "sound", "minecraft:ambient.cave");
                NbtUtils.writeInt(nbtBuf, "tick_delay", 6000);
                NbtUtils.writeInt(nbtBuf, "block_search_extent", 8);
                NbtUtils.writeDouble(nbtBuf, "offset", 2.0);

                NbtUtils.writeTagEnd(nbtBuf);

                NbtUtils.writeFloat(nbtBuf, "music_volume", 1.0f);

                NbtUtils.writeTagEnd(nbtBuf);
                NbtUtils.writeTagEnd(nbtBuf);

                pBuf.writeBytes(nbtBuf);

            } finally {
                nbtBuf.release();
            }

            sendPacket(ctx, pBuf);

        } finally {
            if (pBuf != null && pBuf.refCnt() > 0) {
                pBuf.release();
            }
        }
    }

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
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:painting_variant");

            String[] variants = { // достаточно 1 картины :3
                    "fire"
            };

            writeVarInt(pBuf, variants.length);

            for (String variant : variants) {
                writeString(pBuf, "minecraft:" + variant);

                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    NbtUtils.writeTagCompound(nbtBuf);

                    NbtUtils.writeString(nbtBuf, "asset_id", "minecraft:" + variant);
                    NbtUtils.writeInt(nbtBuf, "width", 2);
                    NbtUtils.writeInt(nbtBuf, "height", 2);

                    NbtUtils.writeTagCompound(nbtBuf, "title");

                    NbtUtils.writeString(nbtBuf, "translate", "painting.minecraft.fire.title");
                    NbtUtils.writeString(nbtBuf, "color", "yellow");

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

    // это пиз*ец
    public void sendDamageTypes(ChannelHandlerContext ctx) {
        ByteBuf pBuf = ctx.alloc().buffer();
        try {
            writeVarInt(pBuf, 0x07);

            writeString(pBuf, "minecraft:damage_type");

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

            writeVarInt(pBuf, names.length);

            for (int i = 0; i < names.length; i++) {
                writeString(pBuf, names[i]);

                pBuf.writeBoolean(true);

                ByteBuf nbtBuf = ctx.alloc().buffer();
                try {
                    NbtUtils.writeTagCompound(nbtBuf);

                    NbtUtils.writeString(nbtBuf, "scaling", scalings[i]);
                    NbtUtils.writeFloat(nbtBuf, "exhaustion", exhaustions[i]);
                    NbtUtils.writeString(nbtBuf, "message_id", messageIds[i]);

                    if (deathMessageTypes[i] != null) {
                        NbtUtils.writeString(nbtBuf, "death_message_type", deathMessageTypes[i]);
                    }

                    if (effects[i] != null) {
                        NbtUtils.writeString(nbtBuf, "effects", effects[i]);
                    }

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

    private void sendPacket(ChannelHandlerContext ctx, ByteBuf buf) {
        ByteBuf finalBuf = ctx.alloc().buffer();
        writeVarInt(finalBuf, buf.readableBytes());
        finalBuf.writeBytes(buf);
        ctx.writeAndFlush(finalBuf);
    }
}