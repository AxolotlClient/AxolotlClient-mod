package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class EnumOptionWidget extends ButtonWidget {

    private final EnumOption option;
    public EnumOptionWidget(int id, int x, int y, EnumOption option) {
        super(id, x, y, 150, 20, Util.getTranslationIfExists(option.get().toString()));
        this.option=option;
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return super.isMouseOver(client, mouseX, mouseY);
    }

    public void mouseClicked(){
        this.message = option.next().toString();
    }
}
