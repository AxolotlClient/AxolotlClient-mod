package io.github.moehreag.axolotlclient.config;

import com.google.gson.*;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.Option;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ConfigManager{
    private static final List<Option> options = Axolotlclient.CONFIG.get();
    private static final Path confPath = FabricLoader.getInstance().getConfigDir().resolve("Axolotlclient.json");

    public static void save(){
        try{
            saveFile();
        } catch (IOException e) {
            Axolotlclient.LOGGER.error("Failed to save config!");
        }
    }

    private static void saveFile() throws IOException {
        JsonObject object = new JsonObject();
        for(Option option:options){

            object.add(option.getName(), option.getJson());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Files.write(confPath, Collections.singleton(gson.toJson(object)));
    }

    public static void load() {
        loadDefaults();
        try {
            JsonObject config = new JsonParser().parse(new FileReader(confPath.toString())).getAsJsonObject();

            for (Option option : options) {
                if (config.has(option.getName())) {
                    JsonElement part = config.get(option.getName());
                    option.setValueFromJsonElement(part);
                }
            }
        } catch (Exception e){Axolotlclient.LOGGER.error("Failed to load config! Using default values...");}
        save();
    }

    private static void loadDefaults(){
        Axolotlclient.CONFIG.get().forEach(Option::setDefaults);
    }


}
