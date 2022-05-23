package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionPairWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.awt.*;

public class OptionsScreenBuilder extends Screen {

    private final Screen parent;
    protected OptionCategory cat;

    private ButtonWidgetList list;

    public OptionsScreenBuilder(Screen parent, OptionCategory category){
        this.parent=parent;
        this.cat=category;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else renderDirtBackground(0);

        drawCenteredString(textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        this.list.render(mouseX, mouseY, tickDelta);

        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id==0){
            MinecraftClient.getInstance().openScreen(parent);
        }
    }

    @Override
    public void init() {
        this.list = new ButtonWidgetList(this.client, this.width, this.height, 50, this.height - 50, 45, cat);



        this.buttons.add(new ButtonWidget(0, this.width/2-75, this.height-40, 200, 20, I18n.translate("back")));
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
        this.list.handleMouse();
    }

    @Override
    protected void keyPressed(char character, int code) {
        super.keyPressed(character, code);
        this.list.keyPressed(character, code);
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }
}
