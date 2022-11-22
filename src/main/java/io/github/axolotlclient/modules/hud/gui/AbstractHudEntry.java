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

package io.github.axolotlclient.modules.hud.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.modules.hud.gui.component.HudEntry;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class AbstractHudEntry extends DrawUtil implements HudEntry {

    protected final BooleanOption enabled = DefaultOptions.getEnabled();
    protected final DoubleOption scale = DefaultOptions.getScale(this);
    private final DoubleOption x = DefaultOptions.getX(getDefaultX(), this);
    private final DoubleOption y = DefaultOptions.getY(getDefaultY(), this);

    private Rectangle trueBounds = null;
    private Rectangle renderBounds = null;
    private DrawPosition truePosition = null;
    private DrawPosition renderPosition;

    @Setter
    @Getter
    protected int width;
    @Setter @Getter
    protected int height;

    @Setter
    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();

    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
    }

    public void init() {}

    public void renderPlaceholderBackground() {
        if (hovered) {
            fillRect(getTrueBounds(), Color.SELECTOR_BLUE.withAlpha(100));
        } else {
            fillRect(getTrueBounds(), Color.WHITE.withAlpha(50));
        }
        outlineRect(getTrueBounds(), Color.BLACK);
    }

    public int getRawX() {
        return getPos().x;
    }

    public void setX(int x) {
        this.x.set((double) intToFloat(x, (int) Util.getWindow().getScaledWidth(),
                0
        ));
    }

    public int getRawY() {
        return getPos().y();
    }

    public void setY(int y) {
        this.y.set((double) intToFloat(y, (int) Util.getWindow().getScaledHeight(),
                0
        ));
    }

    public Rectangle getTrueBounds() {
        return trueBounds;
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     *
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return renderBounds;
    }

    @Override
    public float getScale() {
        return scale.get().floatValue();
    }

    public void scale() {
        float scale = getScale();
        GlStateManager.scalef(scale, scale, 1);
    }

    @Override
    public DrawPosition getPos() {
        return renderPosition;
    }

    @Override
    public DrawPosition getTruePos() {
        return truePosition;
    }

    @Override
    public void onBoundsUpdate() {
        setBounds();
    }

    public void setBounds() {
        setBounds(getScale());
    }

    @Override
    public int getRawTrueX() {
        return truePosition.x();
    }

    @Override
    public int getRawTrueY() {
        return truePosition.y();
    }

    @Override
    public int getTrueWidth() {
        if (trueBounds == null) {
            return HudEntry.super.getTrueWidth();
        }
        return trueBounds.width();
    }

    @Override
    public int getTrueHeight() {
        if (trueBounds == null) {
            return HudEntry.super.getTrueHeight();
        }
        return trueBounds.height();
    }

    public void setBounds(float scale) {
        if (Util.getWindow() == null) {
            truePosition = new DrawPosition(0, 0);
            renderPosition = new DrawPosition(0, 0);
            renderBounds = new Rectangle(0, 0, 1, 1);
            trueBounds = new Rectangle(0, 0, 1, 1);
            return;
        }
        int scaledX = floatToInt(x.get().floatValue(), (int) Util.getWindow().getScaledWidth(), 0) - offsetTrueWidth();
        int scaledY = floatToInt(y.get().floatValue(), (int) Util.getWindow().getScaledHeight(), 0) - offsetTrueHeight();
        if (scaledX < 0) {
            scaledX = 0;
        }
        if (scaledY < 0) {
            scaledY = 0;
        }
        int trueWidth = (int) (getWidth() * getScale());
        if (trueWidth < Util.getWindow().getScaledWidth() && scaledX + trueWidth > Util.getWindow().getScaledWidth()) {
            scaledX = (int) (Util.getWindow().getScaledWidth() - trueWidth);
        }
        int trueHeight = (int) (getHeight() * getScale());
        if (trueHeight < Util.getWindow().getScaledHeight() && scaledY + trueHeight > Util.getWindow().getScaledHeight()) {
            scaledY = (int) (Util.getWindow().getScaledHeight() - trueHeight);
        }
        truePosition.x = scaledX;
        truePosition.y = scaledY;
        renderPosition = truePosition.divide(getScale());
        renderBounds = new Rectangle(renderPosition.x(), renderPosition.y(), getWidth(), getHeight());
        trueBounds = new Rectangle(scaledX, scaledY, (int) (getWidth() * getScale()), (int) (getHeight() * getScale()));
    }

    /**
     * Returns a list of options that should be shown in configuration screens
     *
     * @return List of options
     */
    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = new ArrayList<>();
        options.add(enabled);
        options.add(scale);
        return options;
    }

    /**
     * Returns a list of options that should be saved. By default, this includes {@link #getConfigurationOptions()}
     *
     * @return a list of options.
     */
    @Override
    public List<Option<?>> getSaveOptions() {
        List<Option<?>> options = getConfigurationOptions();
        options.add(x);
        options.add(y);
        return options;
    }

    public OptionCategory getOptionsAsCategory(){
        OptionCategory cat = new OptionCategory(getNameKey(), false);
        cat.add(getConfigurationOptions());
        return cat;
    }
    public OptionCategory getAllOptions() {
        List<Option<?>> options = getSaveOptions();
        OptionCategory cat = new OptionCategory(getNameKey());
        cat.add(options);
        return cat;
    }

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void setEnabled(boolean value) {
        enabled.set(value);
    }

}
