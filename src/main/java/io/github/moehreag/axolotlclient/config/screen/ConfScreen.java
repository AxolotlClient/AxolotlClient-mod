package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.config.ConfigManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class ConfScreen extends Screen {

    private final String title;
    private final Screen parent;

    public ConfScreen(String title, Screen parent){
        this.title = title;
        this.parent=parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        this.renderBackground();

        this.drawCenteredString(this.textRenderer, I18n.translate(title), this.width / 2, 17, 16777215);
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id == 0){
            ConfigManager.save();
            this.client.openScreen(parent);
        }
        super.buttonClicked(button);
    }

    @Override
    public void init() {

        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("back")));
        super.init();
    }
}
