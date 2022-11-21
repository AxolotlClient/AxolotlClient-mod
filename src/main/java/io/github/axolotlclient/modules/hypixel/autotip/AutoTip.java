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

package io.github.axolotlclient.modules.hypixel.autotip;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;

public class AutoTip implements AbstractHypixelMod {

    public static AutoTip INSTANCE = new AutoTip();

    private final OptionCategory category = new OptionCategory("axolotlclient.autotip");

    private final BooleanOption enabled = new BooleanOption("axolotlclient.enabled", false);
    private long lastTime;
    private boolean init = false;

    @Override
    public void init() {
        category.add(enabled);
        init=true;
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    @Override
    public void tick() {
        if(init) {
            if (System.currentTimeMillis() - lastTime > 1200000 && Util.getCurrentServerAddress() != null &&
                    Util.currentServerAddressContains("hypixel") &&
                    enabled.get()) {

                if(MinecraftClient.getInstance().player!=null) {
                    MinecraftClient.getInstance().player.sendChatMessage("/tip all");
                    lastTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public boolean tickable() {
        return true;
    }
}
