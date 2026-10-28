package dev.minixr9k;

import dev.minixr9k.config.Configuration;
import dev.minixr9k.config.PluginLoader;

public class Bootstrap {

    public static void main(String[] args) {
        Configuration.loadConfig();

        MiniCore core = new MiniCore();
        core.start();

        Runtime.getRuntime().addShutdownHook(new Thread(core::shutdown));
    }
}