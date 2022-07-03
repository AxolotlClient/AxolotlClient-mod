package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EnumOptionWidget extends ButtonWidget {

    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, Text.of(option.get().toString()), buttonWidget -> buttonWidget.setMessage(Text.of(option.next().toString())));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return super.isMouseOver(mouseX, mouseY);
    }
}
