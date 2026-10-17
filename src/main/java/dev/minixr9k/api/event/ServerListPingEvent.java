package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.ping.PlayerSample;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class ServerListPingEvent extends Event implements Cancellable {

    private final ChannelHandlerContext ctx;
    private final int protocolVersion;

    private String motd;
    private int onlinePlayers;
    private int maxPlayers;
    private String favicon;
    private int serverProtocol;

    private List<PlayerSample> samplePlayers = new ArrayList<>();

    private boolean cancelled;

    public ServerListPingEvent(ChannelHandlerContext ctx, int protocolVersion, String motd, int onlinePlayers, int maxPlayers, String favicon, int serverProtocol) {
        this.ctx = ctx;
        this.protocolVersion = protocolVersion;
        this.motd = motd;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.favicon = favicon;
        this.serverProtocol = serverProtocol;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public int getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(int serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public List<PlayerSample> getSamplePlayers() {
        return samplePlayers;
    }

    public void setSamplePlayers(List<PlayerSample> samplePlayers) {
        this.samplePlayers = samplePlayers;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}