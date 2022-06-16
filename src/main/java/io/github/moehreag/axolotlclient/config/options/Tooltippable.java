package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

    String getName();


    default @Nullable String getTooltip(){
        if(!Objects.equals(I18n.translate(getName() + ".tooltip"), getName() + ".tooltip")) {
            return I18n.translate(getName() + ".tooltip");
        }
        return null;
    }
}
