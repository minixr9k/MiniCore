package dev.minixr9k.types.display;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.registries.BlockRegistry;
import dev.minixr9k.types.Metadata;
import dev.minixr9k.types.MetadataEntry;
import dev.minixr9k.types.Player;

public class BlockDisplay extends Display {

    private int blockState;

    public BlockDisplay() {
        this.blockState = 0;  // air
    }

    public int getBlockState() {
        return blockState;
    }

    public void setBlockState(int blockState) {
        this.blockState = blockState;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(23, Metadata.BLOCK_STATE, blockState));
            p.sendPacket(metadata);
        }
    }

    public void setBlock(String type) {
        this.blockState = BlockRegistry.getBlock(type, 772);
        setBlockState(blockState);
    }
}
