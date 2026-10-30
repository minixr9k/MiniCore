package dev.minixr9k.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PacketReceiveEvent;
import dev.minixr9k.api.event.RegistriesSendEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.packets.confAndPlay.serverbound.ServerboundPluginMessage;
import dev.minixr9k.packets.configuration.ClientboundFinishConfiguration;
import dev.minixr9k.packets.configuration.ClientboundKnownPacks;
import dev.minixr9k.packets.configuration.ClientboundPluginMessage;
import dev.minixr9k.packets.configuration.serverbound.ServerboundFinishConfiguration;
import dev.minixr9k.packets.configuration.serverbound.ServerboundKnownPacksResponse;
import dev.minixr9k.registries.PacketRegistry;
import dev.minixr9k.registries.RegistryDataSender;
import dev.minixr9k.registries.data.MobVariant;
import dev.minixr9k.registries.data.WolfVariant;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;
import dev.minixr9k.utils.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationState extends SimpleChannelInboundHandler<ByteBuf> {

    private final int protocolVersion;
    private final Player player;

    private final List<MobVariant> cowVariants = List.of(
            new MobVariant("cold", "minecraft:entity/cow/cold_cow"),
            new MobVariant("temperate", "minecraft:entity/cow/temperate_cow"),
            new MobVariant("warm", "minecraft:entity/cow/warm_cow")
    );

    private final List<MobVariant> frogVariants = List.of(
            new MobVariant("cold", "minecraft:entity/frog/cold_frog"),
            new MobVariant("temperate", "minecraft:entity/frog/temperate_frog"),
            new MobVariant("warm", "minecraft:entity/frog/warm_frog")
    );

    private final List<MobVariant> pigVariants = List.of(
            new MobVariant("cold", "minecraft:entity/pig/cold_pig"),
            new MobVariant("temperate", "minecraft:entity/pig/temperate_pig"),
            new MobVariant("warm", "minecraft:entity/pig/warm_pig")
    );

    private final List<MobVariant> chickenVariants = List.of(
            new MobVariant("cold", "minecraft:entity/chicken/cold_chicken"),
            new MobVariant("temperate", "minecraft:entity/chicken/temperate_chicken"),
            new MobVariant("warm", "minecraft:entity/chicken/warm_chicken")
    );

    private final List<MobVariant> catVariants = List.of(
            new MobVariant("all_black", "minecraft:entity/cat/all_black"),
            new MobVariant("black", "minecraft:entity/cat/black"),
            new MobVariant("british_shorthair", "minecraft:entity/cat/british_shorthair"),
            new MobVariant("calico", "minecraft:entity/cat/calico"),
            new MobVariant("jellie", "minecraft:entity/cat/jellie"),
            new MobVariant("persian", "minecraft:entity/cat/persian"),
            new MobVariant("ragdoll", "minecraft:entity/cat/ragdoll"),
            new MobVariant("red", "minecraft:entity/cat/red"),
            new MobVariant("siamese", "minecraft:entity/cat/siamese"),
            new MobVariant("tabby", "minecraft:entity/cat/tabby"),
            new MobVariant("white", "minecraft:entity/cat/white")
    );

    private final List<MobVariant> zombieNautilusVariants = List.of(
            new MobVariant("temperate", "minecraft:entity/zombie_nautilus/temperate")
    );

    private final List<WolfVariant> wolfVariants = List.of(
            new WolfVariant("ashen", "minecraft:entity/wolf/wolf_ashen_tame", "minecraft:entity/wolf/wolf_ashen_angry", "minecraft:entity/wolf/wolf_ashen"),
            new WolfVariant("black", "minecraft:entity/wolf/wolf_black_tame", "minecraft:entity/wolf/wolf_black_angry", "minecraft:entity/wolf/wolf_black"),
            new WolfVariant("chestnut", "minecraft:entity/wolf/wolf_chestnut_tame", "minecraft:entity/wolf/wolf_chestnut_angry", "minecraft:entity/wolf/wolf_chestnut"),
            new WolfVariant("pale", "minecraft:entity/wolf/wolf_pale_tame", "minecraft:entity/wolf/wolf_pale_angry", "minecraft:entity/wolf/wolf_pale"),
            new WolfVariant("rusty", "minecraft:entity/wolf/wolf_rusty_tame", "minecraft:entity/wolf/wolf_rusty_angry", "minecraft:entity/wolf/wolf_rusty"),
            new WolfVariant("snowy", "minecraft:entity/wolf/wolf_snowy_tame", "minecraft:entity/wolf/wolf_snowy_angry", "minecraft:entity/wolf/wolf_snowy"),
            new WolfVariant("spotted", "minecraft:entity/wolf/wolf_spotted_tame", "minecraft:entity/wolf/wolf_spotted_angry", "minecraft:entity/wolf/wolf_spotted"),
            new WolfVariant("striped", "minecraft:entity/wolf/wolf_striped_tame", "minecraft:entity/wolf/wolf_striped_angry", "minecraft:entity/wolf/wolf_striped"),
            new WolfVariant("woods", "minecraft:entity/wolf/wolf_woods_tame", "minecraft:entity/wolf/wolf_woods_angry", "minecraft:entity/wolf/wolf_woods")
    );

    public ConfigurationState(int protocolVersion, Player player) {
        this.protocolVersion = protocolVersion;
        this.player = player;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (protocolVersion <= 772)
            new ClientboundKnownPacks("minecraft", "core", "1.21.8").send(ctx, 1, player);
        else if (protocolVersion == 773)
            new ClientboundKnownPacks("minecraft", "core", "1.21.10").send(ctx, 1, player);
        else if (protocolVersion == 774)
            new ClientboundKnownPacks("minecraft", "core", "1.21.11").send(ctx, 1, player);
        else if (protocolVersion == 775)
            new ClientboundKnownPacks("minecraft", "core", "26.1").send(ctx, 1, player);
        else if (protocolVersion == 776)
            new ClientboundKnownPacks("minecraft", "core", "26.2").send(ctx, 1, player);
        else if (protocolVersion == 777)
            new ClientboundKnownPacks("minecraft", "core", "26.3").send(ctx, 1, player);
        else if (protocolVersion == 778)
            new ClientboundKnownPacks("minecraft", "core", "26.4").send(ctx, 1, player);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (!in.isReadable()) return;
        int packetId = ProtocolUtils.readVarInt(in);

        MinecraftPacket packet = PacketRegistry.handleConfiguration(packetId);

        if (packet != null) {
            packet.read(in, protocolVersion);

            PacketReceiveEvent packetReceiveEvent = new PacketReceiveEvent(packet, player);
            EventBus.getInstance().callEvent(packetReceiveEvent);

            if (packetReceiveEvent.isCancelled()) return;

            handle(ctx, packet);
        }
    }

    private void handle(ChannelHandlerContext ctx, MinecraftPacket packet) {
        if (packet instanceof ServerboundFinishConfiguration) {
            ctx.pipeline().replace(this, "handler", new PlayState(protocolVersion, player));
        }
        else if (packet instanceof ServerboundKnownPacksResponse) {

            ByteBuf payload = ctx.alloc().buffer();
            ProtocolUtils.writeString(payload, "MiniCore");
            new ClientboundPluginMessage("minecraft:brand", payload).send(ctx, 1, player);

            sendRegistryData(ctx);
            new ClientboundFinishConfiguration().send(ctx, 1, player);
        }
        else if (packet instanceof ServerboundPluginMessage) {
            if (((ServerboundPluginMessage) packet).getChannel().equals("minecraft:brand")) {
                if (Configuration.get().features.logBrand)
                    MiniCore.getLogger().info("[Core/Brand] " + player.getUsername() + " using " + ((ServerboundPluginMessage) packet).getData());
                player.setBrand(((ServerboundPluginMessage) packet).getData());
            }
        }
    }

    private void sendRegistryData(ChannelHandlerContext ctx) {
        RegistryDataSender sender = new RegistryDataSender();

        RegistriesSendEvent event = new RegistriesSendEvent(
                player,
                new ArrayList<>(wolfVariants),
                new ArrayList<>(catVariants),
                new ArrayList<>(chickenVariants),
                new ArrayList<>(cowVariants),
                new ArrayList<>(frogVariants),
                new ArrayList<>(pigVariants),
                new ArrayList<>(zombieNautilusVariants)
        );

        EventBus.getInstance().callEvent(event);

        sender.sendOverworldDimensionRegistry(ctx);
        sender.sendBiomeRegistry(ctx);
        sender.sendDamageTypes(ctx);
        sender.sendWolfVariants(ctx, event.getWolfVariants());
        sender.sendWolfSoundVariants(ctx);
        sender.sendMobVariant(ctx, "minecraft:cat_variant", event.getCatVariants());
        sender.sendMobVariant(ctx, "minecraft:chicken_variant", event.getChickenVariants());
        sender.sendMobVariant(ctx, "minecraft:cow_variant", event.getCowVariants());
        sender.sendMobVariant(ctx, "minecraft:frog_variant", event.getFrogVariants());
        sender.sendMobVariant(ctx, "minecraft:pig_variant", event.getPigVariants());
        sender.sendPaintingVariants(ctx);

        if (protocolVersion >= 774) { // 1.21.11
            sender.sendMobVariant(ctx, "minecraft:zombie_nautilus_variant", event.getZombieNautilusVariants());
            sender.sendTimeLine(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String username = (player != null && player.getUsername() != null)
                ? player.getUsername()
                : ctx.channel().remoteAddress().toString();

        String msg = cause.getMessage();
        boolean isReset = cause instanceof java.io.IOException
                || (msg != null && (msg.contains("Connection reset") || msg.contains("Broken pipe")));

        if (isReset) {
            MiniCore.getLogger().info("[Configuration/Disconnect] " + username + " lost connection: Connection reset by peer");
        } else {
            MiniCore.getLogger().info(String.format("[Configuration] Exception in channel for %s: %s%n", username, msg));
        }

        ctx.close();
    }
}