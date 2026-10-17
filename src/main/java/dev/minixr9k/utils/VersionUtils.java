package dev.minixr9k.utils;

import java.util.HashMap;
import java.util.Map;

public class VersionUtils {

    private static final Map<Integer, String> PROTOCOL_VERSION = new HashMap<>();

    static {
        PROTOCOL_VERSION.put(767, "1_21");
        PROTOCOL_VERSION.put(768, "1_21_2");
        PROTOCOL_VERSION.put(769, "1_21_4");
        PROTOCOL_VERSION.put(770, "1_21_5");
        PROTOCOL_VERSION.put(771, "1_21_6");
        PROTOCOL_VERSION.put(772, "1_21_7");
        PROTOCOL_VERSION.put(773, "1_21_9");
        PROTOCOL_VERSION.put(774, "1_21_11");
        PROTOCOL_VERSION.put(775, "26_1");
        PROTOCOL_VERSION.put(776, "26_2");
        PROTOCOL_VERSION.put(777, "26_3");
        PROTOCOL_VERSION.put(778, "26_4");
    }

    public static String getVersion(int protocol) {
        return PROTOCOL_VERSION.getOrDefault(protocol, "Unknown (" + protocol + ")");
    }

}
