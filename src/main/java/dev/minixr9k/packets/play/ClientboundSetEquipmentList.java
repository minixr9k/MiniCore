package dev.minixr9k.packets.play;

import dev.minixr9k.registries.ItemRegistry;
import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.Inventory;
import dev.minixr9k.types.ItemComponent;
import dev.minixr9k.types.ItemStack;
import dev.minixr9k.types.components.ConsumableComponent;
import dev.minixr9k.types.components.FoodComponent;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.*;
import static dev.minixr9k.utils.ProtocolUtils.writeString;
import static dev.minixr9k.utils.ProtocolUtils.writeTextComponent;

public class ClientboundSetEquipmentList implements MinecraftPacket {

    private final int entityId;
    private final Inventory inventory;

    public ClientboundSetEquipmentList(int entityId, Inventory inventory) {
        this.entityId = entityId;
        this.inventory = inventory;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, entityId);

        for (int i = 0; i < 6; i++) {
            if (i == 1) continue;
            ItemStack itemStack = null;
            switch (i) {
                case 0 -> itemStack = inventory.getItemInMainHand();
                case 2 -> itemStack = inventory.getBoots();
                case 3 -> itemStack = inventory.getLeggings();
                case 4 -> itemStack = inventory.getChestplate();
                case 5 -> itemStack = inventory.getHelmet();
            }

            out.writeByte(i);

            if (itemStack == null || itemStack.getType() == null || itemStack.getType().isEmpty())
                out.writeBoolean(false);
            else {
                writeVarInt(out, itemStack.getCount()); // item count
                int item = Registry.getId(itemStack.getType(), RegistryType.ITEMS, protocolVersion);
                writeVarInt(out, item); // item id
                List<ItemComponent> components = itemStack.getComponents();
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
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        if (protocolVersion > 772)
            return 0x64;
        return 0x5F;
    }
}
