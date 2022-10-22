package io.github.axolotlclient.config;

import com.google.gson.*;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.ConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.util.Logger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class AxolotlClientConfigManager implements ConfigManager {
    private static final List<OptionCategory> categories = AxolotlClient.CONFIG.config;
    private static final Path confPath = FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json");

    public void save(){
        try{
            saveFile();
        } catch (IOException e) {
            Logger.error("Failed to save config!");
        }
    }

    private void saveFile() throws IOException {

        JsonObject config = new JsonObject();
        for(OptionCategory category : categories) {
            JsonObject object = new JsonObject();

            config.add(stripTranslationPrefix(category.getName()), this.getConfig(object, category));

        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Files.write(confPath, Collections.singleton(gson.toJson(config)));
    }

    public JsonObject getConfig(JsonObject object, OptionCategory category){
        for (Option option : category.getOptions()) {

            object.add(stripTranslationPrefix(option.getName()), option.getJson());
        }

        if(!category.getSubCategories().isEmpty()){
            for(OptionCategory sub:category.getSubCategories()){
                JsonObject subOption = new JsonObject();
                object.add(stripTranslationPrefix(sub.getName()), getConfig(subOption, sub));
            }
        }
        return object;
    }

    public void load() {

        loadDefaults();

        try {
            JsonObject config = new JsonParser().parse(new FileReader(confPath.toString())).getAsJsonObject();

            for(OptionCategory category:categories) {
                if(config.has(stripTranslationPrefix(category.getName()))) {
                    setOptions(config.get(stripTranslationPrefix(category.getName())).getAsJsonObject(), category);
                }
            }
        } catch (Exception e){
            Logger.error("Failed to load config! Using default values... \nError: ");
            e.printStackTrace();
        }
    }

    public void setOptions(JsonObject config, OptionCategory category){
        for (Option option : category.getOptions()) {
            if (config.has(stripTranslationPrefix(option.getName()))) {
                JsonElement part = config.get(stripTranslationPrefix(option.getName()));
                option.setValueFromJsonElement(part);
            }
        }
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                if (config.has(stripTranslationPrefix(sub.getName()))) {
                    JsonObject subCat = config.get(stripTranslationPrefix(sub.getName())).getAsJsonObject();
                    setOptions(subCat, sub);
                }
            }
        }
    }

    public void loadDefaults(){
        AxolotlClient.CONFIG.config.forEach(this::setOptionDefaults);
    }

    public void setOptionDefaults(OptionCategory category){
        category.getOptions().forEach(Option::setDefaults);
        if(!category.getSubCategories().isEmpty()){
            for (OptionCategory sub: category.getSubCategories()) {
                setOptionDefaults(sub);
            }
        }
    }

    private static String stripTranslationPrefix(String name){
        return name.startsWith("axolotlclient.")? name.substring(14) : name;
    }
}
