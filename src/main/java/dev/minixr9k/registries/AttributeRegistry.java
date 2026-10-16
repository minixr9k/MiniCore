package dev.minixr9k.registries;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AttributeRegistry {

    public static int getAttribute(String attribute, int protocolVersion) {
        String fileName = "attributes1.21.8.json";

        try (InputStream is = AttributeRegistry.class.getResourceAsStream("/attributes/" + fileName)) {
            if (is == null) return 0;

            try (JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return findIdInStream(reader, attribute);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int findIdInStream(JsonReader reader, String attribute) throws Exception {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("minecraft:attribute")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String subName = reader.nextName();
                    if (subName.equals("entries")) {

                        reader.beginObject();
                        while (reader.hasNext()) {
                            String entityName = reader.nextName();

                            if (entityName.equalsIgnoreCase(attribute)) {
                                return readProtocolId(reader);
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

        return -1;
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