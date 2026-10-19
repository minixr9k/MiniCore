package dev.minixr9k.packets.play;

import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.ItemComponent;
import dev.minixr9k.types.ItemStack;
import dev.minixr9k.types.components.ConsumableComponent;
import dev.minixr9k.types.components.FoodComponent;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.*;

public class ClientboundContainerSetContent implements MinecraftPacket {
    private final int windowId;
    private final int stateId;
    private final ItemStack[] slots;
    private final ItemStack carriedItem;

    public ClientboundContainerSetContent(int windowId, int stateId, ItemStack[] slots, ItemStack carriedItem) {
        this.windowId = windowId;
        this.stateId = stateId;
        this.slots = slots;
        this.carriedItem = carriedItem;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, windowId);
        writeVarInt(out, stateId);

        // Prefixed Array
        writeVarInt(out, slots.length);
        for (ItemStack item : slots) {
            writeItemStack(out, item, protocolVersion);
        }

        // Carried Item
        writeItemStack(out, carriedItem, protocolVersion);
    }

    private void writeItemStack(ByteBuf out, ItemStack item, int protocolVersion) {
        if (item == null || item.getCount() <= 0 || "minecraft:air".equalsIgnoreCase(item.getType())) {
            out.writeBoolean(false); // Слот пустой
        } else {
            int itemId = Registry.getId(item.getType(), RegistryType.ITEMS, protocolVersion);
            writeVarInt(out, item.getCount());
            writeVarInt(out, itemId);

            List<ItemComponent> components = item.getComponents();
            if (components != null && !components.isEmpty()) {
                // Количество добавляемых компонентов
                writeVarInt(out, components.size());

                // Количество удаляемых компонентов
                writeVarInt(out, 0);

                // Компоненты
                for (ItemComponent component : components) {
                    writeVarInt(out, Registry.getId(component.getComponentType(), RegistryType.DATA_COMPONENTS, protocolVersion));
                    encodeComponent(out, component);
                }
            } else {
                writeVarInt(out, 0); // Добавлено 0
                writeVarInt(out, 0); // Удалено 0
            }
        }
    }

    private void encodeComponent(ByteBuf out, ItemComponent component) {
        String type = component.getComponentType().toLowerCase();

        switch (type) {
            case "minecraft:custom_model_data" -> {
                writeVarInt(out, 1);
                out.writeFloat(component.getValueAsFloat());
                writeVarInt(out, 0);
                writeVarInt(out, 0);
                writeVarInt(out, 0);
            }
            case "minecraft:profile" -> {
                out.writeBoolean(false); // optional username
                out.writeBoolean(false); // optional uuid

                writeVarInt(out, 1); // properties count
                writeString(out, "textures");
                writeString(out, component.getValueAsString());
                out.writeBoolean(false); // signature empty
            }
            case "minecraft:custom_name" -> {
                writeTextComponent(out, component.getValueAsString());
            }
            case "minecraft:unbreakable", "minecraft:glider" -> {
                // no fields
            }
            case "minecraft:damage", "minecraft:max_damage", "minecraft:max_stack_size" -> {
                writeVarInt(out, component.getValueAsInt());
            }
            case "minecraft:food" -> {
                FoodComponent foodComponent = component.getValueAsFood();
                if (foodComponent != null) {
                    writeVarInt(out, foodComponent.nutrition());
                    out.writeFloat(foodComponent.saturation());
                    out.writeBoolean(foodComponent.canAlwaysEat());
                }
            }
            case "minecraft:consumable" -> {
                ConsumableComponent consumableComponent = component.getValueAsConsumable();
                if (consumableComponent != null) {
                    out.writeFloat(consumableComponent.consumeSeconds());
                    writeVarInt(out, consumableComponent.animation());
                    // sound effect
                    writeVarInt(out, 0);
                    writeString(out, consumableComponent.sound());
                    out.writeBoolean(false); // has fixed range
                    //
                    out.writeBoolean(consumableComponent.hasParticles());
                    writeVarInt(out, 0); // effects (Prefixed Array)
                }
            }
            case "minecraft:lore" -> {
                List<String> lore = component.getValueAsListString();
                writeVarInt(out, lore.size());

                for (String line : lore) {
                    writeTextComponent(out, line);
                }
            }
        }
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {}

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x12;
    }
}
