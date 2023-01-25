/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.scrollableTooltips;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ScrollableTooltips extends AbstractModule {

    public int tooltipOffsetX;
    public int tooltipOffsetY;

    protected KeyBinding key = new KeyBinding("key.scrollHorizontally", Keyboard.KEY_LSHIFT,
            "category.axolotlclient");

    private static final ScrollableTooltips instance = new ScrollableTooltips();

    private final OptionCategory category = new OptionCategory("scrollableTooltips");

    public final BooleanOption enabled = new BooleanOption("enabled", false);
    public final BooleanOption enableShiftHorizontalScroll = new BooleanOption("shiftHorizontalScroll",
            true);
    protected final IntegerOption scrollAmount = new IntegerOption("scrollAmount", 5, 1, 20);
    protected final BooleanOption inverse = new BooleanOption("inverse", false);

    public static ScrollableTooltips getInstance() {
        return instance;
    }

    @Override
    public void init() {
        category.add(enabled);
        category.add(enableShiftHorizontalScroll);
        category.add(scrollAmount);
        category.add(inverse);

        AxolotlClient.CONFIG.rendering.addSubCategory(category);

        KeyBindingHelper.registerKeyBinding(key);
    }

    public void onRenderTooltip() {
        if (enabled.get()) {
            int i = Mouse.getDWheel();
            if (i != 0) {
                if (i < 0) {
                    onScroll(applyInverse(false));
                }

                if (i > 0) {
                    onScroll(applyInverse(true));
                }
            }
        }
    }

    protected boolean applyInverse(boolean value) {
        if (inverse.get()) {
            return !value;
        }
        return value;
    }

    public void onScroll(boolean reverse) {
        if ((Screen.hasShiftDown() && key.getCode() == Keyboard.KEY_LSHIFT) || key.isPressed()) {
            if (reverse) {
                tooltipOffsetX -= scrollAmount.get();
            } else {
                tooltipOffsetX += scrollAmount.get();
            }
        } else {
            if (reverse) {
                tooltipOffsetY -= scrollAmount.get();
            } else {
                tooltipOffsetY += scrollAmount.get();
            }
        }
    }

    public void resetScroll() {
        tooltipOffsetY = tooltipOffsetX = 0;
    }
}
