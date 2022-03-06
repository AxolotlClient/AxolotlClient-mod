package io.github.moehreag.axolotlclient.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class AxolotlclientConfigScreen extends Screen {

    protected final Screen parent;

    public AxolotlclientConfigScreen(Screen parent){
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();

        this.buttons.add(new ButtonWidget(1, this.width/4 - 100, this.height /4 + 20, I18n.translate("general")));
        this.buttons.add(new ButtonWidget(1, this.width/4 - 100, this.height /4 + 20, I18n.translate("badges")));
        this.buttons.add(new ButtonWidget(1, this.width/4 - 100, this.height /4 + 20, I18n.translate("general")));


        this.buttons.add(new ButtonWidget(0, this.width/2 -100, this.height - 50, I18n.translate("back")));
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id == 0){
            ConfigHandler.save();
            this.client.openScreen(parent);
        }
    }
}
