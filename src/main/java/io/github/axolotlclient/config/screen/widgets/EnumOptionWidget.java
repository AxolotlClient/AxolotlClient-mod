package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EnumOptionWidget extends ButtonWidget {

    EnumOption option;

    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, Text.translatable(option.get()), buttonWidget -> buttonWidget.setMessage(Text.translatable(option.next())));
        this.option=option;
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button==1) {
            setMessage(Text.translatable(option.last()));
        } else {
            setMessage(Text.translatable(option.next()));
        }
        return true;
    }
}
