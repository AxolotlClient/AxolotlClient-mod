package io.github.axolotlclient.util.translation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TranslationProvider {

    private static final JsonParser parser = new JsonParser();

    private static final Map<String, String> TRANSLATIONS = new HashMap<>();

    public static void clear() {
        TRANSLATIONS.clear();
    }

    public static void accept(InputStream json) {
        JsonObject obj = parser.parse(new InputStreamReader(json)).getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            TRANSLATIONS.put(key, value);
        }
    }

    public static String translate(String key) {
        return TRANSLATIONS.getOrDefault(key, "axolotlclient." + key);
    }

    public static boolean hasTranslation(String key) {
        return TRANSLATIONS.containsKey(key);
    }

    public static Identifier getLanguageId(String id) {
        return new Identifier("axolotlclient","lang/" + id + ".json");
    }

    public static void load() {
        clear();

        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            ResourceManager resourceManager = mc.getResourceManager();

            List<Resource> resources = new ArrayList<>();

            resources.addAll(resourceManager.getAllResources(new Identifier("axolotlclient", "lang/en_us.json")));
            resources.addAll(resourceManager.getAllResources(new Identifier("axolotlclient", "lang/" + mc.options.language.toLowerCase(Locale.ROOT) + ".json")));

            for(Resource resource : resources) {
                try(InputStream in = resource.getInputStream()) {
                    accept(in);
                }
            }
        }
        catch(IOException ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * A version of {@link String#format(String, Object...)} that doesn't allocate an object if there are no arguments passed.
     * @param fmt The format.
     * @param args The args.
     * @return The formatted string.
     */
    public static String format(String fmt, Object... args) {
        return args.length == 0 ? fmt : String.format(fmt, args);
    }
}
