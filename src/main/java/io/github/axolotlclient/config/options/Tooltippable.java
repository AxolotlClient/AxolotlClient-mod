package io.github.axolotlclient.config.options;

import io.github.axolotlclient.util.Util;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

    String getName();

    default String getTooltip(){
        return this.getTooltip(null);
    }

    default @Nullable String getTooltip(String location){
        if(location!=null){
            if(!Objects.equals(I18n.translate(location + ".tooltip"), location + ".tooltip")) {
                return I18n.translate(location + ".tooltip");
            }
        }
        return Util.getTranslationIfExistsOrNull(getName()+".tooltip");
    }
}
