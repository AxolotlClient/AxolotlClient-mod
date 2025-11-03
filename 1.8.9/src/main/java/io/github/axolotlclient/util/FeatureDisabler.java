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

import java.util.List;

import io.github.axolotlclient.AxolotlClient;
import lombok.Getter;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

public class FeatureDisabler extends FeatureDisablerCommon {
	@Getter
	private static final FeatureDisablerCommon instance = new FeatureDisabler();

	private static final String CHANNEL_NAME = "AXO|block_mods";

	@Override
	protected void registerChannel() {
		ClientPlayNetworking.registerListener(CHANNEL_NAME, (client, handler, buf) -> {
			List<String> array = (List<String>) GsonHelper.read(buf.readString(32767));
			for (String element : array) {
				try {
					FEATURES.get(element).setForceOff(true, "ban_reason");
				} catch (Exception e) {
					AxolotlClient.LOGGER.error("Failed to disable " + element + "!");
				}
			}
			return true;
		});
	}
}
