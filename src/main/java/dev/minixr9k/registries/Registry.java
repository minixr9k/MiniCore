package dev.minixr9k.registries;

import com.google.gson.stream.JsonReader;
import dev.minixr9k.types.registry.RegistryType;
import dev.minixr9k.utils.VersionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Registry {

    // Registry.getId("minecraft:diamond_sword", RegistryType.ITEMS, player.getVersion()) -> int id
    public static int getId(String findValue, RegistryType registryType, int protocolVersion) {
        String detectedVersion = VersionUtils.getVersion(protocolVersion);

        try (InputStream is = Registry.class.getResourceAsStream(
                String.format("/registries/%s/registries.json", detectedVersion)
        )) {
            if (is == null) return 0;

            try (JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return findIdInStream(reader, findValue, registryType.getIdentifier());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int findIdInStream(JsonReader reader, String findValue, String registryName) throws Exception {
        int fallback = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(registryName)) {
                reader.beginObject();

                String defaultObj = null;

                while (reader.hasNext()) {
                    String subName = reader.nextName();

                    if (subName.equals("default")) {
                        defaultObj = reader.nextString();
                    } else if (subName.equals("entries")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String objectName = reader.nextName();

                            if (objectName.equalsIgnoreCase(findValue)) {
                                return readProtocolId(reader);
                            }
                            else if (defaultObj != null && objectName.equalsIgnoreCase(defaultObj)) {
                                fallback = readProtocolId(reader);
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return fallback;
    }

    private static int readProtocolId(JsonReader reader) throws IOException {
        int id = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            String prop = reader.nextName();
            if (prop.equals("protocol_id")) {
                id = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return id;
    }

}