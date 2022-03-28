package io.github.moehreag.axolotlclient.modules.hud.gui.screen;

import io.github.moehreag.axolotlclient.modules.hud.HudEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

// Based around https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/GuiShapeManager.java
// Licensed under GNU LGPL
public class ConfigScreen extends Screen {//extends GuiListBase<AbstractHudEntry, HudEntryWidget, HudListWidget>
        //implements ISelectionListener<AbstractHudEntry> {

    private final Screen parent;


    public ConfigScreen(Screen parent){
        this.parent=parent;
    }

    /*public ConfigScreen(Screen parent) {
        super(10, 20);
        setParent(parent);
        useTitleHierarchy = false;
        title = StringUtils.translate("button.kronhud.configuration");
    }

    @Override
    protected HudListWidget createListWidget(int listX, int listY) {
        return new HudListWidget(listX, listY, getBrowserWidth(), getBrowserHeight(), this, this);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == KeyCodes.KEY_ESCAPE) {
            GuiBase.openGui(getParent());
            return true;
        }

        return false;
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 37;
    }

    @Override
    public void onSelectionChange(AbstractHudEntry hud) {
    }*/

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
        if(button.id==0)MinecraftClient.getInstance().openScreen(new HudEditScreen());
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
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void removed() {
    }
}
