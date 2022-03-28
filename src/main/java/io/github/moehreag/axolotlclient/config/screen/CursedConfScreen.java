package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class CursedConfScreen extends ConfScreen{
    public CursedConfScreen(Screen parent) {
        super("cursedConf.title", parent);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);


    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);

        if(button.id>0){

            if(button.id==1)Axolotlclient.CONFIG.Cursed.rotateWorld=!Axolotlclient.CONFIG.Cursed.rotateWorld;

            MinecraftClient.getInstance().openScreen(this);
        }
    }

    @Override
    public void init() {
        super.init();

        this.buttons.add(new BooleanButtonWidget(1, this.width / 2 - 155, this.height / 6 + 48 - 6, "rotateWorld", Axolotlclient.CONFIG.Cursed.rotateWorld));
    }
}
