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

package io.github.axolotlclient.modules.auth;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.util.UUIDTypeAdapter;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.ThreadExecuter;
import io.github.axolotlclient.util.notifications.Notifications;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.Session;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Auth extends Accounts implements Module {

	@Getter
	private static final Auth Instance = new Auth();

	public final BooleanOption showButton = new BooleanOption("auth.showButton", false);
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final GenericOption viewAccounts = new GenericOption("viewAccounts", "clickToOpen", (x, y) -> client.setScreen(new AccountsScreen(client.currentScreen)));

	private final Map<String, Identifier> textures = new HashMap<>();
	private final Set<String> loadingTexture = new HashSet<>();
	private final Map<String, GameProfile> profileCache = new WeakHashMap<>();

	@Override
	public void init() {
		load();
		this.auth = new MSAuth(AxolotlClient.LOGGER, this);
		if (isContained(client.getSession().getUuid())) {
			current = getAccounts().stream().filter(account -> account.getUuid().equals(client.getSession().getUuid())).collect(Collectors.toList()).get(0);
			if (current.isExpired()) {
				current.refresh(auth, () -> {
				});
			}
		} else {
			current = new Account(client.getSession().getUsername(), client.getSession().getUuid(), client.getSession().getAccessToken());
		}

		OptionCategory category = new OptionCategory("auth");
		category.add(showButton, viewAccounts);
		AxolotlClient.CONFIG.general.add(category);
	}

	@Override
	protected Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	protected void login(Account account) {
		if (client.world != null) {
			return;
		}

		Runnable runnable = () -> {
			try {
				API.getInstance().shutdown();
				((MinecraftClientAccessor) client).setSession(new Session(account.getName(), account.getUuid(), account.getAuthToken(), Session.AccountType.MOJANG.name()));
				if (!account.isOffline()) {
					API.getInstance().startup(account.getUuid());
				}
				save();
				current = account;
				Notifications.getInstance().addStatus("auth.notif.title", "auth.notif.login.successful", current.getName());
				AxolotlClient.LOGGER.info("Successfully logged in as " + current.getName());
			} catch (Exception e) {
				AxolotlClient.LOGGER.error("Failed to log in! ", e);
				Notifications.getInstance().addStatus("auth.notif.title", "auth.notif.login.failed");
			}
		};

		if (account.isExpired() && !account.isOffline()) {
			Notifications.getInstance().addStatus("auth.notif.title", "auth.notif.refreshing", account.getName());
			account.refresh(auth, runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	protected Logger getLogger() {
		return AxolotlClient.LOGGER;
	}

	@Override
	public void loadTextures(String uuid, String name) {
		if (!textures.containsKey(uuid) && !loadingTexture.contains(uuid)) {
			ThreadExecuter.scheduleTask(()-> {
				loadingTexture.add(uuid);
				GameProfile gameProfile;
				if(profileCache.containsKey(uuid)){
					gameProfile = profileCache.get(uuid);
				} else {
					try {
						UUID uUID = UUIDTypeAdapter.fromString(uuid);
						gameProfile = new GameProfile(uUID, name);
						client.getSessionService().fillProfileProperties(gameProfile, false);
					} catch (IllegalArgumentException var2) {
						gameProfile = new GameProfile(null, name);
					}
					profileCache.put(uuid, gameProfile);
				}
				client.getSkinProvider().loadProfileSkin(gameProfile, (type, identifier, minecraftProfileTexture) -> {
					if (type == MinecraftProfileTexture.Type.SKIN) {
						textures.put(uuid, identifier);
						loadingTexture.remove(uuid);
					}
				}, false);
			});
		}

	}

	public Identifier getSkinTexture(Account account) {
		return getSkinTexture(account.getUuid(), account.getName());
	}

	public Identifier getSkinTexture(String uuid, String name){
		loadTextures(uuid, name);
		Identifier id;
		if ((id = textures.get(uuid)) != null) {
			return id;
		}
		try {
			UUID uUID = UUIDTypeAdapter.fromString(uuid);
			return DefaultSkinHelper.getTexture(uUID);
		} catch (IllegalArgumentException ignored) {
			return DefaultSkinHelper.getTexture();
		}
	}
}
