package dev.minixr9k.network;

import dev.minixr9k.MiniCore;
import dev.minixr9k.handlers.HandshakeState;
import dev.minixr9k.network.codec.MinecraftFrameDecoder;
import dev.minixr9k.utils.ClientType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NetworkServer {
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NetworkServer(int port) {
        this.port = port;
    }

    public void start() {
        boolean useEpoll = Epoll.isAvailable();

        MiniCore.getLogger().info("[Core/Network] Ивент-луп выбран: " + (useEpoll ? "Epoll (Linux Native)" : "NIO (Java)"));

        bossGroup = useEpoll ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        workerGroup = useEpoll ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("frame_decoder", new MinecraftFrameDecoder());
                            ch.pipeline().addLast("handler", new HandshakeState(ClientType.MODERN));
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            MiniCore.getLogger().info("[Core/Done] Server bound to port " + port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}