package dev.minixr9k.types;

import dev.minixr9k.features.World;
import dev.minixr9k.packets.play.ClientboundRemoveEntity;
import dev.minixr9k.packets.play.ClientboundSetEntityMetadata;
import dev.minixr9k.packets.play.ClientboundSetEntityVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Entity {

    private int entityId;
    private UUID uuid;
    private String entityType;

    public double x, y, z;
    private float yaw, pitch;
    private boolean isSneaking;
    private boolean isInvisible;
    private boolean isOnFire;

    private double prevX, prevY, prevZ;
    private float prevYaw, prevPitch;

    private boolean onGround = false;

    private List<Entity> passengers = new ArrayList<>();
    private Entity vehicle;

    private Inventory inventory;

    private boolean lookingAtPlayer;

    public void addPassenger(Entity passenger) {
        passengers.add(passenger);
        passenger.setVehicle(this);

        World.updatePassengers(this);
    }

    public void setPassengers(List<Entity> passenger) {
        for (Entity en : passenger) {
            en.setVehicle(this);
        }

        passengers = passenger;

        World.updatePassengers(this);
    }

    public void removePassenger(Entity passenger) {
        passenger.setVehicle(null);

        passengers.remove(passenger);

        World.updatePassengers(this);
    }

    public void removeAllPassengers() {
        for (Entity en : passengers) {
            en.setVehicle(null);
        }

        passengers.clear();

        World.updatePassengers(this);
    }

    public void leaveVehicle() {
        Entity en = getVehicle();
        if (en == null) return;

        en.removePassenger(this);
    }

    public Entity getVehicle() {
        return vehicle;
    }

    public void setVehicle(Entity vehicle) {
        this.vehicle = vehicle;
    }

    public void setVelocity(double x, double y, double z) {
        for (Player player : World.getAllPlayers()) {
            player.sendPacket(new ClientboundSetEntityVelocity(this.getEntityId(), x, y, z));
        }
    }

    public void teleport(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        World.moveEntity(this);
    }

    public void remove() {
        World.removeEntity(this.getEntityId());
    }

    public void remove(Player observer) {
        observer.sendPacket(new ClientboundRemoveEntity(this.getEntityId()));
    }

    public void updatePrevPosition() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    public float getPrevYaw() {
        return prevYaw;
    }

    public float getPrevPitch() {
        return prevPitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public double getVelocityX() {
        return this.x - this.prevX;
    }

    public double getVelocityY() {
        return this.y - this.prevY;
    }

    public double getVelocityZ() {
        return this.z - this.prevZ;
    }

    public double getHorizontalVelocity() {
        double velX = getVelocityX();
        double velZ = getVelocityZ();
        return Math.sqrt(velX * velX + velZ * velZ);
    }

    public short getNetworkVelocityX() {
        double velX = getVelocityX();
        return (short) (clamp(velX, -3.9, 3.9) * 8000.0);
    }

    public short getNetworkVelocityY() {
        double velY = getVelocityY();
        return (short) (clamp(velY, -3.9, 3.9) * 8000.0);
    }

    public short getNetworkVelocityZ() {
        double velZ = getVelocityZ();
        return (short) (clamp(velZ, -3.9, 3.9) * 8000.0);
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public void setSneaking(boolean sneaking) {
        isSneaking = sneaking;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void setInvisible(boolean invisible) {
        this.isInvisible = invisible;

        ClientboundSetEntityMetadata packet = new ClientboundSetEntityMetadata(getEntityId());
        if (invisible) {
            packet.add(new MetadataEntry<>(0, Metadata.BYTE, (byte) 0x20));
        }
        else {
            packet.add(new MetadataEntry<>(0, Metadata.BYTE, (byte) 0x00));
        }

        for (Player p : World.getAllPlayers()) {
            if (p.getProtocolVersion() < 99) continue;
            packet.send(p.getCtx(), p.getProtocolVersion(), p);
        }
    }

    public void setInvisible(boolean invisible, Player observer) {
        this.isInvisible = invisible;

        ClientboundSetEntityMetadata packet = new ClientboundSetEntityMetadata(getEntityId());
        if (invisible) {
            packet.add(new MetadataEntry<>(0, Metadata.BYTE, (byte) 0x20));
        }
        else {
            packet.add(new MetadataEntry<>(0, Metadata.BYTE, (byte) 0x00));
        }

        observer.sendPacket(packet);
    }

    public boolean isOnFire() {
        return isOnFire;
    }

    public void setOnFire(boolean onFire) {
        isOnFire = onFire;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
}
