package io.github.moehreag.axolotlclient.config.screen;

import com.google.common.collect.Lists;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import io.github.moehreag.axolotlclient.config.widgets.TextFieldWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.io.IOException;
import java.nio.file.Files;

import static io.github.moehreag.axolotlclient.Axolotlclient.CONFIG;

public class GeneralConfScreen extends ConfScreen {

    private String tooltip;
    private TextFieldWidget zoomDivisor;

    public GeneralConfScreen(Screen parent){
        super("general.title", parent);
    }

    @Override
    public void init() {
        super.init();

        this.buttons.add(new BooleanButtonWidget(1, this.width / 2 - 155, this.height / 6 + 72 -6, "customSky", CONFIG.customSky));
        this.buttons.add(new BooleanButtonWidget(2, this.width / 2 - 155, this.height / 6 + 96 -6, "showSunMoon", CONFIG.showSunMoon));

        this.buttons.add(new BooleanButtonWidget(4, this.width / 2 + 5, this.height / 6 + 96 -6, "decreaseSensitivity", CONFIG.decreaseSensitivity));

        zoomDivisor = new TextFieldWidget(3, this.width / 2 + 115, this.height / 6 + 72 -6, 40);


        this.buttons.add(new ButtonWidget(99, this.width / 2 + 5, this.height / 6 + 120 + 16, 150, 20, I18n.translate("resetConf")){
            @Override
            public void render(MinecraftClient client, int mouseX, int mouseY) {
                super.render(client, mouseX, mouseY);
                if(isHovered())setTooltip(I18n.translate("resetConfTooltip"));
                else setTooltip(null);
            }
        });

        zoomDivisor.write(String.valueOf(Axolotlclient.CONFIG.zoomDivisor.get()));
        zoomDivisor.setEditable(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        if (this.tooltip != null) {
            this.renderTooltip(Lists.newArrayList(this.tooltip), mouseX, mouseY);
        }

        drawWithShadow(MinecraftClient.getInstance().textRenderer, I18n.translate("zoomDivisorDesc"), this.width/2 + 5, this.height/6 +72, 10526880);
        zoomDivisor.render();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id == 0){
            CONFIG.zoomDivisor.set(Float.parseFloat(zoomDivisor.getText()));
        }

        if(button.id>0){
            if(button.id==1) CONFIG.customSky.toggle();
            if(button.id==2) CONFIG.showSunMoon.toggle();
            if(button.id==4) CONFIG.decreaseSensitivity.toggle();
            if(button.id==99) {
                try {
                    Files.deleteIfExists(FabricLoader.getInstance().getConfigDir().resolve("Axolotlclient.json"));
                } catch (IOException ignored) {}
                ConfigManager.load();
            }

            MinecraftClient.getInstance().openScreen(this);
        }
    }


    public void setTooltip(String tooltip){
        this.tooltip=tooltip;
    }

    @Override
    public void tick(){
        zoomDivisor.tick();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        zoomDivisor.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyPressed(char character, int code) {
        zoomDivisor.keyPressed(character, code);
        super.keyPressed(character, code);
    }
}
