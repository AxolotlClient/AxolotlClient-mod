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

package io.github.axolotlclient.bridge;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import io.github.axolotlclient.bridge.internal.RequiresImpl;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.resource.AxoResourceManager;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.bridge.world.AxoWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface AxoMinecraftClient extends Executor {
	static AxoMinecraftClient getInstance() {
		return PlatformImplInternal.getMinecraftClientInstance();
	}

	static int getCurrentFps() {
		return PlatformImplInternal.getCurrentFps();
	}

	@RequiresImpl
	@Contract(pure = true)
	@Nullable
	default AxoPlayer br$getPlayer() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoWorld br$getWorld() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoFont br$getFont() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoGameOptions br$getGameOptions() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoSession br$getSession() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default String br$getServerAddress() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default String br$getServerName() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default boolean br$isLocalServer() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default Collection<? extends AxoPlayerListEntry> br$getOnlinePlayers() {
		throw BridgeUtil.noImpl();
	}

	/**
	 * Sends a message <i>as</i> the client to the server.
	 *
	 * @param msg
	 */
	@RequiresImpl
	default void br$sendToServer(String msg) {
		throw BridgeUtil.noImpl();
	}

	/**
	 * Sends a message to the client.
	 *
	 * @param msg
	 */
	@RequiresImpl
	default void br$sendToClient(AxoText msg) {
		throw BridgeUtil.noImpl();
	}

	/**
	 * Re-initializes the current screen
	 */
	@RequiresImpl
	default void br$reinitScreen() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoResourceManager br$getResourceManager() {
		throw BridgeUtil.noImpl();
	}

	/*
	 * Return type is an opaque object because we do not have a gui bridge currently.
	 */
	@RequiresImpl
	default Object br$getScreen() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default AxoEntity br$getCameraEntity() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$notifyLevelRenderer() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default List<String> br$getSidebar() {
		throw BridgeUtil.noImpl();
	}
}
