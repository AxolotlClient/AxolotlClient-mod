package io.github.moehreag.axolotlclient.config.screen;

import com.google.common.collect.Lists;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.ConfigHandler;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;
import java.nio.file.Files;

public class GeneralConfScreen extends ConfScreen {

    private String tooltip;

    public GeneralConfScreen(){
        super("general.title");
    }

    @Override
    public void init() {
        super.init();

        this.buttons.add(new BooleanButtonWidget(1, this.width / 2 - 155, this.height / 6 + 72 -6, "customSky", Axolotlclient.CONFIG.General.customSky));

        this.buttons.add(new ButtonWidget(99, this.width / 2 + 5, this.height / 6 + 120 + 16, 150, 20, I18n.translate("resetConf")){
            @Override
            public void render(MinecraftClient client, int mouseX, int mouseY) {
                super.render(client, mouseX, mouseY);
                if(isHovered())setTooltip(I18n.translate("resetConfTooltip"));
                else setTooltip(null);
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        if (this.tooltip != null) {
            this.renderTooltip(Lists.newArrayList(this.tooltip), mouseX, mouseY);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id>0){
            if(button.id==1)Axolotlclient.CONFIG.General.customSky=!Axolotlclient.CONFIG.General.customSky;

            if(button.id==99) {
                try {
                    Files.deleteIfExists(FabricLoader.getInstance().getConfigDir().resolve("Axolotlclient.json"));
                } catch (IOException ignored) {}
                ConfigHandler.init();
            }

            MinecraftClient.getInstance().openScreen(this);
        }
    }


    public void setTooltip(String tooltip){
        this.tooltip=tooltip;
    }

}
