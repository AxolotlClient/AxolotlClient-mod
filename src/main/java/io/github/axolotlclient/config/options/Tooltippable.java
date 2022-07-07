package io.github.axolotlclient.config.options;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

    String getName();

    default String getTooltip(){
        return this.getTooltip(getName());
    }

    default @Nullable String getTooltip(String location){
        if(!Objects.equals(I18n.translate(location + ".tooltip"), location + ".tooltip")) {
            return I18n.translate(location + ".tooltip");
        }
        return null;
    }
}
