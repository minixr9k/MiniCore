package dev.minixr9k.network.handlers;

import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.*;
import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundAckBlockChange;
import dev.minixr9k.packets.play.ClientboundDamageEvent;
import dev.minixr9k.registries.BlockRegistry;
import dev.minixr9k.types.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static dev.minixr9k.features.World.*;
import static dev.minixr9k.utils.ProtocolUtils.readString;
import static dev.minixr9k.utils.ProtocolUtils.readVarInt;

public class PlayerInteractionHandler {

    private final Player player;

    public PlayerInteractionHandler(Player player) {
        this.player = player;
    }

    public void handleInteractEntity(ChannelHandlerContext ctx, ByteBuf in) {
        int entityId = readVarInt(in);
        int type = readVarInt(in);

        if (type == 2) {
            in.readFloat();
            in.readFloat();
            in.readFloat();
        }

        if (type == 0 || type == 2)
            readVarInt(in);

        boolean isSneaking = in.readBoolean();

        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, entityId, type, isSneaking);

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (type == 1) {
            Player p = World.getPlayer(entityId);
            if (p != null && (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SURVIVAL)) {
                p.setHealth(p.getHealth() - 1);
                new ClientboundDamageEvent(entityId, 34).send(ctx, player.getProtocolVersion());
                return;
            }

            new ClientboundDamageEvent(entityId, 34).send(ctx, player.getProtocolVersion());
        }
    }

    public void handleMiddleClick(ChannelHandlerContext ctx, ByteBuf in) {
        long location = in.readLong();
        boolean includeData = in.readBoolean();

        int x = (int) (location >> 38);
        int y = (int) ((location << 52) >> 52);
        int z = (int) ((location << 26) >> 38);

        if (player.getGameMode() != GameMode.CREATIVE) return;

        String type = World.getBlock(x, y, z);
        int idx = type.indexOf("[");
        if (idx != -1) {
            type = type.substring(0, idx);
        }

        player.getInventory().setItemInMainHand(new ItemStack(type, (short) 1));
        player.updateInventory(0);
    }

    public void handlePlayerAction(ChannelHandlerContext ctx, ByteBuf in) {
        int status = readVarInt(in);
        long location = in.readLong();
        byte face = in.readByte();
        int sequence = readVarInt(in);

        int x = (int) (location >> 38);
        int y = (int) ((location << 52) >> 52);
        int z = (int) ((location << 26) >> 38);

        if ((status == 0 && player.getGameMode() == GameMode.CREATIVE)
                || (status == 2 && player.getGameMode() == GameMode.SURVIVAL)) {

            BlockBreakEvent event = new BlockBreakEvent(player, new Block(World.getBlock(x, y, z), new Location(x, y, z)));
            EventBus.getInstance().callEvent(event);

            if (event.isCancelled()) {
                new ClientboundAckBlockChange(sequence).send(ctx, player.getProtocolVersion());
                player.updateInventory(0);
                return;
            }

            breakBlock(x, y, z);
            new ClientboundAckBlockChange(sequence).send(ctx, player.getProtocolVersion());
        }
        else if (status == 3) {
            EventBus.getInstance().callEvent(new PlayerInputEvent(player, InputKey.KEY_CTRL_Q));
            player.updateInventory(0);
        }
        else if (status == 4) {
            EventBus.getInstance().callEvent(new PlayerInputEvent(player, InputKey.KEY_Q));
            player.updateInventory(0);
        }
        else if (status == 6) {
            EventBus.getInstance().callEvent(new PlayerInputEvent(player, InputKey.KEY_F));
        }
    }

    public void handlePlayerCommand(ChannelHandlerContext ctx, ByteBuf in) {
        int entityId = readVarInt(in);
        int actionId = readVarInt(in);
        int jumpBoost = readVarInt(in);

        switch (actionId) {
            case 1 -> { // start sprinting
                player.setSprinting(true);
            }
            case 2 -> { // stop sprinting
                player.setSprinting(false);
            }
            case 6 -> { // start flying with elytra
                player.setGliding(true);
            }
        }
    }

    public void handleSwingArm(ChannelHandlerContext ctx, ByteBuf in) {
        int hand = readVarInt(in);

        EventBus.getInstance().callEvent(new PlayerSwingEvent(player, hand));

        if (hand == 0) {
            swing(player, 0);
            return;
        }
        swing(player, 3);
    }

    public void handleUseItem(ChannelHandlerContext ctx, ByteBuf in) {
        int hand = readVarInt(in);
        int sequence = readVarInt(in);
        float yaw = in.readFloat();
        float pitch = in.readFloat();

        // right click air
        EventBus.getInstance().callEvent(new PlayerInteractEvent(player, ActionType.RIGHT_CLICK_AIR));
    }

    public void handleCustomClickAction(ChannelHandlerContext ctx, ByteBuf in) {
        String action = readString(in);
        in.skipBytes(in.readableBytes());

        EventBus.getInstance().callEvent(new CustomActionEvent(player, action));
    }

    public void handleUseItemOn(ChannelHandlerContext ctx, ByteBuf in) {
        int hand = readVarInt(in);
        long location = in.readLong();
        int face = readVarInt(in);
        float cursorPosX = in.readFloat();
        float cursorPosY = in.readFloat();
        float cursorPosZ = in.readFloat();
        boolean insideBlock = in.readBoolean();
        boolean worldBorderHit = in.readBoolean();
        int sequence = readVarInt(in);

        if (player.getInventory().getItemInMainHand() == null) {
            new ClientboundAckBlockChange(sequence).send(ctx, player.getProtocolVersion());
            return;
        }

        // right click block

        int x = (int) (location >> 38);
        int y = (int) ((location << 52) >> 52);
        int z = (int) ((location << 26) >> 38);

        EventBus.getInstance().callEvent(new PlayerInteractEvent(player, ActionType.RIGHT_CLICK_BLOCK, x, y, z));

        switch (face) {
            case 0 -> y--; // Кликнули снизу блока -> новый блок под ним
            case 1 -> y++; // Кликнули сверху блока -> новый блок над ним
            case 2 -> z--; // Кликнули по северной грани -> блок к северу (-Z)
            case 3 -> z++; // Кликнули по южной грани -> блок к югу (+Z)
            case 4 -> x--; // Кликнули по западной грани -> блок к западу (-X)
            case 5 -> x++; // Кликнули по восточной грани -> блок к востоку (+X)
        }

        if (x == Math.floor(player.getX()) && z == Math.floor(player.getZ()) && (y == Math.floor(player.getY()) || y == Math.floor(player.getY() + 1))) return;
        if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.getInventory().getItemInMainHand() == null) return;

        int blockId = BlockRegistry.getBlock(player.getInventory().getItemInMainHand().getType(), 772);
        if (blockId == -1) return;

        BlockPlaceEvent event = new BlockPlaceEvent(player, new Block(World.getBlock(x, y, z), new Location(x, y, z)));

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            new ClientboundAckBlockChange(sequence).send(ctx, player.getProtocolVersion());
            player.updateInventory(0);
            return;
        }

        placeBlock(x, y, z, blockId);
        new ClientboundAckBlockChange(sequence).send(ctx, player.getProtocolVersion());

        if (player.getGameMode() != GameMode.SURVIVAL) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;
        if (item.getCount() - 1 > 0)
            player.getInventory().setItemHotbar(player.getInventory().getActiveSlot(), new ItemStack(item.getType(), (short) (item.getCount() - 1)));
        else
            player.getInventory().setItem(36 + player.getInventory().getActiveSlot(), null);
    }
}
