package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;

public class ToggleSprintHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("togglesprint");
    private final BooleanOption toggleSprint = new BooleanOption("toggleSprint", false);
    private final BooleanOption toggleSneak = new BooleanOption("toggleSneak", false);
    private final StringOption placeholder = new StringOption("placeholder", "");

    KeyBinding sprintToggle = new KeyBinding("key.toggleSprint", 23, "category.axolotlclient");
    KeyBinding sneakToggle = new KeyBinding("key.toggleSneak", 37, "category.axolotlclient");

    public BooleanOption sprintToggled = new BooleanOption("sprintToggled", false);
    private boolean sprintWasPressed = false;
    public BooleanOption sneakToggled = new BooleanOption("sneakToggled", false);
    private boolean sneakWasPressed = false;

    public ToggleSprintHud(){
        super(100, 20);
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(sprintToggle);
        KeyBindingHelper.registerKeyBinding(sneakToggle);
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();
        if(chroma.get())GlStateManager.color4f(textColor.getChroma().getRed(), textColor.getChroma().getGreen(), textColor.getChroma().getBlue(), 1F);
        if (background.get()) {
            fillRect(getBounds(), backgroundColor.get());
        }
        drawCenteredString(client.textRenderer, getText(), new DrawPosition(pos.x + (Math.round(width) / 2),
                pos.y + (Math.round((float) height / 2)) - 4), chroma.get()? textColor.getChroma() : textColor.get(), shadow.get());
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        drawCenteredString(client.textRenderer, I18n.translate("sprinting_toggled"),
                new DrawPosition(pos.x + (width / 2),
                        pos.y + (height / 2) - 4), textColor.get(), shadow.get());
        GlStateManager.popMatrix();
        hovered = false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public String getText(){

        if(client.options.keySneak.isPressed())return I18n.translate("sneaking_pressed");
        if(client.options.keySprint.isPressed())return I18n.translate("sprinting_pressed");

        if(toggleSneak.get() && sneakToggled.get()){
            return I18n.translate("sneaking_toggled");
        }
        if(toggleSprint.get() && sprintToggled.get()){
             return I18n.translate("sprinting_toggled");
        }

        return placeholder.get();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if(sprintToggle.isPressed() != sprintWasPressed && sprintToggle.isPressed()){
            sprintToggled.toggle();
            ConfigManager.save();
            sprintWasPressed=sprintToggle.isPressed();
        } else  if(!sprintToggle.isPressed())sprintWasPressed=false;
        if(sneakToggle.isPressed() != sneakWasPressed && sneakToggle.isPressed()){
            sneakToggled.toggle();
            ConfigManager.save();
            sneakWasPressed=sneakToggle.isPressed();
        } else if(!sneakToggle.isPressed())sneakWasPressed = false;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(chroma);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(placeholder);

        Axolotlclient.config.add(sprintToggled);
        Axolotlclient.config.add(sneakToggled);
    }
}
