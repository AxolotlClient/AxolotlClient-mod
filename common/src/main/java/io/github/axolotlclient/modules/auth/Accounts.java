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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Accounts {

    private final List<MSAccount> accounts = new ArrayList<>();
    protected MSAccount current;
    protected MSAuth auth;

    public MSAuth getAuth() {
        return auth;
    }

    public List<MSAccount> getAccounts() {
        return accounts;
    }

    public void load() {
        if (getAccountsSaveFile().toFile().exists()) {
            try {
                JsonObject list = GsonHelper.GSON.fromJson(String.join("", Files.readAllLines(getAccountsSaveFile())), JsonObject.class);
                if (list != null) {
                    list.get("accounts").getAsJsonArray().forEach(jsonElement -> accounts.add(MSAccount.deserialize(jsonElement.getAsJsonObject())));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                getAccountsSaveFile().toFile().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected Path getAccountsSaveFile() {
        return getConfigDir().resolve("accounts.json");
    }

    protected abstract Path getConfigDir();

    public void addAccount(MSAccount account) {
        accounts.add(account);
    }

    public MSAccount getCurrent() {
        return current;
    }

    protected abstract void login(MSAccount account);

    public void removeAccount(MSAccount account) {
        accounts.remove(account);
        save();
    }

    public void save() {
        JsonArray array = new JsonArray();
        accounts.forEach(account -> array.add(account.serialize()));
        JsonObject object = new JsonObject();
        object.add("accounts", array);
        try {
            Files.write(getAccountsSaveFile(), GsonHelper.GSON.toJson(object).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            getLogger().error("Failed to save acounts config!", e);
        }
    }

    protected abstract Logger getLogger();

    protected boolean isContained(String uuid) {
        return accounts.stream().anyMatch(account -> account.getUuid().equals(uuid));
    }
}
