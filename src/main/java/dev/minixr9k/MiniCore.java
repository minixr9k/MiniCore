package dev.minixr9k;

import dev.minixr9k.config.Configuration;
import dev.minixr9k.config.PluginLoader;
import dev.minixr9k.features.World;
import dev.minixr9k.logging.Logger;
import dev.minixr9k.network.NetworkServer;
import dev.minixr9k.scheduler.ServerThread;
import dev.minixr9k.scheduler.TaskScheduler;
import dev.minixr9k.types.Player;

import java.util.Scanner;

public class MiniCore {
    private static MiniCore instance;

    private final Logger logger = new Logger();
    private final TaskScheduler scheduler = new TaskScheduler();

    private ServerThread serverThread;
    private NetworkServer networkServer;

    private Thread consoleThread;
    private volatile boolean running = true;

    public MiniCore() {
        instance = this;
    }

    public void start() {
        this.serverThread = new ServerThread();
        this.serverThread.start();
        logger.info("[Core/ServerThread] ServerThread запущен!");

        PluginLoader.loadPlugins();

        World.initWorld(Configuration.get().world.chunks, Configuration.get().world.grassChunks);

        this.networkServer = new NetworkServer(Configuration.get().port);
        this.networkServer.start();

        startConsoleReader();
    }

    private void startConsoleReader() {
        consoleThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (running && scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        executeCommand(line);
                    }
                }
            } catch (Exception e) {
                if (running) {
                    logger.warn("[Console] Ошибка чтения консоли: " + e.getMessage());
                }
            }
        }, "Console-Reader");

        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public void executeCommand(String command) {
        if (command.startsWith("reload")) {
            PluginLoader.disablePlugins();
            PluginLoader.loadPlugins();
            logger.info("[Core/Console] Плагины перезагружены!");
        }

        if (command.startsWith("kick")) {
            if (command.length() < 6) return;
            Player target = World.getPlayer(command.substring(5));
            if (target != null) {
                target.kick("Kicked by operator");
            }
            else {
                logger.info("[Core/Console] Игрок не в сети!");
            }
        }

        if (command.startsWith("say")) {
            if (command.length() < 5) return;
            String message = command.substring(4);
            World.broadcast("[Server] " + message);
        }

        if (command.equals("tps")) {
            logger.info("[Core/Console] §6TPS: §f" + MiniCore.getServerThread().getTps());
        }

        if (command.equals("mspt")) {
            logger.info("[Core/Console] §6MSPT: §f" + MiniCore.getServerThread().getMspt());
        }

        if (command.equals("save-all")) {
            World.broadcast("[ChunkHolder] Сохранение мира...");
            World.saveWorld();
            World.broadcast("[ChunkHolder] Успешно сохранен!");
        }

        if (command.equals("stop")) {
            System.exit(0);
        }
    }

    public void shutdown() {
        running = false;
        logger.info("[ChunkHolder] Выключение сервера... Сохраняем мир...");

        for (Player player : World.getAllPlayers()) {
            player.kick("Server is shutting down");
        }

        if (networkServer != null) {
            networkServer.stop();
        }
        if (serverThread != null) {
            serverThread.shutdown();
        }
        World.saveWorld();

        logger.info("[Core] Сервер успешно выключен.");
    }

    public static MiniCore getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return instance.scheduler;
    }

    public static ServerThread getServerThread() {
        return instance.serverThread;
    }

    public static Logger getLogger() {
        return instance.logger;
    }
}