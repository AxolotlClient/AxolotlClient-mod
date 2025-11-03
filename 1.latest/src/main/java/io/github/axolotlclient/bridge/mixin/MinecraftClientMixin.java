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

package io.github.axolotlclient.bridge.mixin;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.github.axolotlclient.bridge.AxoGameOptions;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.AxoPlayerListEntry;
import io.github.axolotlclient.bridge.AxoSession;
import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.resource.AxoResourceManager;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.bridge.world.AxoWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin implements AxoMinecraftClient {
	@Shadow
	public LocalPlayer player;

	@Shadow
	public ClientLevel level;

	@Shadow
	@Final
	public Font font;

	@Shadow
	@Final
	public Options options;

	@Shadow
	public abstract boolean isSingleplayer();

	@Shadow
	public abstract ServerData getCurrentServer();

	@Shadow
	@Final
	public Gui gui;

	@Shadow
	@Final
	private User user;

	@Shadow
	public Screen screen;

	@Shadow
	public abstract ResourceManager getResourceManager();

	@Shadow
	@Final
	public LevelRenderer levelRenderer;

	@Shadow
	private Entity cameraEntity;

	@Override
	public @Nullable AxoPlayer br$getPlayer() {
		return player;
	}

	@Override

	public AxoWorld br$getWorld() {
		return level;
	}

	@Override
	public AxoFont br$getFont() {
		return font;
	}

	@Override

	public AxoGameOptions br$getGameOptions() {
		return options;
	}

	@Override
	public boolean br$isLocalServer() {
		return isSingleplayer();
	}

	@Override
	public String br$getServerAddress() {
		return Optional.ofNullable(getCurrentServer()).map(x -> x.ip).orElse(null);
	}

	@Override
	public String br$getServerName() {
		return Optional.ofNullable(getCurrentServer()).map(x -> x.name).orElse(null);
	}

	@Override
	public Collection<? extends AxoPlayerListEntry> br$getOnlinePlayers() {
		return player.connection.getOnlinePlayers();
	}

	@Override
	public void br$sendToClient(AxoText msg) {
		gui.getChat().addMessage((Component) msg);
	}

	@Override
	public void br$sendToServer(String msg) {
		if (msg.startsWith("/")) {
			player.connection.sendCommand(msg.substring(1));
		} else {
			player.connection.sendChat(msg);
		}
	}

	@Override
	public AxoSession br$getSession() {
		// TODO... -?
		return new AxoSession(user.getName(), user.getProfileId().toString(), user.getAccessToken());
	}

	@Override
	public void br$reinitScreen() {
		if (screen != null) {
			screen.init((Minecraft) (Object) this, screen.width, screen.height);
		}
	}

	@Override
	public AxoResourceManager br$getResourceManager() {
		return getResourceManager();
	}

	@Override
	public Object br$getScreen() {
		return screen;
	}

	@Override
	public void br$notifyLevelRenderer() {
		levelRenderer.needsUpdate();
	}

	@Override
	public AxoEntity br$getCameraEntity() {
		return cameraEntity;
	}

	@Override
	public List<String> br$getSidebar() {
		List<String> lines = new ArrayList<>();
		Minecraft client = Minecraft.getInstance();
		if (client.level == null)
			return lines;

		Scoreboard scoreboard = client.level.getScoreboard();
		Objective sidebar = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
		if (sidebar == null)
			return lines;

		Collection<PlayerScoreEntry> scores = scoreboard.listPlayerScores(sidebar);
		List<PlayerScoreEntry> list = scores.stream().filter(
				input -> input != null && !input.isHidden())
			.collect(Collectors.toList());

		if (list.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
		} else {
			scores = list;
		}

		for (PlayerScoreEntry score : scores) {
			PlayerTeam team = scoreboard.getPlayerTeam(score.owner());
			if (team == null)
				return lines;
			String text = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
			if (!text.trim().isEmpty())
				lines.add(text);
		}

		lines.add(sidebar.getDisplayName().getString());
		Collections.reverse(lines);

		return lines;
	}
}
