package dev.minixr9k.utils;

import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.writeNBTString;

public class NbtUtils {

    public static void writeTagEnd(ByteBuf buf) {
        buf.writeByte(0);
    }

    public static void writeByte(ByteBuf buf, String identifier, int value) {
        buf.writeByte(1);
        writeNBTString(buf, identifier);
        buf.writeByte(value);
    }

    public static void writeShort(ByteBuf buf, String identifier, int value) {
        buf.writeByte(2);
        writeNBTString(buf, identifier);
        buf.writeShort(value);
    }

    public static void writeInt(ByteBuf buf, String identifier, int value) {
        buf.writeByte(3);
        writeNBTString(buf, identifier);
        buf.writeInt(value);
    }

    public static void writeLong(ByteBuf buf, String identifier, int value) {
        buf.writeByte(4);
        writeNBTString(buf, identifier);
        buf.writeLong(value);
    }

    public static void writeFloat(ByteBuf buf, String identifier, float value) {
        buf.writeByte(5);
        writeNBTString(buf, identifier);
        buf.writeFloat(value);
    }

    public static void writeDouble(ByteBuf buf, String identifier, double value) {
        buf.writeByte(6);
        writeNBTString(buf, identifier);
        buf.writeDouble(value);
    }

    public static void writeString(ByteBuf buf, String identifier, String value) {
        buf.writeByte(8);
        writeNBTString(buf, identifier);
        writeNBTString(buf, value);
    }

    public static void writeTagList(ByteBuf buf, String identifier) {
        buf.writeByte(9);
        writeNBTString(buf, identifier);
    }

    public static void writeTagCompound(ByteBuf buf) {
        buf.writeByte(10);
    }

}
