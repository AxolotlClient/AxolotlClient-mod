package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class StringOptionWidget extends ButtonWidget {

    public TextFieldWidget textField;

    public final StringOption option;

    public StringOptionWidget(int id, int x, int y, StringOption option){
        super(id, x, y, 150, 40, option.get());
        textField = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x, y, 150, 20){
            @Override
            public void mouseClicked(int mouseX, int mouseY, int button) {
                if(isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                    super.mouseClicked(mouseX, mouseY, button);
                } else {
                    this.setFocused(false);
                }
            }
        };
        this.option=option;
        textField.setText(option.get());
        textField.setVisible(true);
        textField.setEditable(true);
        textField.setMaxLength(512);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        GlStateManager.disableDepthTest();
        //MinecraftClient.getInstance().textRenderer.draw(I18n.translate(option.getName()), x, y, -1);
        textField.y = y;
        textField.x = x;
        textField.render();
        GlStateManager.enableDepthTest();
    }


    public void keyPressed(char c, int code){
        this.textField.keyPressed(c, code);
        this.option.set(textField.getText());
    }



}
