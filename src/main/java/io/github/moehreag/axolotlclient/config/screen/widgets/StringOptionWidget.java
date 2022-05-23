package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

public class StringOptionWidget extends ButtonWidget {

    public TextFieldWidget textField;

    private final StringOption option;

    public StringOptionWidget(int id, int x, int y, StringOption option){
        super(id, x, y, 150, 40, option.get());
        textField = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x, y+10, 150, 20);
        this.option=option;
        textField.setText(option.get());
        textField.setVisible(true);
        textField.setEditable(true);
        textField.setMaxLength(512);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        MinecraftClient.getInstance().textRenderer.draw(I18n.translate(option.getName()) + ":", x, y, -1);
        textField.render();
    }

}
