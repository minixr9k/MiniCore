package dev.minixr9k;

import dev.minixr9k.config.Configuration;
import dev.minixr9k.config.PluginLoader;
import dev.minixr9k.features.World;
import dev.minixr9k.network.ServerThread;
import dev.minixr9k.scheduler.TaskScheduler;

public class MiniCore {

    private static ServerThread serverThread;
    private static NetworkServer networkServer;
    private static final TaskScheduler scheduler = new TaskScheduler();

    public static void main(String[] args) {
        Configuration.loadConfig();
        PluginLoader.loadPlugins();
        World.initWorld(Configuration.get().world.chunks, Configuration.get().world.grassChunks);

        serverThread = new ServerThread();
        serverThread.start();
        System.out.println("[Core/ServerThread] ServerThread запущен!");

        networkServer = new NetworkServer(Configuration.get().port);
        networkServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(MiniCore::shutdown));
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    public static ServerThread getServerThread() {
        return serverThread;
    }

    public static void shutdown() {
        System.out.println("[ChunkHolder] Выключение сервера... Сохраняем мир...");
        if (networkServer != null) {
            networkServer.stop();
        }
        if (serverThread != null) {
            serverThread.shutdown();
        }
        World.saveWorld();
    }

}
