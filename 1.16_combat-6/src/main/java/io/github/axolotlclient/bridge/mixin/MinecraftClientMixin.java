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
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.AxoPlayerListEntry;
import io.github.axolotlclient.bridge.AxoSession;
import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.AxoGameOptions;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.resource.AxoResourceManager;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.bridge.world.AxoWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements AxoMinecraftClient {
	@Final
	@Shadow
	public TextRenderer textRenderer;

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Final
	@Shadow
	public GameOptions options;

	@Shadow
	@Final
	private Session session;

	@Shadow
	public abstract boolean isInSingleplayer();

	@Shadow
	@Nullable
	public abstract ServerInfo getCurrentServerEntry();

	@Shadow
	@Final
	public InGameHud inGameHud;

	@Shadow
	@Nullable
	public Screen currentScreen;

	@Shadow
	public abstract ResourceManager getResourceManager();

	@Shadow
	@Final
	public WorldRenderer worldRenderer;

	@Shadow
	@Nullable
	public Entity cameraEntity;

	public MinecraftClientMixin(String string) {
		super(string);
	}

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
		return Optional.ofNullable(getCurrentServerEntry()).map(x -> x.address).orElse(null);
	}

	@Override
	public String br$getServerName() {
		return Optional.ofNullable(getCurrentServerEntry()).map(x -> x.name).orElse(null);
	}

	@Override
	public Collection<? extends AxoPlayerListEntry> br$getOnlinePlayers() {
		return player.networkHandler.getPlayerList();
	}

	@Override
	public void br$sendToClient(AxoText msg) {
		inGameHud.getChatHud().addMessage((Text) msg);
	}

	@Override
	public void br$sendToServer(String msg) {
		player.sendChatMessage(msg);
	}

	@Override
	public void br$reinitScreen() {
		if (currentScreen != null) {
			currentScreen.init((MinecraftClient) (Object) this, currentScreen.width, currentScreen.height);
		}
	}

	@Override
	public AxoResourceManager br$getResourceManager() {
		return getResourceManager();
	}

	@Override
	public Object br$getScreen() {
		return currentScreen;
	}

	@Override
	public void br$notifyLevelRenderer() {
		worldRenderer.scheduleTerrainUpdate();
	}

	@Override
	public AxoEntity br$getCameraEntity() {
		return cameraEntity;
	}

	@Override
	public List<String> br$getSidebar() {
		List<String> lines = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null)
			return lines;

		Scoreboard scoreboard = client.world.getScoreboard();
		if (scoreboard == null)
			return lines;
		ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
		if (sidebar == null)
			return lines;

		Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
		List<ScoreboardPlayerScore> list = scores.stream().filter(
				input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());

		if (list.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
		} else {
			scores = list;
		}

		for (ScoreboardPlayerScore score : scores) {
			Team team = scoreboard.getPlayerTeam(score.getPlayerName());
			if (team == null)
				return lines;
			String text = team.getPrefix().getString() + team.getSuffix().getString();
			if (!text.trim().isEmpty())
				lines.add(text);
		}

		lines.add(sidebar.getDisplayName().getString());
		Collections.reverse(lines);

		return lines;
	}
}
