package dev.minixr9k.network.handlers;

import dev.minixr9k.MiniCore;
import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PlayerChatEvent;
import dev.minixr9k.api.event.PlayerCommandEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.config.PluginLoader;
import dev.minixr9k.features.World;
import dev.minixr9k.types.Player;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

import static dev.minixr9k.utils.ProtocolUtils.readString;
import static dev.minixr9k.utils.ProtocolUtils.readVarInt;

public class PlayerChatHandler {

    private final Player player;

    public PlayerChatHandler(Player player) {
        this.player = player;
    }

    public void handleChat(ChannelHandlerContext ctx, ByteBuf in) {
        String message = readString(in);

        if (message.length() > 256) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.skipBytes(in.readableBytes());

        if (message.contains("§") && Configuration.get().features.illegalCharactersCheck) {
            player.kick("Illegal characters");
            return;
        }

        PlayerChatEvent event = new PlayerChatEvent(player, message);
        EventBus.getInstance().callEvent(event);

        String finalMessage = event.getStyle();
        if (finalMessage.isEmpty()) return;

        World.broadcast(finalMessage);
    }

    public void handleCommand(ChannelHandlerContext ctx, ByteBuf in) {
        int length = readVarInt(in);

        if (Configuration.get().features.debug) {
            System.out.println("[Core/Debug] recv command packet, length=" + length);
        }

        if (length > 256) {
            in.skipBytes(length);
            return;
        }

        String command = in.toString(in.readerIndex(), length, StandardCharsets.UTF_8);
        in.skipBytes(length);

        EventBus.getInstance().callEvent(new PlayerCommandEvent(player, command));

        if (Configuration.get().features.logCommands)
            System.out.println("[In-game/Chat] " + player.getUsername() + " использует /" + command);

        if (!Configuration.get().features.buildInCommands) return;

        if (command.startsWith("reload")) {
            if (player.getOpLevel() < 4) {
                player.sendMessage("Требуется 4 уровень оп для выполнения данной команды!");
                return;
            }
            PluginLoader.disablePlugins();
            PluginLoader.loadPlugins();
            player.sendMessage("Плагины перезагружены!");
        }

        if (command.startsWith("kick")) {
            if (player.getOpLevel() < 4) {
                player.sendMessage("Требуется 4 уровень оп для выполнения данной команды!");
                return;
            }
            if (command.length() < 6) return;
            Player target = World.getPlayer(command.substring(5));
            if (target != null) {
                target.kick("Kicked by operator");
            }
            else {
                player.sendMessage("Игрок не в сети!");
            }
        }

        if (command.equals("tps")) {
            if (player.getOpLevel() < 4) {
                player.sendMessage("Требуется 4 уровень оп для выполнения данной команды!");
                return;
            }

            player.sendMessage("§6TPS: §f" + MiniCore.getServerThread().getTps());
        }

        if (command.equals("mspt")) {
            if (player.getOpLevel() < 4) {
                player.sendMessage("Требуется 4 уровень оп для выполнения данной команды!");
                return;
            }

            player.sendMessage("§6MSPT: §f" + MiniCore.getServerThread().getMspt());
        }

        if (command.equals("save-all")) {
            if (player.getOpLevel() < 4) {
                player.sendMessage("Требуется 4 уровень оп для выполнения данной команды!");
                return;
            }
            World.broadcast("[ChunkHolder] Сохранение мира...");
            World.saveWorld();
            World.broadcast("[ChunkHolder] Успешно сохранен!");
        }

        if (command.equals("setblock")) {
            World.setLocalBlock(1, -60, 1, 1062, player);
        }
    }
}
