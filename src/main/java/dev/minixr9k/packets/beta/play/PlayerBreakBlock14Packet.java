package dev.minixr9k.packets.beta.play;

import dev.minixr9k.utils.BetaPacket;
import io.netty.buffer.ByteBuf;

public class PlayerBreakBlock14Packet implements BetaPacket {

    private byte status;
    private int x;
    private byte y;
    private int z;
    private byte face;

    public PlayerBreakBlock14Packet() {}

    @Override
    public void write(ByteBuf out, int protocolVersion) {

    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {
        this.status = in.readByte();
        this.x = in.readInt();
        this.y = in.readByte();
        this.z = in.readInt();
        this.face = in.readByte();
    }

    public byte getStatus() {
        return status;
    }

    public int getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public byte getFace() {
        return face;
    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x0E;
    }
}
