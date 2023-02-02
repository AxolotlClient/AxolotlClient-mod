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
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.notifications.Notifications;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.Session;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Auth extends Accounts implements Module {

    @Getter
    private static final Auth Instance = new Auth();

    public final BooleanOption showButton = new BooleanOption("auth.showButton", false);
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final GenericOption viewAccounts = new GenericOption("viewAccounts", "clickToOpen", (x, y) -> client.setScreen(new AccountsScreen(client.currentScreen)));

    @Override
    public void init() {
        load();
        this.auth = new MSAuth(AxolotlClient.LOGGER, this);
        if (isContained(client.getSession().getUuid())) {
            current = getAccounts().stream().filter(account -> account.getUuid().equals(client.getSession().getUuid())).collect(Collectors.toList()).get(0);
            if(current.isExpired()) {
                current.refresh(auth, () -> {});
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
                ((MinecraftClientAccessor) client).setSession(new Session(account.getName(), account.getUuid(), account.getAuthToken(), Session.AccountType.MOJANG.name()));
                save();
                current = account;
                Notifications.getInstance().addStatus(I18n.translate("auth.notif.title"), I18n.translate("auth.notif.login.successful", current.getName()));
                AxolotlClient.LOGGER.info("Successfully logged in as " + current.getName());
            } catch (Exception e) {
                AxolotlClient.LOGGER.error("Failed to log in! ", e);
                Notifications.getInstance().addStatus(I18n.translate("auth.notif.title"), I18n.translate("auth.notif.login.failed"));
            }
        };

        if(account.isExpired()){
            Notifications.getInstance().addStatus(I18n.translate("auth.notif.title"), I18n.translate("auth.notif.refreshing", current.getName()));
            account.refresh(auth, runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    protected Logger getLogger() {
        return AxolotlClient.LOGGER;
    }

    public void loadSkinFile(Identifier skinId, MSAccount account){
        if(!account.isOffline() && MinecraftClient.getInstance().getTextureManager().getTexture(skinId) == null) {
            try {
                BufferedImage image = ImageIO.read(Auth.getInstance().getSkinFile(account));
                if (image != null) {
                    client.getTextureManager().loadTexture(skinId, new NativeImageBackedTexture(image));
                    AxolotlClient.LOGGER.debug("Loaded skin file for "+ account.getName());
                }
            } catch (IOException e) {
                AxolotlClient.LOGGER.warn("Couldn't load skin file for " + account.getName());
            }
        }
    }
}
