package io.github.moehreag.axolotlclient.config.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;
import java.nio.file.Files;

public class GeneralConfScreen extends ConfScreen {

    protected Screen parent;

    private String tooltip;

    public GeneralConfScreen(Screen parent){
        super("general.title");
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();

        this.buttons.add(new ButtonWidget(1, this.width / 2 + 5, this.height / 6 + 120 + 16, I18n.translate("resetConf")){
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

            if(button.id==1) {
                try {
                    Files.deleteIfExists(FabricLoader.getInstance().getConfigDir().resolve("Axolotlclient.json"));
                } catch (IOException ignored) {}
            }

            MinecraftClient.getInstance().openScreen(this);
        }
    }


    public void setTooltip(String tooltip){
        this.tooltip=tooltip;
    }

}
