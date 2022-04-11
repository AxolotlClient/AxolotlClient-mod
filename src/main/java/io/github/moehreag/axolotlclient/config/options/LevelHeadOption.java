package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class LevelHeadOption extends OptionBase implements EnumOption{

    LevelHeadMode value;

    public LevelHeadOption(String name) {
        super(name);
        this.value=LevelHeadMode.NETWORK;
    }

    @Override
    public OptionType getType() {
        return OptionType.ENUM;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Enum<?> get() {
        return value;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        switch(element.getAsString()){
            case "NETWORK": value=LevelHeadMode.NETWORK; break;
            case "BEDWARS": value=LevelHeadMode.BEDWARS; break;
            case "SKYWARS": value=LevelHeadMode.SKYWARS; break;
        }
    }

    public LevelHeadMode next(){
        switch (value){
            case BEDWARS:value=LevelHeadMode.SKYWARS; break;
            case SKYWARS:value=LevelHeadMode.NETWORK; break;
            case NETWORK:value=LevelHeadMode.BEDWARS; break;
        }
        return value;
    }

    @Override
    public void setDefaults() {
        this.value=LevelHeadMode.NETWORK;
    }
    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(value.toString());
    }


    public enum LevelHeadMode{
        NETWORK,
        BEDWARS,
        SKYWARS;
    }
}