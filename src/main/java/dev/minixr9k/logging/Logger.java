package dev.minixr9k.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String formatted = String.format("[%s %s] %s", timestamp, level, message);

        if (level.equalsIgnoreCase("ERROR"))
            System.err.println(formatted);
        else
            System.out.println(formatted);
    }

}
