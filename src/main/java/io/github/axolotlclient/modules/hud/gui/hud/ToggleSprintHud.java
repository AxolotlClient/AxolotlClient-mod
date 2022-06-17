package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.StringOption;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.apache.commons.io.Charsets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ToggleSprintHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("togglesprint");
    private final BooleanOption toggleSprint = new BooleanOption("toggleSprint", false);
    private final BooleanOption toggleSneak = new BooleanOption("toggleSneak", false);
    private final BooleanOption randomPlaceholder = new BooleanOption("randomPlaceholder", false);
    private final StringOption placeholder = new StringOption("placeholder", "");

    KeyBinding sprintToggle = new KeyBinding("key.toggleSprint", 23, "category.axolotlclient");
    KeyBinding sneakToggle = new KeyBinding("key.toggleSneak", 37, "category.axolotlclient");

    public BooleanOption sprintToggled = new BooleanOption("sprintToggled", false);
    private boolean sprintWasPressed = false;
    public BooleanOption sneakToggled = new BooleanOption("sneakToggled", false);
    private boolean sneakWasPressed = false;

    private final List<String> texts = new ArrayList<>();
    private String text = "";

    public ToggleSprintHud(){
        super(100, 20);
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(sprintToggle);
        KeyBindingHelper.registerKeyBinding(sneakToggle);
    }

    private void loadRandomPlaceholder(){
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("axolotlclient", "texts/splashes.txt")).getInputStream(), Charsets.UTF_8)
            );
            String string;
            while((string = bufferedReader.readLine()) != null) {
                string = string.trim();
                if (!string.isEmpty()) {
                    texts.add(string);
                }
            }

            text = texts.get(new Random().nextInt(texts.size()));
        } catch (Exception e){
            text = "";
        }
    }

    private String getRandomPlaceholder(){
        if(Objects.equals(text, "")){
            loadRandomPlaceholder();
        }
        return text;
    }

    @Override
    public String getPlaceholder() {
        return randomPlaceholder.get() ? getRandomPlaceholder() : placeholder.get();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public String getValue(){

        if(client.options.keySneak.isPressed())return I18n.translate("sneaking_pressed");
        if(client.options.keySprint.isPressed())return I18n.translate("sprinting_pressed");

        if(toggleSneak.get() && sneakToggled.get()){
            return I18n.translate("sneaking_toggled");
        }
        if(toggleSprint.get() && sprintToggled.get()){
             return I18n.translate("sprinting_toggled");
        }
        return getPlaceholder();
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
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(randomPlaceholder);
        options.add(placeholder);

        AxolotlClient.config.add(sprintToggled);
        AxolotlClient.config.add(sneakToggled);
    }


}
