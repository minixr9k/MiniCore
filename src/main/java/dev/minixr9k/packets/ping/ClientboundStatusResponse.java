package dev.minixr9k.packets.ping;

import dev.minixr9k.api.EventBus;
import dev.minixr9k.api.event.ServerListPingEvent;
import dev.minixr9k.config.Configuration;
import dev.minixr9k.features.World;
import dev.minixr9k.types.ping.PlayerSample;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.UUID;

import static dev.minixr9k.utils.ProtocolUtils.writeString;

public class ClientboundStatusResponse implements MinecraftPacket {

    @Override
    public void read(ByteBuf in, int protocolVersion) {}

    @Override
    public void write(ByteBuf out, int protocolVersion) {
        String motd = Configuration.get().motd.text;
        int online = World.getAllPlayers().size();
        int max = Configuration.get().motd.maxPlayers;
        String favicon = Configuration.getServerIcon();
        int serverProtocol = Configuration.get().features.mainProtocol;

        ServerListPingEvent event = new ServerListPingEvent(
                null, protocolVersion, motd, online, max, favicon, serverProtocol
        );

        EventBus.getInstance().callEvent(event);

        if (event.isCancelled()) {
            event.getCtx().close();
            return;
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"version\":{\"name\":\"MiniCore\",\"protocol\":").append(event.getServerProtocol()).append("},");
        json.append("\"players\":{");
        json.append("\"max\":").append(event.getMaxPlayers()).append(",");
        json.append("\"online\":").append(event.getOnlinePlayers());

        if (!event.getSamplePlayers().isEmpty()) {
            json.append(",\"sample\":[");
            for (int i = 0; i < event.getSamplePlayers().size(); i++) {
                PlayerSample player = event.getSamplePlayers().get(i);
                if (i > 0) json.append(",");
                json.append("{\"name\":\"").append(player.getName()).append("\",");
                json.append("\"id\":\"").append(player.getId().toString()).append("\"}");
            }
            json.append("]");
        }
        json.append("},");
        json.append("\"description\":{\"text\":\"").append(event.getMotd()).append("\"}");

        if (event.getFavicon() != null && !event.getFavicon().isEmpty()) {
            json.append(",\"favicon\":\"").append(event.getFavicon()).append("\"");
        }

        json.append("}");

        writeString(out, json.toString());
    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x00;
    }
}
