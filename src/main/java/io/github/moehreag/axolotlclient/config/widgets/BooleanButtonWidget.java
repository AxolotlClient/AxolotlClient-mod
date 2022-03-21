package io.github.moehreag.axolotlclient.config.widgets;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class BooleanButtonWidget extends ButtonWidget {
    public BooleanButtonWidget(int id, int x, int y, String message, Boolean option) {
        this(id, x, y, 150, 20, message, option);
    }

    public BooleanButtonWidget(int id, int x, int y, int width, int height, String key, boolean option) {
        super(id, x, y, width, height, I18n.translate(key) + ": "+ I18n.translate("options."+(option?"on":"off")));
    }
}
