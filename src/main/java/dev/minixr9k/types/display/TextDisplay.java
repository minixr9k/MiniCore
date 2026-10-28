package dev.minixr9k.types.display;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.types.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextDisplay extends Display {

    private List<String> text;
    private int lineWidth;
    private int backgroundColor;
    private byte opacity;
    private boolean shadow;
    private boolean seeThrough;

    public TextDisplay() {
        text = new ArrayList<>();
        lineWidth = 200;
        opacity = -1;
        backgroundColor = 1073741824;
        shadow = false;
        seeThrough = false;
    }

    public void setText(String text) {
        Collections.addAll(this.text, text);
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(23, Metadata.TEXT_COMPONENT, text));
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

    public void setBackgroundColor(int alpha, int red, int green, int blue) {
        alpha = Math.max(0, Math.min(255, alpha));
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        setBackgroundColor(argbColor);
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(25, Metadata.VAR_INT, color));
            p.sendPacket(metadata);
        }
    }
}
