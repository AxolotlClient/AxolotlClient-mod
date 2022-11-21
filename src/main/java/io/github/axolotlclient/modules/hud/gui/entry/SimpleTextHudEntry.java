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

package io.github.axolotlclient.modules.hud.gui.entry;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.Justification;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class SimpleTextHudEntry extends TextHudEntry implements DynamicallyPositionable {

    protected final EnumOption justification = new EnumOption("axolotlclient.justification", Justification.values(), Justification.CENTER.toString());
    protected final EnumOption anchor = DefaultOptions.getAnchorPoint();

    private final IntegerOption minWidth;

    public SimpleTextHudEntry() {
        this(53, 13, true);
    }

    protected SimpleTextHudEntry(int width, int height) {
        this(width, height, true);
    }

    protected SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
        minWidth = new IntegerOption("minwidth", width, 1, 300);
    }

    @Override
    public void renderComponent(float delta) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture();
        DrawPosition pos = getPos();
        String value = getValue();

        int valueWidth = client.textRenderer.getStringWidth(value);
        int elementWidth = valueWidth + 4;

        int min = minWidth.get();
        if (elementWidth < min) {
            if (width != min) {
                setWidth(min);
                onBoundsUpdate();
            }
        } else if (elementWidth != width) {
            setWidth(elementWidth);
            onBoundsUpdate();
        }
        drawString(
                value,
                pos.x() + Justification.valueOf(justification.get()).getXOffset(valueWidth, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                getTextColor().getAsInt(), shadow.get()
        );
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public Color getTextColor() {
        return textColor.get();
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        DrawPosition pos = getPos();
        String value = getPlaceholder();
        drawString(
                value,
                pos.x() + Justification.valueOf(justification.get()).getXOffset(value, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.get().getAsInt(), shadow.get()
        );
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(justification);
        options.add(anchor);
        options.add(minWidth);
        return options;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.valueOf(anchor.get());
    }
}
