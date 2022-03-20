package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class NametagConfScreen extends ConfScreen {
    protected Screen parent;

    public NametagConfScreen(Screen parent){
        super("nametagConf.title");
        this.parent = parent;
    }

    @Override
    public void init() {
        this.buttons.add(new BooleanButtonWidget(1, this.width / 2 - 155, this.height / 6 + 72 - 6, "showOwnNametag", Axolotlclient.CONFIG.NametagConf.showOwnNametag));
        this.buttons.add(new BooleanButtonWidget(2, this.width / 2 + 5, this.height / 6 + 72 - 6, "forceShadows", Axolotlclient.CONFIG.NametagConf.useShadows));
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);

        if(button.id>0){
            if(button.id==1)Axolotlclient.CONFIG.NametagConf.showOwnNametag=!Axolotlclient.CONFIG.NametagConf.showOwnNametag;
            if(button.id==2)Axolotlclient.CONFIG.NametagConf.useShadows=!Axolotlclient.CONFIG.NametagConf.useShadows;
            MinecraftClient.getInstance().openScreen(this);
        }
    }
}
