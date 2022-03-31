package io.github.moehreag.axolotlclient.modules.hud.gui.screen;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.hud.HudEditScreen;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.concurrent.atomic.AtomicInteger;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private final  HudManager manager;


    public ConfigScreen(Screen parent, HudManager manager){
        this.parent=parent;
        this.manager = manager;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void keyPressed(char character, int code) {
        super.keyPressed(character, code);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
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
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id==0)MinecraftClient.getInstance().openScreen(parent!=null?parent: new HudEditScreen());
    }

    @Override
    public void setScreenBounds(int width, int height) {
        super.setScreenBounds(width -20, height -40);
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new ButtonWidget(0,
                width / 2 - 50,
                height - 50 ,
                100, 20,
                I18n.translate("back")
        ));

        AtomicInteger id= new AtomicInteger(1);
        manager.getEntries().forEach((AbstractHudEntry entry) -> {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(entry.getNameKey(), 0, 0, -1);
            this.buttons.add(new ButtonWidget(id.getAndIncrement(), entry.getX(), entry.getY(), 60, 20, entry.getName()));
            Axolotlclient.LOGGER.info(entry.getNameKey());
        });
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void removed() {
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }
}
