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

	private final Map<Account, Identifier> textures = new HashMap<>();
	private final Set<Account> loadingTexture = new HashSet<>();

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
	public void loadTextures(Account account) {
		if (!textures.containsKey(account) && !loadingTexture.contains(account)) {
			ThreadExecuter.scheduleTask(()-> {
				loadingTexture.add(account);
				GameProfile gameProfile;
				try {
					UUID uUID = UUIDTypeAdapter.fromString(account.getUuid());
					gameProfile = new GameProfile(uUID, account.getName());
					client.getSessionService().fillProfileProperties(gameProfile, false);
				} catch (IllegalArgumentException var2) {
					gameProfile = new GameProfile(null, account.getName());
				}
				client.getSkinProvider().loadProfileSkin(gameProfile, (type, identifier, minecraftProfileTexture) -> {
					if (type == MinecraftProfileTexture.Type.SKIN) {
						textures.put(account, identifier);
						loadingTexture.remove(account);
					}
				}, false);
			});
		}

	}

	public Identifier getSkinTexture(Account account) {
		loadTextures(account);
		Identifier id;
		if ((id = textures.get(account)) != null) {
			return id;
		}
		try {
			UUID uuid = UUIDTypeAdapter.fromString(account.getUuid());
			return DefaultSkinHelper.getTexture(uuid);
		} catch (IllegalArgumentException ignored) {
			return DefaultSkinHelper.getTexture();
		}
	}
}
