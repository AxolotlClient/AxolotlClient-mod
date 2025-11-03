/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.bridge.impl;

import com.mojang.brigadier.CommandDispatcher;
import io.github.axolotlclient.bridge.events.Events;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class Bridge {
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> Events.CLIENT_START.invoker().run());
		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> Events.CLIENT_STOP.invoker().run());
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> Events.TICK.invoker().run());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

			@Override
			public void apply(ResourceManager manager) {
				Events.END_RESOURCE_RELOAD.invoker().run();
			}

			@Override
			public Identifier getFabricId() {
				return new Identifier("axolotlclient", "bridge/resource_listener");
			}
		});

		ClientPlayConnectionEvents.INIT.register((handler, client) ->
			Events.BEGIN_JOIN_SERVER.invoker().accept(new Events.ServerJoinInfo(handler.getConnection().getAddress().toString())));

		ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, sender, minecraftClient) -> Events.CONNECTION_PLAY_READY.invoker().run());
		ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> Events.DISCONNECT.invoker().run());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void postInit() {
		Events.COMMAND_REGISTER.invoker().accept(
			() -> (CommandDispatcher) ClientCommandManager.DISPATCHER
		);
	}
}
