package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.EnumOption;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class EnumOptionWidget extends ButtonWidget {

    private final EnumOption option;
    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, Text.of(option.get().toString()), buttonWidget -> buttonWidget.setMessage(Text.of(option.next().toString())));
        this.option=option;
    }
}
