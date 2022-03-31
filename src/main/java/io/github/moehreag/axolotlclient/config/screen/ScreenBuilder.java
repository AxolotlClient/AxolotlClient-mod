package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.screen.widgets.OptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

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
            this.options.add(new OptionWidget(option, this.width/2 - (right?200:-50), lines, this.height, widget -> this.dialog=widget.getDialog()));
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
        this.dialog=null;
        //System.out.println("Clicked at X:"+mouseX+" Y:"+mouseY);
        options.forEach(optionWidget -> {
            if(optionWidget.isHovered(mouseX, mouseY)) OptionWidget.action.onClick(optionWidget);
        });
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
        //options.forEach(OptionWidget::clicked);
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
}
