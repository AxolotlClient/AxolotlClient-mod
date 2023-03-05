/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.axolotlclient.api.API;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;

public class AxolotlClientTest implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) ->
				ClientCommandManager.getDispatcher().register(ClientCommandManager.literal("apisend")
						.then(ClientCommandManager.argument("sample response", StringArgumentType.greedyString()).executes(context -> {
			API.getInstance().onMessage(context.getInput());
			return 1;
		}))));

	}
}
