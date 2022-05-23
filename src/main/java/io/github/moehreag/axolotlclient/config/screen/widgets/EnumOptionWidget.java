package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.EnumOption;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class EnumOptionWidget extends ButtonWidget {

    private final EnumOption option;
    public EnumOptionWidget(int id, int x, int y, EnumOption option) {
        super(id, x, y, 150, 20, I18n.translate(option.getName())+ ": "+ option.get().toString());
        this.option=option;
    }

    public void mouseClicked(){
        this.message = I18n.translate(option.getName())+ ": "+ this.option.next().toString();
    }
}
