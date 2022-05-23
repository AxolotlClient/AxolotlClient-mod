package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.ColorOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class ColorOptionWidget extends ButtonWidget {

    private final ColorOption option;

    private TextFieldWidget textField;

    public ColorOptionWidget(int id, int x, int y, ColorOption option) {
        super(id, x, y, 150, 20, "");
        this.option=option;
        textField = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x+75, y, 73, 20);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        return super.isMouseOver(client, mouseX, mouseY);
    }
}
