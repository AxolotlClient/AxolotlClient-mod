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

package io.github.axolotlclient.modules.hypixel.autoboop;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.Text;

public class AutoBoop implements AbstractHypixelMod {
    public static AutoBoop Instance = new AutoBoop();

    protected OptionCategory cat = new OptionCategory("axolotlclient.autoBoop");
    protected BooleanOption enabled = new BooleanOption("axolotlclient.enabled", "autoBoop", false);

    @Override
    public void init() {

        cat.add(enabled);
    }

    @Override
    public OptionCategory getCategory() {
        return cat;
    }

    public void onMessage(Text message){
        if(enabled.get() && message.asUnformattedString().contains("Friend >") && message.asUnformattedString().contains("joined.")){
            String player = message.asUnformattedString().substring(message.asFormattedString().indexOf(">") + 2, message.asUnformattedString().lastIndexOf(" "));
            Util.sendChatMessage( "/boop "+player);
        }
    }
}
