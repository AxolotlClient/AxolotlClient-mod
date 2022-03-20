package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.config.AxolotlclientConfigScreen;
import io.github.moehreag.axolotlclient.config.ConfigHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class ConfScreen extends Screen {

    private final String title;

    public ConfScreen(String title){
        this.title = title;
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
            ConfigHandler.save();
            this.client.openScreen(new AxolotlclientConfigScreen());
        }
        super.buttonClicked(button);
    }

    @Override
    public void init() {

        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("back")));
        super.init();
    }
}
