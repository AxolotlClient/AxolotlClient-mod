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

package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.modules.freelook.Freelook;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public class FeatureDisabler {

    private static final HashMap<BooleanOption, String[]> disabledServers = new HashMap<>();
    private static final HashMap<BooleanOption, Supplier<Boolean>> conditions = new HashMap<>();

    private static final Supplier<Boolean> NONE = ()-> true;

    private static String currentAddress;

    public static void init() {
        setServers(AxolotlClient.CONFIG.fullBright, NONE, "gommehd");
        setServers(AxolotlClient.CONFIG.timeChangerEnabled, NONE, "gommehd");
        setServers(Freelook.getInstance().enabled, () -> Freelook.getInstance().needsDisabling(), "hypixel", "mineplex", "gommehd", "nucleoid");


    }

    public static void onServerJoin(String address) {
        currentAddress = address;
        update();
    }

    public static void clear() {
        disabledServers.forEach((option, strings) -> option.setForceOff(false, "ban_reason"));
    }

    private static void disableOption(BooleanOption option, String[] servers, String currentServer) {
        boolean ban = false;
        for (String s : servers) {
            if (currentServer.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                ban = conditions.get(option).get();
                break;
            }
        }

        if (option.getForceDisabled() != ban) {
            option.setForceOff(ban, "ban_reason");
        }
    }

    private static void setServers(BooleanOption option, Supplier<Boolean> condition, String... servers) {
        disabledServers.put(option, servers);
        conditions.put(option, condition);
    }

    public static void update(){
        disabledServers.forEach((option, strings) -> disableOption(option, strings, currentAddress));
    }
}
