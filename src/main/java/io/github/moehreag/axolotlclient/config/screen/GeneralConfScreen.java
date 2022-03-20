package io.github.moehreag.axolotlclient.config.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class GeneralConfScreen extends ConfScreen {

    protected Screen parent;

    public GeneralConfScreen(Screen parent){
        super("general.title");
        this.parent = parent;
    }

    @Override
    public void init() {
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

            MinecraftClient.getInstance().openScreen(this);
        }
    }

}
