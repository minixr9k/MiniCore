package dev.minixr9k.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import dev.minixr9k.json.JConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class Configuration {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JConfig config;
    private static String serverIcon = "";

    public static void loadConfig() {
        try (FileReader reader = new FileReader("configuration.json")) {

            config = gson.fromJson(reader, JConfig.class);
            System.out.println("[Core/Config] Конфиг успещно загружен!");
            loadIcon();

        } catch (FileNotFoundException e) {
            System.out.println("[Core/Config] Конфиг не найден! Создаем новый...");
            config = new JConfig();
            saveConfig();
            loadIcon();
        } catch (IOException e) {
            throw new RuntimeException("[Core/Config] Ошибка при чтении конфигурации", e);
        }
    }

    public static void loadIcon() {
        try {
            File file = new File("server-icon.png");
            if (!file.exists()) {
                if (Configuration.get().features.debug)
                    System.out.println("[Core/Config] Иконка сервера (server-icon.png) не найдена!");
                return;
            }

            BufferedImage icon = ImageIO.read(file);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageIO.write(icon, "png", bs);

            serverIcon = "data:image/png;base64," + Base64.getEncoder().encodeToString(bs.toByteArray());

            if (Configuration.get().features.debug)
                System.out.println("[Core/Config] Успешно загружена иконка сервера!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        if (config == null) {
            config = new JConfig();
        }

        try (FileWriter writer = new FileWriter("configuration.json")) {
            gson.toJson(config, writer);
            System.out.println("[Core/Config] Конфиг создан!");
        } catch (IOException e) {
            throw new RuntimeException("[Core/Config] Ошибка при сохранении конфигурации", e);
        }
    }

    public static String getServerIcon() {
        return serverIcon;
    }

    public static JConfig get() {
        return config;
    }
}
