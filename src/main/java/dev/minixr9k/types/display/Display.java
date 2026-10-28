package dev.minixr9k.types.display;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.types.*;

public class Display extends Entity {

    private int interpolationDelay;
    private int interpolationTransfDuration;
    private int interpolationPosRotDuration;

    private Vector4f rotation;
    private Vector3f scale;
    private Vector3f translation;

    private Billboard billboard;

    private int brightnessOverride;

    private float viewRange;
    private float shadowRadius;
    private float shadowStrength;
    private float width;
    private float height;
    private int glowColorOverride;

    public Display() {
        interpolationDelay = 0;
        interpolationTransfDuration = 0;
        interpolationPosRotDuration = 0;

        rotation = new Vector4f(0, 0, 0, 1);
        scale = new Vector3f(1, 1, 1);
        translation = new Vector3f(0, 0, 0);

        billboard = Billboard.FIXED;

        brightnessOverride = -1;

        viewRange = 3f;
        shadowRadius = 0f;
        shadowStrength = 1f;
        width = 0;
        height = 0;
        glowColorOverride = -1;
    }

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

    public void setInterpolationDelay(int interpolationDelay) {
        this.interpolationDelay = interpolationDelay;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(8, Metadata.VAR_INT, interpolationDelay));
            p.sendPacket(metadata);
        }
    }

    public void setInterpolationTransfDuration(int interpolationTransfDuration) {
        this.interpolationTransfDuration = interpolationTransfDuration;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(9, Metadata.VAR_INT, interpolationTransfDuration));
            p.sendPacket(metadata);
        }
    }

    public void setInterpolationPosRotDuration(int interpolationPosRotDuration) {
        this.interpolationPosRotDuration = interpolationPosRotDuration;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(10, Metadata.VAR_INT, interpolationPosRotDuration));
            p.sendPacket(metadata);
        }
    }

    public void setBrightnessOverride(int brightnessOverride) {
        this.brightnessOverride = brightnessOverride;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(16, Metadata.VAR_INT, brightnessOverride));
            p.sendPacket(metadata);
        }
    }

    public void setRotation(float pitch, float yaw, float roll) {
        float p = (float) Math.toRadians(pitch) * 0.5f;
        float y = (float) Math.toRadians(yaw) * 0.5f;
        float r = (float) Math.toRadians(roll) * 0.5f;

        float cp = (float) Math.cos(p);
        float sp = (float) Math.sin(p);
        float cy = (float) Math.cos(y);
        float sy = (float) Math.sin(y);
        float cr = (float) Math.cos(r);
        float sr = (float) Math.sin(r);

        float x = sp * cy * cr + cp * sy * sr;
        float yVal = cp * sy * cr - sp * cy * sr;
        float z = cp * cy * sr - sp * sy * cr;
        float w = cp * cy * cr + sp * sy * sr;

        this.rotation = new Vector4f(x, yVal, z, w);
        sendRotationPacket();
    }

    public void setRotation(Vector4f rotation) {
        this.rotation = rotation;
        sendRotationPacket();
    }

    private void sendRotationPacket() {
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

    public void setTranslation(Vector3f translation) {
        this.translation = translation;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(11, Metadata.VECTOR3F, translation));
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

    public void setViewRange(float viewRange) {
        this.viewRange = viewRange;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(17, Metadata.FLOAT, viewRange));
            p.sendPacket(metadata);
        }
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(18, Metadata.FLOAT, shadowRadius));
            p.sendPacket(metadata);
        }
    }

    public void setShadowStrength(float shadowStrength) {
        this.shadowStrength = shadowStrength;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(19, Metadata.FLOAT, shadowStrength));
            p.sendPacket(metadata);
        }
    }

    public void setWidth(float width) {
        this.width = width;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(20, Metadata.FLOAT, width));
            p.sendPacket(metadata);
        }
    }

    public void setHeight(float height) {
        this.height = height;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(21, Metadata.FLOAT, height));
            p.sendPacket(metadata);
        }
    }

    public void setGlowColorOverride(int glowColorOverride) {
        this.glowColorOverride = glowColorOverride;

        for (Player p : World.getAllPlayers()) {
            ClientboundSetEntityMetadata metadata = new ClientboundSetEntityMetadata(getEntityId());
            metadata.add(new MetadataEntry<>(22, Metadata.VAR_INT, glowColorOverride));
            p.sendPacket(metadata);
        }
    }

    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    public int getInterpolationTransfDuration() {
        return interpolationTransfDuration;
    }

    public int getInterpolationPosRotDuration() {
        return interpolationPosRotDuration;
    }

    public Vector4f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Billboard getBillboard() {
        return billboard;
    }

    public int getBrightnessOverride() {
        return brightnessOverride;
    }

    public float getViewRange() {
        return viewRange;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getGlowColorOverride() {
        return glowColorOverride;
    }
}
