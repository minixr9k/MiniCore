package dev.minixr9k.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.auth.PlayerProfile;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.features.SkinCache;
import dev.minixr9k.features.World;
import dev.minixr9k.json.JConfig;
import dev.minixr9k.packets.login.ClientboundLoginDisconnect;
import dev.minixr9k.packets.login.ClientboundLoginSuccessPacket;
import dev.minixr9k.packets.login.ClientboundPluginRequest;
import dev.minixr9k.packets.login.serverbound.ServerboundLoginAck;
import dev.minixr9k.packets.login.serverbound.ServerboundLoginStart;
import dev.minixr9k.packets.login.serverbound.ServerboundPluginResponse;
import dev.minixr9k.registries.PacketRegistry;
import dev.minixr9k.types.Player;
import dev.minixr9k.utils.MinecraftPacket;
import dev.minixr9k.utils.ProtocolUtils;
import dev.minixr9k.utils.VelocityForwarding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public class LoginState extends SimpleChannelInboundHandler<ByteBuf> {

    private final int protocolVersion;
    private final List<PlayerProfile> properties;
    private Player player;
    private int messageId;

    public LoginState(int protocolVersion, List<PlayerProfile> properties) {
        this.protocolVersion = protocolVersion;
        this.properties = properties;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (protocolVersion != Configuration.get().features.mainProtocol) {
            if (Configuration.get().features.debug)
                MiniCore.getLogger().info("[Core/Login] подключение с версией протокола " + protocolVersion);
            new ClientboundLoginDisconnect("Для подключения используйте версию 1.21.8").send(ctx, protocolVersion);
            ctx.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int packetId = ProtocolUtils.readVarInt(in);

        MinecraftPacket packet = PacketRegistry.handleLogin(packetId);

        if (packet != null) {
            packet.read(in, protocolVersion);
            handlePacket(ctx, packet, in);
        } else {
            ctx.close();
        }
    }

    private void handlePacket(ChannelHandlerContext ctx, MinecraftPacket packet, ByteBuf buf) throws NoSuchAlgorithmException, InvalidKeyException {
        if (packet instanceof ServerboundLoginStart) {

            String username = ((ServerboundLoginStart) packet).getUsername();
            UUID uuid = ((ServerboundLoginStart) packet).getUuid();

            if (uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
                uuid = UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
            }

            if (username.length() < 3 || username.length() > 16) {
                disconnect(ctx, username, "Слишком длинный либо короткий ник");
            }

            if (Configuration.get().motd.maxPlayers > 0 && World.getAllPlayers().size() >= Configuration.get().motd.maxPlayers) {
                disconnect(ctx, username, "Сервер переполнен!");
            }

            if (World.getPlayer(username) != null) {
                disconnect(ctx, username, "Игрок с таким ником уже играет на сервере!");
            }

            if (Configuration.get().proxy.enabled && Configuration.get().proxy.forwardingMode == JConfig.ForwardingMode.MODERN) {

                ByteBuf payload = ctx.alloc().buffer();
                ProtocolUtils.writeVarInt(payload, 1);

                messageId = World.proxyMessageId.getAndIncrement();
                new ClientboundPluginRequest(messageId, "velocity:player_info", payload).send(ctx, protocolVersion);
                return;
            }

            player = new Player(ctx, protocolVersion);
            player.setUsername(username);
            player.setUuid(uuid);

            if (!properties.isEmpty()) {
                if (SkinCache.get(uuid) != null)
                    SkinCache.remove(uuid);
                SkinCache.put(uuid, properties);
            }

            MiniCore.getLogger().info("[Core/Login] UUID of player " + username + " is " + uuid);

            new ClientboundLoginSuccessPacket(uuid, username).send(ctx, protocolVersion, player);
        }
        else if (packet instanceof ServerboundPluginResponse) {
            int proxyMessageId = ((ServerboundPluginResponse) packet).getMessageId();
            boolean success = ((ServerboundPluginResponse) packet).isSuccessful();

            if (proxyMessageId == messageId) {
                if (!success || ((ServerboundPluginResponse) packet).getData() == null) {
                    disconnect(ctx, "-", "This server requires you to connect with Velocity.");
                    return;
                }
            }

            if (success) {
                try {
                    VelocityForwarding.ForwardedData forwardedData = VelocityForwarding.parse(((ServerboundPluginResponse) packet).getData(), Configuration.get().proxy.tokens.get(0));

                    UUID uuid = forwardedData.uuid;
                    String username = forwardedData.username;

                    player = new Player(ctx, protocolVersion);
                    player.setUuid(uuid);
                    player.setUsername(username);

                    if (!forwardedData.properties.isEmpty()) {
                        if (SkinCache.get(uuid) != null)
                            SkinCache.remove(uuid);
                        SkinCache.put(uuid, forwardedData.properties);
                    }

                    MiniCore.getLogger().info("[Core/Velocity] UUID of player " + player.getUsername() + " is " + player.getUuid());

                    new ClientboundLoginSuccessPacket(uuid, username).send(ctx, protocolVersion, player);
                } catch (Exception e) {
                    if (Configuration.get().features.debug)
                        MiniCore.getLogger().error("Failed to verify Velocity payload: " + e.getMessage());
                    disconnect(ctx, "-", "Invalid proxy response");
                }
            }
        }
        else if (packet instanceof ServerboundLoginAck) {
            if (player == null) {
                ctx.close();
                return;
            }

            ctx.pipeline().replace(this, "config_handler", new ConfigurationState(protocolVersion, player));
        }
    }

    private void disconnect(ChannelHandlerContext ctx, String username, String reason) {
        new ClientboundLoginDisconnect(reason).send(ctx, protocolVersion);
        ctx.close();
        MiniCore.getLogger().info("[Core/Login] [" + ctx.channel().remoteAddress().toString() + "] Disconnecting " + username + " cause: " + reason);
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
            MiniCore.getLogger().info("[Login/Disconnect] " + username + " lost connection: Connection reset by peer");
        } else {
            MiniCore.getLogger().info(String.format("[Login] Exception in channel for %s: %s%n", username, msg));
            cause.printStackTrace();
        }

        ctx.close();
    }
}