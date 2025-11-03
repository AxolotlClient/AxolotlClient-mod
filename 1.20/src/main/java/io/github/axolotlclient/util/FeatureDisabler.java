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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.axolotlclient.AxolotlClient;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class FeatureDisabler extends FeatureDisablerCommon {
	@Getter
	private static final FeatureDisablerCommon instance = new FeatureDisabler();

	@Override
	protected void registerChannel() {
		ClientPlayConnectionEvents.INIT.register((handler0, client0) ->
			ClientPlayNetworking.registerGlobalReceiver((Identifier) CHANNEL_NAME, (client, handler, buf, responseSender) -> {
				JsonArray array = JsonParser.parseString(buf.readString()).getAsJsonArray();
				for (JsonElement element : array) {
					try {
						FEATURES.get(element.getAsString()).setForceOff(true, "ban_reason");
					} catch (Exception e) {
						AxolotlClient.LOGGER.error("Failed to disable " + element.getAsString() + "!");
					}
				}
			})
		);
	}
}
