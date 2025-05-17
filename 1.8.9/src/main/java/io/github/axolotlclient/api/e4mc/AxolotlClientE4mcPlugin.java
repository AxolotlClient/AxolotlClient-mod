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

package io.github.axolotlclient.api.e4mc;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.multiplayer.ServerInfoUtil;
import io.github.axolotlclient.api.types.Status;
import link.e4mc.E4mcClient;
import link.e4mc.QuiclimeSession;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Setter
public class AxolotlClientE4mcPlugin {

	private String e4mcDomain = null;

	public static final AxolotlClientE4mcPlugin INSTANCE = new AxolotlClientE4mcPlugin();

	public Status.Activity.E4mcMetadata getStatusDescription() {
		Minecraft mc = Minecraft.getInstance();
		var levelName = mc.getServer().getWorldName();
		if (E4mcClient.session == null || E4mcClient.session.state != QuiclimeSession.State.STARTED) {
			if (mc.getServer().getStatus() != null) {
				return new Status.Activity.E4mcMetadata(null, ServerInfoUtil.getServerInfo(levelName, mc.getServer().getStatus()));
			}
			return new Status.Activity.E4mcMetadata(null, ServerInfoUtil.getServerInfo(levelName, null));
		}
		if (!API.getInstance().getApiOptions().allowFriendsServerJoin.get()) {
			return new Status.Activity.E4mcMetadata(null, ServerInfoUtil.getServerInfo(levelName, mc.getServer().getStatus()));
		}
		return new Status.Activity.E4mcMetadata(e4mcDomain, ServerInfoUtil.getServerInfo(levelName, mc.getServer().getStatus()));
	}

}
