package dev.minixr9k.network.handlers;

import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.InventoryClickEvent;
import dev.minixr9k.api.event.PlayerSetHotbarSlotEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.features.World;
import dev.minixr9k.registries.ItemRegistry;
import dev.minixr9k.types.GameMode;
import dev.minixr9k.types.ItemStack;
import dev.minixr9k.types.Player;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static dev.minixr9k.utils.ProtocolUtils.readVarInt;

public class PlayerInventoryHandler {

    private final Player player;

    public PlayerInventoryHandler(Player player) {
        this.player = player;
    }

    public void handleSetHotbarSlot(ChannelHandlerContext ctx, ByteBuf in) {
        short slot = in.readShort();
        if (slot < 0 || slot > 8) return;

        EventBus.getInstance().callEvent(new PlayerSetHotbarSlotEvent(player, slot));

        player.getInventory().setActiveSlot(slot);
        World.setEquipment(player);
    }

    public void handleSetCreativeModeSlot(ChannelHandlerContext ctx, ByteBuf in) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            in.skipBytes(in.readableBytes());
            return;
        }

        short slot = in.readShort();

        if (slot != -1) {

            int count = readVarInt(in); // item count
            if (count != 0) {
                int itemId = readVarInt(in); // item id

                boolean add = in.readBoolean(); // components to add
                boolean rem = in.readBoolean(); // components to remove

                String itemName = ItemRegistry.getItemName(itemId, player.getProtocolVersion());

                player.getInventory().setItem(slot, new ItemStack(itemName, (short) count));
            }
        }
    }

    public void handleInventory(ChannelHandlerContext ctx, ByteBuf in) {
        int windowId = readVarInt(in);
        int stateId = readVarInt(in);
        short slot = in.readShort();
        byte button = in.readByte();
        int mode = readVarInt(in);
        in.skipBytes(in.readableBytes());

        InventoryClickEvent event = new InventoryClickEvent(player, windowId, stateId, slot, button, mode);

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            player.updateInventory(stateId);
            return;
        }

        if (mode == 0) {
            switch (button) {
                case 0 -> {
                    ItemStack itemStack = player.getInventory().getItem(slot);
                    ItemStack carriedItem = player.getInventory().getCarriedItem();

                    if (carriedItem == null) {
                        if (itemStack != null) { // берем в курсор
                            player.getInventory().setCarriedItem(itemStack);
                            player.getInventory().setItem(slot, null);
                        }
                    }
                    else {
                        if (itemStack == null) { // кладем в пустой слот
                            player.getInventory().setItem(slot, carriedItem);
                            player.getInventory().setCarriedItem(null);
                        }
                        else {
                            if (!itemStack.getType().equals(carriedItem.getType())) {
                                // меняем местами предметы если они не одинового материала (1 в слот, другой в курсор)
                                player.getInventory().setItem(slot, carriedItem);
                                player.getInventory().setCarriedItem(itemStack);
                            }
                            else {
                                if (itemStack.getCount() + carriedItem.getCount() <= 64) {
                                    // если сумма не больше 64 то все чики пуки
                                    ItemStack newItemStack = new ItemStack(itemStack.getType(), (short) (itemStack.getCount() + carriedItem.getCount()), itemStack.getComponents());
                                    player.getInventory().setItem(slot, newItemStack);
                                    player.getInventory().setCarriedItem(null);
                                }
                                else {
                                    // если сумма больше 64
                                    int count = itemStack.getCount() + carriedItem.getCount() - 64;
                                    ItemStack newItemStack = new ItemStack(itemStack.getType(), (short) 64);
                                    ItemStack newCarriedItem = new ItemStack(carriedItem.getType(), (short) count);
                                    player.getInventory().setItem(slot, newItemStack);
                                    player.getInventory().setCarriedItem(newCarriedItem);
                                }
                            }
                        }
                    }
                }
                case 1 -> {
                    ItemStack itemStack = player.getInventory().getItem(slot);
                    ItemStack carriedItem = player.getInventory().getCarriedItem();

                    if (carriedItem == null) {
                        if (itemStack != null) { // берем в курсор
                            if (itemStack.getCount() > 1) { // если больше 1 берем половину
                                ItemStack newItemStack = new ItemStack(itemStack.getType(), (short) (itemStack.getCount() / 2), itemStack.getComponents());
                                player.getInventory().setCarriedItem(new ItemStack(itemStack.getType(), (short) (itemStack.getCount() - newItemStack.getCount()), itemStack.getComponents()));
                                player.getInventory().setItem(slot, newItemStack);
                            }
                            else {
                                player.getInventory().setCarriedItem(itemStack);
                                player.getInventory().setItem(slot, null);
                            }
                        }
                    }
                    else {
                        if (itemStack != null) {
                            // кладем по 1 блоку в слот
                            if (itemStack.getType().equals(carriedItem.getType())) {
                                if (itemStack.getCount() + 1 <= 64) {
                                    ItemStack newItemStack = new ItemStack(itemStack.getType(), (short) (itemStack.getCount() + 1), itemStack.getComponents());
                                    if (carriedItem.getCount() - 1 <= 0)
                                        player.getInventory().setCarriedItem(null);
                                    else
                                        player.getInventory().setCarriedItem(new ItemStack(itemStack.getType(), (short) (carriedItem.getCount() - 1), itemStack.getComponents()));
                                    player.getInventory().setItem(slot, newItemStack);
                                }
                            }
                            else {
                                // меняем местами предметы если они не одинового материала (1 в слот, другой в курсор)
                                player.getInventory().setItem(slot, carriedItem);
                                player.getInventory().setCarriedItem(itemStack);
                            }
                        }
                        else {
                            // кладем 1 блок в новый слот с курсора
                            ItemStack newItemStack = new ItemStack(carriedItem.getType(), (short) 1, carriedItem.getComponents());
                            if (carriedItem.getCount() - 1 <= 0)
                                player.getInventory().setCarriedItem(null);
                            else
                                player.getInventory().setCarriedItem(new ItemStack(carriedItem.getType(), (short) (carriedItem.getCount() - 1), carriedItem.getComponents()));
                            player.getInventory().setItem(slot, newItemStack);
                        }
                    }
                }
            }
        }
        else if (mode == 4) {
            switch (button) {
                case 0 -> {
                    // q
                    if (Configuration.get().features.debug)
                        System.out.println("q");
                }
                case 1 -> {
                    // ctrl + q
                    if (Configuration.get().features.debug)
                        System.out.println("ctrl q");
                }
            }
        }

        player.updateInventory(stateId);
        World.setEquipment(player);

//        System.out.println("mode=" + mode + " slot=" + slot + " button=" + button);
    }
}
