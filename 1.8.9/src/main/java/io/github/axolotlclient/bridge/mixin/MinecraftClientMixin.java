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
import com.google.common.util.concurrent.ListenableFuture;
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
import net.minecraft.client.Session;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.resource.manager.ResourceManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin implements AxoMinecraftClient {
	@Shadow
	public TextRenderer textRenderer;

	@Shadow
	public LocalClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Shadow
	public GameOptions options;

	@Shadow
	@Final
	private Session session;

	@Shadow
	public abstract boolean isInSingleplayer();

	@Shadow
	public abstract ServerListEntry getCurrentServerEntry();

	@Shadow
	public GameGui gui;

	@Shadow
	public Screen screen;

	@Shadow
	public abstract ResourceManager getResourceManager();

	@Shadow
	public abstract ListenableFuture<Object> submit(Runnable runnable);

	@Shadow
	public WorldRenderer worldRenderer;

	@Shadow
	private Entity camera;

	@Shadow
	private String serverAddress;

	@Override
	public @Nullable AxoPlayer br$getPlayer() {
		return player;
	}

	@Override
	public AxoWorld br$getWorld() {
		return world;
	}

	@Override
	public AxoFont br$getFont() {
		return textRenderer;
	}

	@Override

	public AxoGameOptions br$getGameOptions() {
		return options;
	}

	@Override
	public AxoSession br$getSession() {
		return new AxoSession(session.getUsername(), session.getUuid(), session.getAccessToken());
	}

	@Override
	public boolean br$isLocalServer() {
		return isInSingleplayer();
	}

	@Override
	public String br$getServerAddress() {
		return Optional.ofNullable(getCurrentServerEntry())
			.map(x -> x.address)
			.orElse(serverAddress);
	}

	@Override
	public String br$getServerName() {
		return Optional.ofNullable(getCurrentServerEntry())
			.map(x -> x.name)
			.orElse(null);
	}

	@Override
	public Collection<? extends AxoPlayerListEntry> br$getOnlinePlayers() {
		return player == null ? List.of()
			: Collections.unmodifiableCollection(player.networkHandler.getOnlinePlayers());
	}

	@Override
	public void br$sendToClient(AxoText msg) {
		gui.getChat().addMessage((Text) msg);
	}

	@Override
	public void br$sendToServer(String msg) {
		player.sendChat(msg);
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
	public void execute(@NotNull Runnable command) {
		this.submit(command);
	}

	@Override
	public Object br$getScreen() {
		return screen;
	}

	@Override
	public void br$notifyLevelRenderer() {
		worldRenderer.onViewChanged();
	}

	@Override
	public AxoEntity br$getCameraEntity() {
		return camera;
	}

	@Override
	public List<String> br$getSidebar() {
		List<String> lines = new ArrayList<>();
		Minecraft client = Minecraft.getInstance();
		if (client.world == null)
			return lines;

		Scoreboard scoreboard = client.world.getScoreboard();
		if (scoreboard == null)
			return lines;
		ScoreboardObjective sidebar = scoreboard.getDisplayObjective(1);
		if (sidebar == null)
			return lines;

		Collection<ScoreboardScore> scores = scoreboard.getScores(sidebar);
		List<ScoreboardScore> list = scores.stream().filter(
				input -> input != null && input.getOwner() != null && !input.getOwner().startsWith("#"))
			.collect(Collectors.toList());

		if (list.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
		} else {
			scores = list;
		}

		for (ScoreboardScore score : scores) {
			Team team = scoreboard.getTeamOfMember(score.getOwner());
			if (team == null)
				return lines;
			String text = team.getPrefix() + team.getSuffix();
			if (!text.trim().isEmpty())
				lines.add(text);
		}

		lines.add(sidebar.getDisplayName());
		Collections.reverse(lines);

		return lines;
	}
}
