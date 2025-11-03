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

package io.github.axolotlclient.modules.freelook;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import io.github.axolotlclient.bridge.AxoPerspective;
import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.key.AxoKeys;
import io.github.axolotlclient.modules.AbstractCommonModule;
import io.github.axolotlclient.util.FeatureDisablerCommon;
import io.github.axolotlclient.util.options.ForceableBooleanOption;

public class Freelook extends AbstractCommonModule {
	private static final Freelook INSTANCE = new Freelook();
	private static final AxoKeybinding KEY = AxoKeybinding.create(AxoKeys.KEY_V, "key.freelook");
	private static final AxoKeybinding KEY_ALT = AxoKeybinding.create(null, "key.freelook.alt");
	public final ForceableBooleanOption enabled = new ForceableBooleanOption("enabled", false);
	private final OptionCategory category = OptionCategory.create("freelook");
	private final StringArrayOption mode =
		new StringArrayOption("mode", new String[]{"snap_AxoPerspective", "freelook"}, "freelook",
			value -> FeatureDisablerCommon.getInstance().update()
		);
	private final BooleanOption invert = new BooleanOption("invert", false);
	private final EnumOption<AxoPerspective> perspective =
		new EnumOption<>("AxoPerspective", AxoPerspective.class, AxoPerspective.THIRD_PERSON_BACK);
	private final BooleanOption toggle = new BooleanOption("toggle", "freelook.toggle.tooltip", false);
	private final EnumOption<AxoPerspective> AxoPerspectiveAlt = new EnumOption<>("AxoPerspective.alt", AxoPerspective.class,
		AxoPerspective.THIRD_PERSON_FRONT);
	private final BooleanOption toggleAlt = new BooleanOption("toggle.alt", false);
	private final WrappedValue active = new WrappedValue(), activeAlt = new WrappedValue();
	private float yaw, pitch;
	private final Deque<AxoPerspective> previousAxoPerspectives = new ArrayDeque<>();

	public static Freelook getInstance() {
		return INSTANCE;
	}

	@Override
	public void init() {
		category.add(enabled, mode, perspective, invert, toggle);
		category.add(AxoPerspectiveAlt, toggleAlt);
		AxolotlClientCommon.getInstance().getConfig().addCategory(category);
	}

	@Override
	public void tick() {
		if (!enabled.get() || client.br$getScreen() != null) return;
		tickSet(toggle, KEY, perspective, active);
		tickSet(toggleAlt, KEY_ALT, AxoPerspectiveAlt, activeAlt);
	}

	private void tickSet(BooleanOption toggle, AxoKeybinding key, EnumOption<AxoPerspective> perspective, WrappedValue active) {
		if (toggle.get()) {
			if (key.br$consumeClick()) {
				if (active.val) {
					stop(active);
				} else {
					start(perspective.get(), active);
				}
			}
		} else {
			if (key.br$isPressed()) {
				if (!active.val) {
					start(perspective.get(), active);
				}
			} else if (active.val) {
				stop(active);
			}
		}
	}

	private void stop(WrappedValue active) {
		active.val = false;
		client.br$notifyLevelRenderer();
		setAxoPerspective(previousAxoPerspectives.pop());
	}

	private void start(AxoPerspective AxoPerspective, WrappedValue active) {
		previousAxoPerspectives.push(client.br$getGameOptions().br$getCameraType());
		active.val = true;
		setAxoPerspective(AxoPerspective);

		AxoEntity camera = client.br$getCameraEntity();

		if (camera == null) camera = client.br$getPlayer();
		if (camera == null) return;

		yaw = camera.br$getYaw();
		pitch = camera.br$getPitch();
	}

	private void setAxoPerspective(AxoPerspective AxoPerspective) {
		client.br$getGameOptions().br$setCameraType(AxoPerspective);
	}

	public boolean consumeRotation(double dx, double dy) {
		if (!(active.val || activeAlt.val) || !enabled.get() || !mode.get().equals("freelook")) return false;

		if (!invert.get()) dy = -dy;
		if (client.br$getGameOptions().br$getCameraType().isMirrored() ||
			client.br$getGameOptions().br$getCameraType().isFirstPerson()) {
			dy *= -1;
		}

		yaw += (float) (dx * 0.15F);
		pitch += (float) (dy * 0.15F);

		if (pitch > 90) {
			pitch = 90;
		} else if (pitch < -90) {
			pitch = -90;
		}

		client.br$notifyLevelRenderer();
		return true;
	}

	public float yaw(float defaultValue) {
		if (!(active.val || activeAlt.val) || !enabled.get() || !mode.get().equals("freelook")) return defaultValue;

		return yaw;
	}

	public float pitch(float defaultValue) {
		if (!(active.val || activeAlt.val) || !enabled.get() || !mode.get().equals("freelook")) return defaultValue;

		return pitch;
	}

	public boolean needsDisabling() {
		return mode.get().equals("freelook");
	}

	public boolean isActive() {
		return active.val || activeAlt.val;
	}

	private static class WrappedValue {
		boolean val;
	}
}
