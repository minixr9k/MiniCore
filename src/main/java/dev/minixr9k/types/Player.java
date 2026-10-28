package dev.minixr9k.types;

import dev.minixr9k.auth.PlayerProfile;
import dev.minixr9k.features.World;
import dev.minixr9k.packets.beta.play.ChatMessage3Packet;
import dev.minixr9k.packets.beta.play.Disconnect255Packet;
import dev.minixr9k.packets.beta.play.PlayerPositionAndLook13Packet;
import dev.minixr9k.packets.beta.play.TimeUpdate4Packet;
import dev.minixr9k.packets.confAndPlay.ClientboundDisconnect;
import dev.minixr9k.packets.play.*;
import dev.minixr9k.types.dialog.Dialog;
import dev.minixr9k.types.hud.HudMode;
import dev.minixr9k.utils.BetaPacket;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Player extends Entity {

    private String username;
    private GameMode gameMode;
    private boolean isAllowFlight;
    private boolean isFlying;
    private boolean isLoaded;
    private int opLevel;
    private boolean isSprinting;
    private boolean isGliding;

    private int health = 20;
    private int food = 20;
    private int saturation = 20;

    private int passengerOfEntity = 0;

    private int ping;

    private List<PlayerProfile> profile;
    private String brand;

    private HudMode hudMode = HudMode.SHOW;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx;
    private int protocolVersion;

    private CustomContainer activeContainer = null;
    private int containerStateId = 1;

    public Player(ChannelHandlerContext ctx, int protocolVersion) {
        this.ctx = ctx;
        this.protocolVersion = protocolVersion;
        setInventory(new Inventory(ctx, protocolVersion));
        setEntityType("minecraft:player");
    }

    public void sendMessage(String message) {
        if (protocolVersion <= 99)
            new ChatMessage3Packet(message).send(ctx, protocolVersion, this);
        else
            new ClientboundSystemMessage(message, false).send(ctx, protocolVersion, this);
    }

    public void sendActionBar(String message) {
        if (protocolVersion <= 99)
            new ChatMessage3Packet(message).send(ctx, protocolVersion, this);
        else
            new ClientboundSystemMessage(message, true).send(ctx, protocolVersion, this);
    }

    public void sendTitle(String title, String subtitle) {
        new ClientboundSetTitleText(title).send(ctx, protocolVersion, this);
        new ClientboundSetSubTitleText(subtitle).send(ctx, protocolVersion, this);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        new ClientboundSetTitleText(title).send(ctx, protocolVersion, this);
        new ClientboundSetSubTitleText(subtitle).send(ctx, protocolVersion, this);
        new ClientboundSetTitleAnimation(fadeIn, stay, fadeOut).send(ctx, protocolVersion, this);
    }

    public void clearTitle() {
        new ClientboundClearTitle(false).send(ctx, protocolVersion, this);
    }

    public void resetTitle() {
        new ClientboundClearTitle(true).send(ctx, protocolVersion, this);
    }

    public void playSound(String sound, float volume, float pitch) {
        new ClientboundSoundEntity(sound, 0, getEntityId(), volume, pitch, System.currentTimeMillis()).send(ctx, protocolVersion, this);
    }

    public void stopSound(String sound) {
        new ClientboundStopSound((byte) 2, -1, sound).send(ctx, protocolVersion, this);
    }

    public void stopAllSounds() {
        new ClientboundStopSound((byte) 0, -1, "").send(ctx, protocolVersion, this);
    }

    public void showDialog(Dialog dialog) {
        new ClientboundShowDialog(dialog).send(ctx, protocolVersion, this);
    }

    public void closeDialog() {
        new ClientboundClearDialog().send(ctx, protocolVersion, this);
    }

    public void addPotionEffect(String effect, int duration, int amplifier) {
        new ClientboundEntityEffect(getEntityId(), effect, amplifier, duration, (byte) 0x00).send(ctx, protocolVersion, this);
    }

    public void addPotionEffect(String effect, int duration, int amplifier, byte flags) {
        new ClientboundEntityEffect(getEntityId(), effect, amplifier, duration, flags).send(ctx, protocolVersion, this);
    }

    public void removePotionEffect(String effect) {
        new ClientboundRemoveEffect(getEntityId(), effect).send(ctx, protocolVersion, this);
    }

    public void setCooldown(String material, int ticks) {
        long endTime = System.currentTimeMillis() + (ticks * 50L);
        cooldowns.put(material, endTime);

        new ClientboundSetCooldown(material, ticks).send(ctx, protocolVersion, this);
    }

    public boolean hasCooldown(String material) {
        cleanCooldowns();

        Long endTime = cooldowns.get(material);
        if (endTime == null) return false;

        return System.currentTimeMillis() < endTime;
    }

    private void cleanCooldowns() {
        long now = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    public void teleport(double x, double y, double z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        if (protocolVersion < 99) {
            new PlayerPositionAndLook13Packet(x, y, y + 1.62, z, 0, 0, false).send(ctx, protocolVersion, this);
            return;
        }
        new ClientboundSyncPlayerPos(0, x, y, z, 0, 0).send(ctx, protocolVersion, this);
    }

    @Override
    public void teleport(Location location) {
        this.setX(location.getX());
        this.setY(location.getY());
        this.setZ(location.getZ());
        this.setYaw(location.getYaw());
        this.setPitch(location.getPitch());

        if (protocolVersion < 99) {
            new PlayerPositionAndLook13Packet(location.getX(), location.getY(), location.getY() + 1.62, location.getZ(), location.getYaw(), location.getPitch(), false).send(ctx, protocolVersion, this);
            return;
        }

        new ClientboundSyncPlayerPos(0, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()).send(ctx, protocolVersion, this);
    }

    public void teleport(double x, double y, double z, float yaw, float pitch) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setYaw(yaw);
        this.setPitch(pitch);
        if (protocolVersion < 99) {
            new PlayerPositionAndLook13Packet(x, y, y + 1.62, z, yaw, pitch, false).send(ctx, protocolVersion, this);
            return;
        }
        new ClientboundSyncPlayerPos(0, x, y, z, yaw, pitch).send(ctx, protocolVersion, this);
    }

    public void setRotation(float yaw, float pitch) {
        teleport(this.x, this.y, this.z, yaw, pitch);
    }

    public void openInventory(CustomContainer inventory) {
        this.activeContainer = inventory;

        ItemStack[] playerSlots = this.getInventory().getSlots();

        for (int i = 0; i < 27; i++) {
            inventory.setItem(inventory.getSize() + i, playerSlots[9 + i]);
        }

        for (int i = 0; i < 9; i++) {
            inventory.setItem(inventory.getSize() + 27 + i, playerSlots[36 + i]);
        }

        new ClientboundOpenScreen(inventory.getWindowId(), inventory.getType(), inventory.getTitle())
                .send(ctx, protocolVersion, this);

        new ClientboundContainerSetContent(
                inventory.getWindowId(),
                containerStateId++,
                inventory.getSlots(),
                getInventory().getCarriedItem()
        ).send(ctx, protocolVersion, this);
    }

    public CustomContainer getActiveInventory() {
        return activeContainer;
    }

    public void closeInventory() {
        if (this.activeContainer != null) {
            int windowId = this.activeContainer.getWindowId();
            this.activeContainer = null;
            new ClientboundCloseContainer(windowId).send(ctx, protocolVersion, this);
        }
    }

    @Deprecated(forRemoval = true)
    public void setGamemode(GameMode gamemode) {
        this.gameMode = gamemode;

        setAllowFlight(gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR);

        new ClientboundGameEvent(3, gamemode.getId()).send(ctx, protocolVersion, this);
    }

    public void setGameMode(GameMode gamemode) {
        this.gameMode = gamemode;
        hudMode = HudMode.SHOW;

        setAllowFlight(gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR);

        new ClientboundGameEvent(3, gamemode.getId()).send(ctx, protocolVersion, this);
    }

    public void setHud(HudMode hudMode) {
        this.hudMode = hudMode;
        // отключает полоски хп, голод, броню
        if (hudMode == HudMode.HIDE_STATS) {
            new ClientboundGameEvent(3, GameMode.CREATIVE.getId()).send(ctx, protocolVersion, this);
            new ClientboundPlayerAbilities((byte) 0x00, 0.05f, 0.1f).send(ctx, protocolVersion, this);
        }
        // отключает весь hud (как в f1)
        else if (hudMode == HudMode.HIDE_ALL) {
            new ClientboundGameEvent(3, GameMode.SPECTATOR.getId()).send(ctx, protocolVersion, this);
            new ClientboundPlayerAbilities((byte) 0x00, 0.05f, 0.1f).send(ctx, protocolVersion, this);
        }
        else {
            setGameMode(gameMode);
        }
    }

    public void setHealth(int health) {
        if (health <= 0) {
            this.health = 20;
            new ClientboundSetHealth(20, food, saturation).send(ctx, protocolVersion, this);
            return;
        }
        this.health = health;
        new ClientboundSetHealth(health, food, saturation).send(ctx, protocolVersion, this);
    }

    public void setFoodLevel(int food) {
        this.food = food;
        new ClientboundSetHealth(health, food, saturation).send(ctx, protocolVersion, this);
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
        new ClientboundSetHealth(health, food, saturation).send(ctx, protocolVersion, this);
    }

    public void setLocalWorldTime(int time, boolean isTimeIncreasing) {
        if (protocolVersion < 99) {
            new TimeUpdate4Packet(time).send(ctx, protocolVersion, this);
            return;
        }
        new ClientboundUpdateTime(time, time, isTimeIncreasing).send(ctx, protocolVersion, this);
    }

    public void sendTabList(String header, String footer) {
        new ClientboundTabList(header, footer).send(ctx, protocolVersion, this);
    }

    public void sendGameEvent(int eventId, int value) {
        new ClientboundGameEvent(eventId, value).send(ctx, protocolVersion, this);
    }

    public void setAttribute(String attribute, double value) {
        World.updateAttribute(getEntityId(), attribute, value);
    }

    public void setLocalAttribute(String attribute, double value) {
        new ClientboundUpdateAttributes(getEntityId(), attribute, value).send(ctx, protocolVersion, this);
    }

//    public void resetAttribute(String attribute) {
//    }

    public void sendPluginMessage(String channel, ByteBuf buf) {
        new ClientboundPluginMessage(channel, buf).send(ctx, protocolVersion, this);
    }

    public void sendPacket(MinecraftPacket packet) {
        packet.send(ctx, protocolVersion, this);
    }

    public void sendPacket(BetaPacket packet) {
        packet.send(ctx, protocolVersion, this);
    }

    public void kick(String reason) {
        if (protocolVersion < 99) {
            new Disconnect255Packet(reason).send(ctx, protocolVersion, this);
            ctx.close();
            return;
        }
        new ClientboundDisconnect(reason).send(ctx, protocolVersion, this);
        ctx.close();
    }

    public void setOpLevel(int opLevel) {
        this.opLevel = opLevel;
        switch (opLevel) {
            case 0:
                new ClientboundEntityEvent(getEntityId(), (byte) 24).send(ctx, protocolVersion, this);
                break;
            case 1:
                new ClientboundEntityEvent(getEntityId(), (byte) 25).send(ctx, protocolVersion, this);
                break;
            case 2:
                new ClientboundEntityEvent(getEntityId(), (byte) 26).send(ctx, protocolVersion, this);
                break;
            case 3:
                new ClientboundEntityEvent(getEntityId(), (byte) 27).send(ctx, protocolVersion, this);
                break;
            case 4:
                new ClientboundEntityEvent(getEntityId(), (byte) 28).send(ctx, protocolVersion, this);
                break;
        }
    }

    public void setOpLevelNoPacket(int opLevel) {
        this.opLevel = opLevel;
    }

    public int getOpLevel() { return opLevel; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSystemGameMode(GameMode gameMode) { this.gameMode = gameMode; }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean isAllowFlight() {
        return isAllowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        isAllowFlight = allowFlight;
        if (allowFlight)
            new ClientboundPlayerAbilities((byte) 0x04, 0.05f, 0.1f).send(ctx, protocolVersion, this);
        else {
            if (hudMode != HudMode.SHOW) {
                setHud(hudMode);
                return;
            }

            switch (this.gameMode) {
                case ADVENTURE -> {
                    new ClientboundGameEvent(3, 2).send(ctx, protocolVersion, this);
                }
                case CREATIVE -> {
                    new ClientboundGameEvent(3, 1).send(ctx, protocolVersion, this);
                }
                case SURVIVAL -> {
                    new ClientboundGameEvent(3, 0).send(ctx, protocolVersion, this);
                }
                case SPECTATOR -> {
                    new ClientboundGameEvent(3, 3).send(ctx, protocolVersion, this);
                }
            }
        }
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
        if (flying)
            new ClientboundPlayerAbilities((byte) 0x02, 0.05f, 0.1f).send(ctx, protocolVersion, this);
        else {
            if (hudMode != HudMode.SHOW) {
                setHud(hudMode);
                return;
            }

            switch (this.gameMode) {
                case ADVENTURE -> {
                    new ClientboundGameEvent(3, 2).send(ctx, protocolVersion, this);
                }
                case CREATIVE -> {
                    new ClientboundGameEvent(3, 1).send(ctx, protocolVersion, this);
                }
                case SURVIVAL -> {
                    new ClientboundGameEvent(3, 0).send(ctx, protocolVersion, this);
                }
                case SPECTATOR -> {
                    new ClientboundGameEvent(3, 3).send(ctx, protocolVersion, this);
                }
            }
        }
    }

    public void updateInventory(int stateId) {
        new ClientboundContainerSetContent(0, stateId, getInventory().getSlots(), getInventory().getCarriedItem()).send(ctx, protocolVersion, this);
    }

    public Location getEyeLocation() {
        return getLocation().add(0, 1.62, 0);
    }

    public Vector3f getLookDirection() {
        float yawRad = (float) Math.toRadians(this.getYaw());
        float pitchRad = (float) Math.toRadians(this.getPitch());

        float x = (float) (-Math.sin(yawRad) * Math.cos(pitchRad));
        float y = (float) -Math.sin(pitchRad);
        float z = (float) (Math.cos(yawRad) * Math.cos(pitchRad));

        return new Vector3f(x, y, z);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public int getProtocolVersion() { return protocolVersion; }

    public void setProtocolVersion(int protocolVersion) { this.protocolVersion = protocolVersion; }

    public List<PlayerProfile> getProfile() { return profile; }

    public void setProfile(List<PlayerProfile> profile) { this.profile = profile; }

    public int getPassengerOfEntity() {
        return passengerOfEntity;
    }

    public void setPassengerOfEntity(int passengerOfEntity) {
        this.passengerOfEntity = passengerOfEntity;
    }

    public int getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public int getSaturation() {
        return saturation;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    public boolean isGliding() {
        return isGliding;
    }

    public void setGliding(boolean gliding) {
        isGliding = gliding;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

}
