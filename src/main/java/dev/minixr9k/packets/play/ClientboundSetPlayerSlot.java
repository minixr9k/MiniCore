package dev.minixr9k.packets.play;

import dev.minixr9k.registries.Registry;
import dev.minixr9k.types.ItemComponent;
import dev.minixr9k.types.ItemStack;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.*;

public class ClientboundSetPlayerSlot implements MinecraftPacket {

    private final int slot;
    private final ItemStack itemStack;

    public ClientboundSetPlayerSlot(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        writeVarInt(out, slot); // item slot
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
            return 0x6A;
        return 0x65;
    }
}
