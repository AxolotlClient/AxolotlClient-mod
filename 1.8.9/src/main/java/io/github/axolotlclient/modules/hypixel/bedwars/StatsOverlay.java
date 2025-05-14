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

package io.github.axolotlclient.modules.hypixel.bedwars;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.PlayerData.Bedwars.CombinedGameData;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

// TODO: maybe i18n this
public class StatsOverlay extends BoxHudEntry implements DynamicallyPositionable {
	@FunctionalInterface
	private interface EntryRenderer {
		String render(BedwarsTeam team, String name, CombinedGameData data, int winstreak);
	}

	private record Entry(boolean acceptNull, String name, EntryRenderer compRenderer) {
	}

	private static final List<Entry> RENDER_ENTRIES = List.of(
		new Entry(true, "Player", (t, n, bw, ws) -> t.getColorSection() + n),
		new Entry(false, "FKDR", (t, n, bw, ws) -> (Formatting.GOLD + "%.2f (%s/%s)").formatted(bw.fkdr(), bw.finalKills(), bw.finalDeaths())),
		new Entry(false, "KDR", (t, n, bw, ws) -> (Formatting.GOLD + "%.2f (%s/%s)").formatted(bw.kdr(), bw.kills(), bw.deaths())),
		new Entry(false, "WLR", (t, n, bw, ws) -> (Formatting.GOLD + "%.2f (%s/%s)").formatted(bw.wlr(), bw.wins(), bw.losses())),
		new Entry(false, "WS", (t, n, bw, ws) -> Formatting.GOLD.toString() + ws)
	);

	private class RenderHelper {
		private final Map<String, IntObjectPair<CombinedGameData>> stats;
		private final Map<BedwarsTeam, List<String>> playersByTeam;
		private int xCursor = getPos().x + padding.get();
		private int yFinal = 0;

		private RenderHelper(Map<String, IntObjectPair<CombinedGameData>> stats, Map<BedwarsTeam, List<String>> playersByTeam) {
			this.stats = stats;
			this.playersByTeam = playersByTeam;
		}

		private void renderColumn(Entry renderEntry) {
			final var shadow = true;
			final var dy = client.textRenderer.fontHeight + rowMargin.get();

			int currY = getPos().y + padding.get();
			int newXCursor = drawString(renderEntry.name, xCursor, currY, 0xffffffff, shadow);

			currY += dy;

			for (final var entry : playersByTeam.entrySet()) {
				final var team = entry.getKey();
				final var members = entry.getValue();

				for (String playerName : members) {
					final var data = stats.get(playerName);
					final var text = data == null ?
						(renderEntry.acceptNull ? renderEntry.compRenderer.render(team, playerName, null, 0) : Formatting.RED + "?") :
						renderEntry.compRenderer.render(team, playerName, data.right(), data.leftInt());

					newXCursor = Math.max(newXCursor, drawString(text, xCursor, currY, 0xffffffff, shadow));
					currY += dy;
				}
			}

			yFinal = currY;
			xCursor = newXCursor + columnMargin.get();
		}

		private void render() {
			for (final var renderEntry : RENDER_ENTRIES) {
				renderColumn(renderEntry);
			}

			// don't multiply the padding by two, since it's already accounted for by the cursors
			setWidth(xCursor - getPos().x + padding.get() - columnMargin.get());
			setHeight(yFinal - getPos().y + padding.get() - rowMargin.get());
		}
	}

	private static final Map<BedwarsTeam, List<String>> SAMPLE_PLAYERS = Map.of(
		BedwarsTeam.AQUA, List.of("FloweyTF", "Adaklys"),
		BedwarsTeam.GREEN, List.of("herobrine", "steve")
	);

	private static final Map<String, IntObjectPair<CombinedGameData>> SAMPLE_STATS = Map.of(
		"FloweyTF", IntObjectPair.of(3, new CombinedGameData(4234, 5634, 500, 300, 1469, 336, 230, 123)),
		"Adaklys", IntObjectPair.of(3, new CombinedGameData(1984, 2048, 300, 500, 834, 737, 123, 273)),
		"steve", IntObjectPair.of(3, new CombinedGameData(10, 1, 10, 1, 10, 1, 10, 1))
	);

	public final static Identifier ID = new Identifier("axolotlclient", "bedwars_stats_overlay");

	protected final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();
	protected final IntegerOption padding = new IntegerOption("padding", 3, 1, 10);
	protected final IntegerOption columnMargin = new IntegerOption("columnMargin", 3, 0, 10);
	protected final IntegerOption rowMargin = new IntegerOption("rowMargin", 1, 0, 10);

	private final BedwarsMod mod;
	private Map<String, IntObjectPair<CombinedGameData>> stats = new HashMap<>();
	private final Map<BedwarsTeam, List<String>> playersByTeam = new EnumMap<>(BedwarsTeam.class);
	private final KeyBinding toggle = new KeyBinding("bedwars.toggle_stats_overlay", Keyboard.KEY_K, "category.axolotlclient");
	private boolean shouldRender = false;
	@Nullable
	private String errorMessage = null;

	public StatsOverlay(BedwarsMod mod) {
		super(400, 600, true);
		this.mod = mod;
	}

	void onStart() {
		playersByTeam.clear();
		// can't call clear here, since we need a fresh map to avoid requests from writing
		stats = new HashMap<>();
		shouldRender = true;

		if (!API.getInstance().getApiOptions().enabled.get()) {
			errorMessage = "API Not Enabled!";
			return;
		}

		if (!API.getInstance().isAuthenticated()) {
			errorMessage = "API Not Authenticated!";
			return;
		}

		final var api = HypixelAbstractionLayer.getInstance().getPlayerDataApi();

		// need to use capturedStats since this map could've been "retired"
		final var capturedStats = this.stats;
		client.getNetworkHandler().getOnlinePlayers().forEach(playerInfo -> {
			final var uuid = playerInfo.getProfile().getId();
			final var name = playerInfo.getProfile().getName();

			System.out.printf("StatsOverlay: 0 %s %s\n", uuid, name);

			// TODO: maybe merge this into BedwarsGame?
			mod.getGame().flatMap(game -> game.getPlayer(uuid)).ifPresent(bwPlayer -> {
				final var team = playersByTeam.computeIfAbsent(bwPlayer.getTeam(), ignored -> new ArrayList<>());
				team.add(name);
				System.out.printf("StatsOverlay: 1 %s %s\n", name, team);
			});

			// begin resolving players
			api.getAsync(uuid.toString()).whenCompleteAsync((playerData, throwable) -> {
				System.out.printf("StatsOverlay: 2 %s\n", name);

				if (playerData == null || playerData.isEmpty()) {
					return;
				}

				capturedStats.put(name, IntObjectPair.of(
					playerData.get().bedwars().all().winstreak(),
					playerData.get().bedwars().core())
				);
			}, client::submit);
		});
	}

	@Override
	public void init() {
		super.init();
		KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> {
			keyBindingRegistry.register(toggle);
		});
	}

	@Override
	public void render(float delta) {
		if(errorMessage != null) {
			drawString(Formatting.RED + errorMessage, getX(), getY(), 0xffffffff, true);
		}

		if (mod.inGame() && shouldRender) {
			super.render(delta);
		}
	}

	@Override
	public void renderComponent(float delta) {
		new RenderHelper(stats, playersByTeam).render();
	}

	@Override
	public void renderPlaceholderComponent(float delta) {
		new RenderHelper(SAMPLE_STATS, SAMPLE_PLAYERS).render();
	}

	@Override
	public void tick() {
		if (mod.inGame()) {
			if (this.toggle.consumeClick()) {
				shouldRender = !shouldRender;
			}
		}
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public AnchorPoint getAnchor() {
		return anchor.get();
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		final var opts = super.getConfigurationOptions();
		opts.add(anchor);
		opts.add(padding);
		opts.add(columnMargin);
		opts.add(rowMargin);
		return opts;
	}
}
