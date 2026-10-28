package dev.minixr9k.types.display;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.types.ItemStack;
import dev.minixr9k.types.Metadata;
import dev.minixr9k.types.MetadataEntry;
import dev.minixr9k.types.Player;

public class ItemDisplay extends Display {

    private ItemStack displayedItem;
    private DisplayType displayType;

    public enum DisplayType {
        NONE(0),
        THIRD_PERSON_LEFT_HAND(1),
        THIRD_PERSON_RIGHT_HAND(2),
        FIRST_PERSON_LEFT_HAND(3),
        FIRST_PERSON_RIGHT_HAND(4),
        HEAD(5),
        GUI(6),
        GROUND(7),
        FIXED(8)
        ;

        private final int id;

        DisplayType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public ItemDisplay() {
        this.displayedItem = null;
        this.displayType = DisplayType.NONE;
    }

    public ItemStack getItemStack() {
        return displayedItem;
    }

    public void setItemStack(ItemStack displayedItem) {
        this.displayedItem = displayedItem;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(23, Metadata.SLOT, displayedItem));
            p.sendPacket(metadata);
        }
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(24, Metadata.BYTE, (byte) displayType.getId()));
            p.sendPacket(metadata);
        }
    }
}
