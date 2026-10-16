package dev.minixr9k.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PacketReceiveEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.packets.confAndPlay.serverbound.ServerboundPluginMessage;
import dev.minixr9k.packets.configuration.ClientboundFinishConfiguration;
import dev.minixr9k.packets.configuration.ClientboundKnownPacks;
import dev.minixr9k.packets.configuration.ClientboundPluginMessage;
import dev.minixr9k.packets.configuration.serverbound.ServerboundFinishConfiguration;
import dev.minixr9k.packets.configuration.serverbound.ServerboundKnownPacksResponse;
import dev.minixr9k.registries.PacketRegistry;
import dev.minixr9k.registries.RegistryDataSender;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;
import dev.minixr9k.utils.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConfigurationState extends SimpleChannelInboundHandler<ByteBuf> {

    private final int protocolVersion;
    private final Player player;

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
        sender.sendOverworldDimensionRegistry(ctx);
        sender.sendBiomeRegistry(ctx);
        sender.sendDamageTypes(ctx);
        sender.sendWolfVariants(ctx);
        sender.sendCatVariants(ctx);
        sender.sendWolfSoundVariants(ctx);
        sender.sendChickenVariants(ctx);
        sender.sendCowVariants(ctx);
        sender.sendFrogVariants(ctx);
        sender.sendPigVariants(ctx);
        sender.sendPaintingVariants(ctx);
        if (protocolVersion >= 774) {
            sender.sendZombieNautilusVariants(ctx);
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
            MiniCore.getLogger().info("[Configuration/Disconnect] " + username + " lost connection: Connection reset by peer\n");
        } else {
            MiniCore.getLogger().info(String.format("[Configuration] Exception in channel for %s: %s%n\n", username, msg));
        }

        ctx.close();
    }
}