package dev.minixr9k.types.textdisplay;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.types.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextDisplay extends Entity {

    private List<String> text;
    private Billboard billboard;
    private int lineWidth;
    private byte opacity;
    private boolean shadow;
    private boolean seeThrough;
    private Vector4f rotation;
    private Vector3f scale;

    public enum Billboard {
        FIXED((byte)0),
        VERTICAL((byte)1),
        HORIZONTAL((byte)2),
        CENTER((byte)3);

        private final byte id;

        Billboard(byte id) {
            this.id = id;
        }

        public byte getId() {
            return id;
        }
    }

    public TextDisplay() {
        text = new ArrayList<>();
        billboard = Billboard.FIXED;
        lineWidth = 200;
        opacity = -1;
        shadow = false;
        seeThrough = false;
        rotation = new Vector4f(0, 0, 0, 1);
        scale = new Vector3f(1, 1, 1);
    }

    public void setText(String text) {
        Collections.addAll(this.text, text);
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(23, Metadata.TEXT_COMPONENT, text));
            p.sendPacket(metadata);
        }
    }

    public void setRotation(Vector4f rotation) {
        this.rotation = rotation;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(13, Metadata.QUATERNION, rotation));
            p.sendPacket(metadata);
        }
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(12, Metadata.VECTOR3F, scale));
            p.sendPacket(metadata);
        }
    }

    public void setLineWidth(int width) {
        lineWidth = width;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(24, Metadata.VAR_INT, width));
            p.sendPacket(metadata);
        }
    }

    public void setTextOpacity(byte opacity) {
        this.opacity = opacity;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(26, Metadata.BYTE, opacity));
            p.sendPacket(metadata);
        }
    }

    public void setBillboard(Billboard billboard) {
        this.billboard = billboard;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(15, Metadata.BYTE, billboard.getId()));
            p.sendPacket(metadata);
        }
    }

    public void setShadowed(boolean shadow) {
        this.shadow = shadow;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(27, Metadata.BYTE, shadow ? (byte) 0x01 : (byte) 0x00));
            p.sendPacket(metadata);
        }
    }

    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(27, Metadata.BYTE, seeThrough ? (byte) 0x02 : (byte) 0x00));
            p.sendPacket(metadata);
        }
    }

    public void setBrightness() {
    }

    public void setBackgroundColor() {
    }
}
