package dev.minixr9k.network.handlers;

import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.PlayerMoveEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetPassengers;
import dev.minixr9k.types.Entity;
import dev.minixr9k.types.Player;
import dev.minixr9k.types.actions.MoveType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PlayerMovementHandler {

    private final Player player;

    public PlayerMovementHandler(Player player) {
        this.player = player;
    }

    public void handlePosition(ChannelHandlerContext ctx, ByteBuf in) {
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        byte flags = in.readByte();

        PlayerMoveEvent event = new PlayerMoveEvent(player, x, y, z, player.getYaw(), player.getPitch(), flags == 0x01, MoveType.POSITION);

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            player.teleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            return;
        }

        player.setX(x);
        player.setY(y);
        player.setZ(z);
        player.setOnGround(flags == 0x01);

        World.movePlayer(player);
    }

    public void handlePositionRotation(ChannelHandlerContext ctx, ByteBuf in) {
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        byte flags = in.readByte();

        PlayerMoveEvent event = new PlayerMoveEvent(player, x, y, z, yaw, pitch, flags == 0x01, MoveType.POSITION_ROTATION);

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            player.teleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            return;
        }

        player.setX(x);
        player.setY(y);
        player.setZ(z);
        player.setYaw(yaw);
        player.setPitch(pitch);
        player.setOnGround(flags == 0x01);

        World.movePlayer(player);
    }

    public void handleRotation(ChannelHandlerContext ctx, ByteBuf in) {
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        byte flags = in.readByte();

        PlayerMoveEvent event = new PlayerMoveEvent(player, player.getX(), player.getY(), player.getZ(), yaw, pitch, flags == 0x01, MoveType.ROTATION);

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            player.teleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            return;
        }

        player.setYaw(yaw);
        player.setPitch(pitch);
        player.setOnGround(flags == 0x01);

        World.movePlayer(player);
    }

    public void handleMoveVehicle(ChannelHandlerContext ctx, ByteBuf in) {
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        boolean onGround = in.readBoolean();

        int entityType = player.getPassengerOfEntity();
        if (entityType != 0) {
            Entity entity = World.getEntity(entityType);
            if (entity == null) return;

            double deltaY = y - entity.getY();

            entity.updatePrevPosition();

            entity.setX(x);
            entity.setY(y);
            entity.setZ(z);
            entity.setYaw(yaw);
            entity.setPitch(pitch);
            player.setX(x);
            player.setY(y);
            player.setZ(z);

            if (deltaY > 0 && Configuration.get().features.fixBoatFly) {
                entity.setX(entity.getPrevX());
                entity.setY(entity.getPrevY());
                entity.setZ(entity.getPrevZ());
                new ClientboundSetPassengers(player.getPassengerOfEntity(), 0).send(ctx, player.getProtocolVersion());
                World.setPassenger(player, 0);
                player.setPassengerOfEntity(0);
            }

            World.moveEntity(entity);
        }
    }
}
