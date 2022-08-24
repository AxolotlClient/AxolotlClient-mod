package io.github.axolotlclient.config.options;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

    String getName();

    default String getTooltipLocation(){
        return getName();
    }

    default String getTooltip(){
        return this.getTooltip(getTooltipLocation());
    }

    default @Nullable String getTooltip(String location){
        String translation = I18n.translate(location + ".tooltip");
        if(!Objects.equals(translation, location + ".tooltip")) {
            return translation;
        }
        return null;
    }
}
