package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.screen.widgets.CategoryWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.CustomButtonWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.CustomWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

public class CategoryScreenBuilder extends Screen {

    private final Screen parent;
    private CustomWidget back;
    private final List<OptionCategory> categories = AxolotlClient.CONFIG.getCategories();

    public CategoryScreenBuilder(Screen parent){
        this.parent=parent;
    }

    int lines = 1;
    boolean right;

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else renderDirtBackground(0);

        drawCenteredString(this.textRenderer, I18n.translate("config"), width/2, height/4, -1);
        buttons.forEach(buttonWidget -> buttonWidget.render(MinecraftClient.getInstance(), mouseX, mouseY));
        back.render(mouseX, mouseY);

    }

    @Override
    public void init() {
        super.init();
        right=false;
        lines=1;
        for(OptionCategory category:categories){
            this.buttons.add(new CategoryWidget(category, this.width/2 - (right?-50:200), lines, 150, this.height));
            if(right)lines++;
            right=!right;
        }

        this.buttons.add(new CustomButtonWidget(99,this.width-106, this.height-26, 100, 20, "credits", new Identifier("axolotlclient", "textures/gui/button2.png")));
        back = new CustomWidget(this.width/2-75, (this.height/6) *5, 150, 20,"back" , widget -> client.openScreen(parent), new Identifier("axolotlclient", "textures/gui/button1.png"));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if(button==0) {
            this.buttons.forEach(buttonWidget -> {

                if(buttonWidget.isMouseOver(client, mouseX, mouseY) && buttonWidget instanceof CategoryWidget){
                    buttonWidget.playDownSound(client.getSoundManager());
                    this.client.openScreen(new OptionScreenBuilder(this, ((CategoryWidget) buttonWidget).category));
                } else if(buttonWidget.isMouseOver(client, mouseX, mouseY) && buttonWidget instanceof CustomButtonWidget){
                    buttonWidget.playDownSound(client.getSoundManager());
                    if(buttonWidget.id==99)client.openScreen(new CreditsScreen(new CategoryScreenBuilder(parent)));
                }
            });
            if(back.isHovered(mouseX, mouseY)){CustomWidget.onClick.onClick(back);back.playDownSound();}
        }
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }
}
