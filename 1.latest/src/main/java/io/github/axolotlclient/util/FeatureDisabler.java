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

import com.mojang.serialization.Codec;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FeatureDisabler extends FeatureDisablerCommon {
	@Getter
	private static final FeatureDisablerCommon instance = new FeatureDisabler();

	private static final CustomPacketPayload.Type<FeaturePayload> CHANNEL_ID =
		new CustomPacketPayload.Type<>((ResourceLocation) CHANNEL_NAME);

	@Override
	protected void registerChannel() {
		PayloadTypeRegistry.playS2C().register(CHANNEL_ID, FeaturePayload.CODEC);
		ClientPlayConnectionEvents.INIT.register((handler0, client0) -> {
			ClientPlayNetworking.registerGlobalReceiver(CHANNEL_ID, (payload, ctx) -> {
				for (String feature : payload.features) {
					try {
						ForceableBooleanOption e = FEATURES.get(feature);
						e.setForceOff(true, "ban_reason");
					} catch (Exception e) {
						AxolotlClient.LOGGER.error("Failed to disable " + feature + "!");
					}
				}
			});
		});
	}

	private record FeaturePayload(List<String> features) implements CustomPacketPayload {
		public static final StreamCodec<ByteBuf, FeaturePayload> CODEC =
			ByteBufCodecs.fromCodec(Codec.STRING.listOf().xmap(FeaturePayload::new, FeaturePayload::features));

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return CHANNEL_ID;
		}
	}
}
