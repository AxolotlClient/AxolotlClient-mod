package io.github.moehreag.axolotlclient.config.screen;

import static io.github.moehreag.axolotlclient.Axolotlclient.CONFIG;

import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import io.github.moehreag.axolotlclient.config.widgets.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

public class BadgeConfScreen extends ConfScreen {

    private TextFieldWidget badgeField;

    protected Screen parent;

    public BadgeConfScreen(Screen parent) {
        super("badges.title");
        this.parent = parent;
    }

    @Override
    public void init(){
        super.init();
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(new BooleanButtonWidget(1, this.width / 2 - 155, this.height / 6 + 72 - 6, "showBadge" , CONFIG.badgeOptions.showBadge));
        this.buttons.add(new BooleanButtonWidget(2, this.width / 2 + 5, this.height / 6 + 72 - 6, "customBadge", CONFIG.badgeOptions.CustomBadge));
        badgeField = new TextFieldWidget(3, this.width / 2 + 5, this.height / 6 + 96 + 10);

    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        drawWithShadow(this.textRenderer, I18n.translate("customBadgeDesc"), this.width / 2 + 5, this.height/6 + 96 - 4, 10526880);
        badgeField.render();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id == 0){
            CONFIG.badgeOptions.badgeText = badgeField.getText();
        } else {
            switch (button.id) {
                case 1:
                    CONFIG.badgeOptions.showBadge = !CONFIG.badgeOptions.showBadge;
                    break;
                case 2:
                    CONFIG.badgeOptions.CustomBadge = !CONFIG.badgeOptions.CustomBadge;
                    break;
            }
            MinecraftClient.getInstance().openScreen(this);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        badgeField.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyPressed(char character, int code){
        super.keyPressed(character, code);
        badgeField.keyPressed(character, code);
    }

    @Override
    public void tick() {
        badgeField.tick();
    }
}
