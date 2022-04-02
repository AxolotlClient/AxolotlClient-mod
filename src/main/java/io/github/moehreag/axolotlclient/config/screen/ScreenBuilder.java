package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.screen.widgets.CustomWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.OptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenBuilder extends Screen {

    private final List<OptionWidget> options = new ArrayList<>();
    private ButtonWidget dialog;

    private Screen parent;

    public ScreenBuilder(Screen parent){
        this.parent=parent;
    }

    int lines = 1;
    boolean right;

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        //renderBackground();
        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        options.forEach(optionWidget -> optionWidget.render(MinecraftClient.getInstance(), mouseX, mouseY));
        if(dialog!=null)dialog.render(MinecraftClient.getInstance(), mouseX, mouseY);

    }

    @Override
    protected void keyPressed(char character, int code) {
        super.keyPressed(character, code);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
    }

    @Override
    public void init() {
        super.init();
        for(Option option:Axolotlclient.CONFIG.get()){
            this.dialog=null;

            this.options.add(new OptionWidget(option, this.width/2 - (right?-50:200), lines, this.height, optionWidget -> this.dialog=optionWidget.getDialog()));
            if(right)lines++;
            right=!right;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if(button==0) {
            this.dialog = null;
            //System.out.println("Dialog: "+this.dialog);
            options.forEach(optionWidget -> {
                if (optionWidget.isHovered(mouseX, mouseY)) CustomWidget.onClick.onClick(optionWidget);
            });
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void mouseDragged(int i, int j, int k, long l) {
        super.mouseDragged(i, j, k, l);
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
    }

    @Override
    public void handleKeyboard() {
        super.handleKeyboard();
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        this.options.clear();
        this.dialog=null;
        this.width=width;
        this.height=height;
        init();
    }
}
