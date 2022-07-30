package io.github.axolotlclient.config;

import com.google.gson.*;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.OptionCategory;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ConfigManager{
    private static final List<OptionCategory> categories = AxolotlClient.CONFIG.config;
    private static final Path confPath = FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json");

    public static void save(){
        try{
            saveFile();
        } catch (IOException e) {
            AxolotlClient.LOGGER.error("Failed to save config!");
        }
    }

    private static void saveFile() throws IOException {

        JsonObject config = new JsonObject();
        for(OptionCategory category : categories) {
            JsonObject object = new JsonObject();

            config.add(category.getName(), getConfig(object, category));

        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Files.write(confPath, Collections.singleton(gson.toJson(config)));
    }

    private static JsonObject getConfig(JsonObject object, OptionCategory category){
        for (Option option : category.getOptions()) {

            object.add(option.getName(), option.getJson());
        }

        if(!category.getSubCategories().isEmpty()){
            for(OptionCategory sub:category.getSubCategories()){
                JsonObject subOption = new JsonObject();
                object.add(sub.getName(), getConfig(subOption, sub));
            }
        }
        return object;
    }

    public static void load() {

        try {
            JsonObject config = new JsonParser().parse(new FileReader(confPath.toString())).getAsJsonObject();

            for(OptionCategory category:categories) {
                if(config.has(category.getName())) {
                    setOptions(config.get(category.getName()).getAsJsonObject(), category);
                }
            }
        } catch (Exception e){
            AxolotlClient.LOGGER.error("Failed to load config! Using default values... \nError: ");
            e.printStackTrace();
            loadDefaults();
        }
        //save();
    }

    private static void setOptions(JsonObject config, OptionCategory category){
        for (Option option : category.getOptions()) {
            if (config.has(option.getName())) {
                JsonElement part = config.get(option.getName());
                option.setValueFromJsonElement(part);
            }
        }
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                JsonObject subCat = config.get(sub.getName()).getAsJsonObject();
                setOptions(subCat, sub);
            }
        }
    }

    private static void loadDefaults(){
        AxolotlClient.CONFIG.config.forEach(OptionCategory -> {
            OptionCategory.getOptions().forEach(Option::setDefaults);
            if(!OptionCategory.getSubCategories().isEmpty()){
                for(OptionCategory category : OptionCategory.getSubCategories()){
                    category.getOptions().forEach(Option::setDefaults);
                }
            }
        });
    }


}
