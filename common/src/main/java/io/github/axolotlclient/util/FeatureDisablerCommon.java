/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.modules.hud.HudManagerCommon;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ToggleSprintHud;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import lombok.Getter;

public abstract class FeatureDisablerCommon {
	@Getter
	private static FeatureDisablerCommon instance;

	private static final Supplier<Boolean> NONE = () -> true;
	protected static final AxoIdentifier CHANNEL_NAME = AxoIdentifier.of("axolotlclient", "block_mods");
	// Features that can be disabled on the server's behalf
	// If something should be added here, feel free to ping us via your favorite way.
	protected static final HashMap<String, ForceableBooleanOption> FEATURES = CommonUtil.make(() -> {
		HashMap<String, ForceableBooleanOption> features = new HashMap<>();
		features.put("freelook", Freelook.getInstance().enabled);
		features.put("timechanger", AxolotlClientCommon.getInstance().getConfig().timeChangerEnabled);
		features.put("lowfire", AxolotlClientCommon.getInstance().getConfig().lowFire);
		features.put("fullbright", AxolotlClientCommon.getInstance().getConfig().fullBright);
		return features;
	});

	private final HashMap<ForceableBooleanOption, String[]> disabledServers = new HashMap<>();
	private final HashMap<ForceableBooleanOption, Supplier<Boolean>> conditions = new HashMap<>();
	private String currentAddress = "";

	protected FeatureDisablerCommon() {
		Preconditions.checkState(instance == null, "singleton already initialized");
		instance = this;
	}

	public void init() {
		setServers(AxolotlClientCommon.getInstance().getConfig().fullBright, NONE, "gommehd");
		setServers(AxolotlClientCommon.getInstance().getConfig().lowFire, NONE, "gommehd");
		setServers(Freelook.getInstance().enabled, () -> Freelook.getInstance().needsDisabling(), "hypixel", "mineplex", "gommehd", "nucleoid", "mccisland");
		setServers(((ToggleSprintHud) HudManagerCommon.getInstance().get(ToggleSprintHud.ID)).toggleSneak, NONE, "hypixel");

		Events.BEGIN_JOIN_SERVER.register(info -> {
			if (info.address() != null) {
				onServerJoin(info.address());
			}
		});

		Events.DISCONNECT.register(this::clear);

		registerChannel();
	}

	protected abstract void registerChannel();

	private void setServers(ForceableBooleanOption option, Supplier<Boolean> condition, String... servers) {
		disabledServers.put(option, servers);
		conditions.put(option, condition);
	}

	public void onServerJoin(String address) {
		currentAddress = address;
		update();
	}

	public void clear() {
		disabledServers.keySet().forEach(option -> option.setForceOff(false, ""));
		FEATURES.values().forEach(option -> option.setForceOff(false, ""));
	}

	public void update() {
		disabledServers.forEach((option, strings) -> disableOption(option, strings, currentAddress));
	}

	private void disableOption(ForceableBooleanOption option, String[] servers, String currentServer) {
		boolean ban = false;
		for (String s : servers) {
			if (currentServer.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
				ban = conditions.get(option).get();
				break;
			}
		}

		if (option.isForceOff() != ban) {
			option.setForceOff(ban, "ban_reason");
		}
	}
}
