package dev.minixr9k.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.*;
import dev.minixr9k.auth.PlayerProfile;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.features.MiniMessage;
import dev.minixr9k.features.SkinCache;
import dev.minixr9k.features.World;
import dev.minixr9k.network.handlers.*;
import dev.minixr9k.packets.confAndPlay.ClientboundResourcepackPush;
import dev.minixr9k.packets.play.*;
import dev.minixr9k.types.*;
import dev.minixr9k.types.dialog.*;
import dev.minixr9k.types.textdisplay.TextDisplay;
import dev.minixr9k.utils.Requests;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static dev.minixr9k.features.World.*;
import static dev.minixr9k.utils.ProtocolUtils.*;

public class PlayState extends SimpleChannelInboundHandler<ByteBuf> {

    private final int protocolVersion;
    private final Player player;

    private final PlayerChatHandler chatHandler;
    private final KeepAliveHandler keepAliveHandler;
    private final PlayerMovementHandler movementHandler;
    private final PlayerInteractionHandler interactionHandler;
    private final PlayerInventoryHandler inventoryHandler;

    public PlayState(int protocolVersion, Player player) {
        this.protocolVersion = protocolVersion;
        this.player = player;

        chatHandler = new PlayerChatHandler(player);
        keepAliveHandler = new KeepAliveHandler(player);
        movementHandler = new PlayerMovementHandler(player);
        interactionHandler = new PlayerInteractionHandler(player);
        inventoryHandler = new PlayerInventoryHandler(player);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        new ClientboundJoinGame(Configuration.get().gameMode, player).send(ctx, protocolVersion, player);

        sendChunks(player, protocolVersion, Configuration.get().world.chunks);

        String resourcePack = Configuration.get().resourcepack.url;
        String resourcePackSha1 = Configuration.get().resourcepack.sha1;
        if (!resourcePack.isEmpty() && !resourcePackSha1.isEmpty()) {
            UUID uuid = UUID.nameUUIDFromBytes(resourcePack.getBytes(StandardCharsets.UTF_8));
            new ClientboundResourcepackPush(uuid, resourcePack, resourcePackSha1, Configuration.get().resourcepack.forced, Configuration.get().resourcepack.prompt).send(ctx, protocolVersion, player);
        }

        MiniCore.getScheduler().runTaskAsync(() -> {
            try {
                ctx.executor().execute(() -> {
                    if (!ctx.channel().isActive()) {
                        return;
                    }

                    if (Configuration.get().buildInSkins && !SkinCache.has(player.getUuid())) {
                        List<PlayerProfile> skinData = Requests.getSkin(player.getUsername());
                        SkinCache.put(player.getUuid(), skinData);
                    }
                    player.setEntityId(World.globalEntityId.getAndIncrement());
                    World.spawnPlayers(player);
                    World.addPlayer(player);
                    World.spawnEntities(player);
                    EventBus.getInstance().callEvent(new PlayerJoinEvent(player));
                    if (Configuration.get().features.buildInMessages)
                        World.broadcast("§e{player} joined the game (id={entityId})".replace("{player}", player.getUsername()).replace("{entityId}", String.valueOf(player.getEntityId())));

                    keepAliveHandler.startKeepAliveLoop(ctx);

                    if (protocolVersion <= 773)
                        player.setLocalWorldTime(Configuration.get().time, Configuration.get().isTimeIncreasing);
                    player.teleport(Configuration.get().spawnPosition);

                    int opLevel = Configuration.get().operators.getOrDefault(player.getUsername(), 0);
                    if (opLevel > 0) player.setOpLevel(opLevel);

                    player.sendGameEvent(13, 0);
                    World.setEquipment(player);

                    new ClientboundCommands(List.of()).send(ctx, protocolVersion, player);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        if (!in.isReadable()) return;

        int packetId = readVarInt(in);

        // TODO
//        PacketReceiveEvent packetReceiveEvent = new PacketReceiveEvent(packet, player);
//        EventBus.getInstance().callEvent(packetReceiveEvent);
//
//        if (packetReceiveEvent.isCancelled()) return;

        switch (packetId) {
            case 0x00 -> handleTeleport(in);
            case 0x04 -> handleChangeGamemode(in);
            case 0x06 -> chatHandler.handleCommand(ctx, in);
            case 0x08 -> chatHandler.handleChat(ctx, in);
            case 0x11 -> inventoryHandler.handleInventory(ctx, in);
            case 0x15 -> handlePlayerLoaded(ctx, in);
            case 0x1B -> keepAliveHandler.handleKeepAliveResponse(in);
            case 0x19 -> interactionHandler.handleInteractEntity(ctx, in);
            case 0x1D -> movementHandler.handlePosition(ctx, in);
            case 0x1E -> movementHandler.handlePositionRotation(ctx, in);
            case 0x1F -> movementHandler.handleRotation(ctx, in);
            case 0x21 -> movementHandler.handleMoveVehicle(ctx, in);
            case 0x23 -> interactionHandler.handleMiddleClick(ctx, in);
            case 0x28 -> interactionHandler.handlePlayerAction(ctx, in);
            case 0x29 -> interactionHandler.handlePlayerCommand(ctx, in);
            case 0x2A -> handlePlayerInput(ctx, in);
            case 0x34 -> inventoryHandler.handleSetHotbarSlot(ctx, in);
            case 0x37 -> inventoryHandler.handleSetCreativeModeSlot(ctx, in);
            case 0x3C -> interactionHandler.handleSwingArm(ctx, in);
            case 0x3F -> interactionHandler.handleUseItemOn(ctx, in);
            case 0x40 -> interactionHandler.handleUseItem(ctx, in);
            case 0x41 -> interactionHandler.handleCustomClickAction(ctx, in);
            default -> {
                if (Configuration.get().features.debug && packetId != 0xC)
                    MiniCore.getLogger().info("[Core/Debug] Unknown packet: " + Integer.toHexString(packetId));
                in.skipBytes(in.readableBytes());
            }
        }
    }

    private void handleTeleport(ByteBuf in) {
        int id = readVarInt(in);
        if (Configuration.get().features.debug)
            MiniCore.getLogger().info("[Core/Debug] Teleport confirmed! id=" + id);
    }

    private void handleChangeGamemode(ByteBuf in) {
        int gamemode = readVarInt(in);
        if (player.getOpLevel() < 2) return;
        switch(gamemode) {
            case 0 -> player.setGameMode(GameMode.SURVIVAL);
            case 1 -> player.setGameMode(GameMode.CREATIVE);
            case 2 -> player.setGameMode(GameMode.ADVENTURE);
            case 3 -> player.setGameMode(GameMode.SPECTATOR);
        }
    }

    private void handlePlayerInput(ChannelHandlerContext ctx, ByteBuf in) {
        byte flags = in.readByte();

        EventBus.getInstance().callEvent(new PlayerInputEvent(player, flags));

        if (isPressed(InputKey.KEY_SHIFT, flags)) {
            player.setSneaking(true);
            sneak(player, true);
        }
        else {
            player.setSneaking(false);
            sneak(player, false);
        }
    }

    public boolean isPressed(InputKey key, byte flags) {
        return switch (key) {
            case KEY_W     -> (flags & 0x01) != 0;
            case KEY_A     -> (flags & 0x04) != 0;
            case KEY_S     -> (flags & 0x02) != 0;
            case KEY_D     -> (flags & 0x08) != 0;
            case KEY_SPACE -> (flags & 0x10) != 0;
            case KEY_SHIFT -> (flags & 0x20) != 0;
            case KEY_CTRL  -> (flags & 0x40) != 0;
            default        -> false;
        };
    }

    private void handlePlayerLoaded(ChannelHandlerContext ctx, ByteBuf in) {
        EventBus.getInstance().callEvent(new PlayerLoadedEvent(player));
        player.setLoaded(true);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {

        EventBus.getInstance().callEvent(new PlayerQuitEvent(player));

        keepAliveHandler.closeTasks();

        World.removePlayer(player);
        if (Configuration.get().features.buildInMessages)
            World.broadcast("§e{player} quit the game".replace("{player}", player.getUsername()));
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
            MiniCore.getLogger().info("[In-game/Disconnect] " + username + " lost connection: Connection reset by peer\n");
        } else {
            MiniCore.getLogger().info(String.format("[In-game] Exception in channel for %s: %s%n\n", username, msg));
        }

        ctx.close();
    }
}