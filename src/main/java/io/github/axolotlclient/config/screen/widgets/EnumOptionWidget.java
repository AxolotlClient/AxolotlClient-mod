package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EnumOptionWidget extends ButtonWidget {

    EnumOption option;

    public EnumOptionWidget(int x, int y, EnumOption option) {
        super(x, y, 150, 20, new TranslatableText(option.get()), buttonWidget -> buttonWidget.setMessage(new TranslatableText(option.next())));
        this.option=option;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(canHover()) {
            return super.isMouseOver(mouseX, mouseY);
        }
        return false;
    }

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button==1) {
            setMessage(new TranslatableText(option.last()));
        } else {
            setMessage(new TranslatableText(option.next()));
        }
        return true;
    }
}
