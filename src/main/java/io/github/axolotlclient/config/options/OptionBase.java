package io.github.axolotlclient.config.options;

import org.jetbrains.annotations.Nullable;

public abstract class OptionBase implements Option {

    public String name;
    public String tooltipKeyPrefix;

    public OptionBase(String name){
        this.name=name;
    }

    public OptionBase(String name, String tooltipKeyPrefix){
        this.name = name;
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    @Override
    public @Nullable String getTooltip() {
        return Option.super.getTooltip(tooltipKeyPrefix);
    }

    public String getName(){
        return name;
    }
}
