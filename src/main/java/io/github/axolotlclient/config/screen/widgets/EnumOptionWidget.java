package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.EnumOption;
import net.minecraft.client.gui.widget.ButtonWidget;

public class EnumOptionWidget extends ButtonWidget {

    private final EnumOption option;
    public EnumOptionWidget(int id, int x, int y, EnumOption option) {
        super(id, x, y, 150, 20, option.get().toString());
        this.option=option;
    }

    public void mouseClicked(){
        this.message = this.option.next().toString();
    }
}
