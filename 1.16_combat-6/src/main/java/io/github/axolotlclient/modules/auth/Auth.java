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

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.notifications.Notifications;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.Session;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Auth extends Accounts implements Module {

	@Getter
	private final static Auth Instance = new Auth();
	private final MinecraftClient client = MinecraftClient.getInstance();
	public final BooleanOption showButton = new BooleanOption("auth.showButton", false);
	private final GenericOption viewAccounts = new GenericOption("viewAccounts", "clickToOpen", (x, y) -> client.openScreen(new AccountsScreen(client.currentScreen)));

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
			current = new MSAccount(client.getSession().getUsername(), client.getSession().getUuid(), client.getSession().getAccessToken());
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
	protected void login(MSAccount account) {
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
				Notifications.getInstance().addStatus(new TranslatableText("auth.notif.title"), new TranslatableText("auth.notif.login.successful", current.getName()));
			} catch (Exception e) {
				e.printStackTrace();
				Notifications.getInstance().addStatus(new TranslatableText("auth.notif.title"), new TranslatableText("auth.notif.login.failed"));
			}
		};

		if (account.isExpired() && !account.isOffline()) {
			Notifications.getInstance().addStatus(new TranslatableText("auth.notif.title"), new TranslatableText("auth.notif.refreshing", account.getName()));
			account.refresh(auth, runnable);
		} else {
			runnable.run();
		}
	}

	@Override
	protected Logger getLogger() {
		return AxolotlClient.LOGGER;
	}

	public void loadSkinFile(Identifier skinId, MSAccount account) {
		if (!account.isOffline() && MinecraftClient.getInstance().getTextureManager().getTexture(skinId) == null) {
			try {
				MinecraftClient.getInstance().getTextureManager().registerTexture(skinId,
						new NativeImageBackedTexture(NativeImage.read(Files.newInputStream(getSkinFile(account).toPath()))));
				AxolotlClient.LOGGER.debug("Loaded skin file for " + account.getName());
			} catch (IOException e) {
				AxolotlClient.LOGGER.warn("Couldn't load skin file for " + account.getName());
			}
		}
	}
}
