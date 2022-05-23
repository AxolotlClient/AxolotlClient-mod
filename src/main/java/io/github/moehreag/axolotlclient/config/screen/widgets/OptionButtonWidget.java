package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class OptionButtonWidget extends ButtonWidget {

    private final Option option;

    public OptionButtonWidget(int id, int x, int y, Option option, String message) {
        super(id, x, y, 150, 20, I18n.translate(message)+": "+ I18n.translate ("options."+(((BooleanOption) option).get()?"on":"off")));
        this.option = option;
    }

    public Option getOption() {
        return this.option;
    }
}
