/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import lombok.Getter;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class ToggleSprintHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "togglesprint");
    private final BooleanOption toggleSprint = new BooleanOption("axolotlclient.toggleSprint", false);
    private final BooleanOption toggleSneak = new BooleanOption("axolotlclient.toggleSneak", false);
    private final BooleanOption randomPlaceholder = new BooleanOption("axolotlclient.randomPlaceholder", false);
    private final StringOption placeholder = new StringOption("axolotlclient.placeholder", "No keys pressed");

    private final KeyBinding sprintToggle = new KeyBinding("key.toggleSprint", Keyboard.KEY_K,
            "axolotlclient.category.axolotlclient");
    private final KeyBinding sneakToggle = new KeyBinding("key.toggleSneak", Keyboard.KEY_I,
            "axolotlclient.category.axolotlclient");

    @Getter
    private final BooleanOption sprintToggled = new BooleanOption("axolotlclient.sprintToggled", false);
    private boolean sprintWasPressed = false;
    @Getter
    private final BooleanOption sneakToggled = new BooleanOption("axolotlclient.sneakToggled", false);
    private boolean sneakWasPressed = false;

    private final List<String> texts = new ArrayList<>();
    private String text = "";

    public ToggleSprintHud() {
        super(100, 20, false);
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(sprintToggle);
        KeyBindingHelper.registerKeyBinding(sneakToggle);
    }

    private void loadRandomPlaceholder() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            MinecraftClient.getInstance().getResourceManager()
                                    .getResource(new Identifier("texts/splashes.txt")).getInputStream(),
                            StandardCharsets.UTF_8));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                string = string.trim();
                if (!string.isEmpty()) {
                    texts.add(string);
                }
            }

            text = texts.get(new Random().nextInt(texts.size()));
        } catch (Exception e) {
            text = "";
        }
    }

    private String getRandomPlaceholder() {
        if (Objects.equals(text, "")) {
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
    public String getValue() {
        if (client.options.keySneak.isPressed()) {
            return I18n.translate("axolotlclient.sneaking_pressed");
        }
        if (client.options.keySprint.isPressed()) {
            return I18n.translate("axolotlclient.sprinting_pressed");
        }

        if (toggleSneak.get() && sneakToggled.get()) {
            return I18n.translate("axolotlclient.sneaking_toggled");
        }
        if (toggleSprint.get() && sprintToggled.get()) {
            return I18n.translate("axolotlclient.sprinting_toggled");
        }
        return getPlaceholder();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (sprintToggle.isPressed() != sprintWasPressed && sprintToggle.isPressed() && toggleSprint.get()) {
            sprintToggled.toggle();
            sprintWasPressed = sprintToggle.isPressed();
        } else if (!sprintToggle.isPressed()) {
            sprintWasPressed = false;
        }
        if (sneakToggle.isPressed() != sneakWasPressed && sneakToggle.isPressed() && toggleSneak.get()) {
            sneakToggled.toggle();
            sneakWasPressed = sneakToggle.isPressed();
        } else if (!sneakToggle.isPressed()) {
            sneakWasPressed = false;
        }
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(randomPlaceholder);
        options.add(placeholder);
        return options;
    }

    @Override
    public List<Option<?>> getSaveOptions() {
        List<Option<?>> options = super.getSaveOptions();
        options.add(sprintToggled);
        options.add(sneakToggled);
        return options;
    }
}