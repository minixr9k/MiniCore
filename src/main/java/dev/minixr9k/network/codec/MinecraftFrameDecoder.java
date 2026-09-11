package dev.minixr9k.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static dev.minixr9k.utils.ProtocolUtils.readVarInt;

public class MinecraftFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex(); // запоминаем текущую позицию чтения

        int length = readVarInt(in);

        if (length == -1) {
            in.resetReaderIndex(); // восстанавливаем позицию чтения
            return;
        }

        // если пакет не дошел целиком :3
        if (in.readableBytes() < length) {
            in.resetReaderIndex(); // восстанавливаем позицию чтения
            return;
        }

        // Байт в пакете достаточно!
        ByteBuf packetBuf = in.readRetainedSlice(length);
        out.add(packetBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }
}
