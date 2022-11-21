/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
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
 */

package io.github.axolotlclient.modules.hud.util;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.DoubleOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.modules.hud.gui.component.HudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.CardinalOrder;
import lombok.experimental.UtilityClass;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

@UtilityClass
public class DefaultOptions {

    public static DoubleOption getX(double defaultX, HudEntry entry) {
        return new DoubleOption("x", value -> entry.onBoundsUpdate(), defaultX, 0, 1);
    }

    public static DoubleOption getY(double defaultY, HudEntry entry) {
        return new DoubleOption("y", value -> entry.onBoundsUpdate(), defaultY, 0, 1);
    }

    public static DoubleOption getScale(HudEntry entry) {
        return new DoubleOption("axolotlclient.scale", value -> entry.onBoundsUpdate(), 1, 0, 2);
    }

    public static BooleanOption getEnabled() {
        return new BooleanOption("axolotlclient.enabled", false);
    }

    public static EnumOption getAnchorPoint() {
        return getAnchorPoint(AnchorPoint.TOP_LEFT);
    }

    public static EnumOption getAnchorPoint(AnchorPoint defaultValue) {
        return new EnumOption("axolotlclient.anchorpoint", AnchorPoint.values(), defaultValue.toString());
    }

    public static EnumOption getCardinalOrder(CardinalOrder defaultValue) {
        return new EnumOption("axolotlclient.cardinalorder", CardinalOrder.values(), defaultValue.toString());
    }
}
