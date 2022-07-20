package io.github.axolotlclient.modules.hud.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ScoreboardHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "scoreboardhud");

	public static final ScoreboardObjective placeholder = Util.make(() -> {
		Scoreboard placeScore = new Scoreboard();
		ScoreboardObjective objective = placeScore.addObjective("placeholder", ScoreboardCriterion.DUMMY,
			Text.literal("Cool Players"),
			ScoreboardCriterion.RenderType.INTEGER);
		ScoreboardPlayerScore score = new ScoreboardPlayerScore(placeScore, objective, "DarkKronicle");
		score.setScore(2);
		placeScore.updateScore(score);
		placeScore.updatePlayerScore("DarkKronicle", objective);
		score.setScore(1);
		placeScore.updateScore(score);
		placeScore.updatePlayerScore("Dinnerbone", objective);

		placeScore.setObjectiveSlot(1, objective);
		return objective;
	});

    private final ColorOption backgroundColor = new ColorOption("backgroundcolor", Color.parse("#4C000000"));
    private final ColorOption topColor = new ColorOption("topbackgroundcolor", Color.parse("#66000000"));
    private final BooleanOption scores = new BooleanOption("scores", true);
    private final ColorOption scoreColor = new ColorOption("scorecolor", Color.parse("#FFFF5555"));
    
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    public ScoreboardHud() {
        super(200, 146);
    }

    @Override
	public void render(MatrixStack matrices) {
		matrices.push();
		scale(matrices);
		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective scoreboardObjective = null;
		Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
		if (team != null) {
			int t = team.getColor().getColorIndex();
			if (t >= 0) {
				scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
			}
		}

		ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
		if (scoreboardObjective2 != null) {
			this.renderScoreboardSidebar(matrices, scoreboardObjective2, false);
		}
		matrices.pop();
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		renderScoreboardSidebar(matrices, placeholder, true);
		hovered = false;
		matrices.pop();
	}

	// Abusing this could break some stuff/could allow for unfair advantages. The goal is not to do this, so it won't
	// show any more information than it would have in vanilla.
	private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, boolean placeholder) {
		Scoreboard scoreboard = objective.getScoreboard();
		Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
		List<ScoreboardPlayerScore> filteredScores = scores.stream().filter((testScore) ->
			testScore.getPlayerName() != null && !testScore.getPlayerName().startsWith("#")
		).collect(Collectors.toList());

		if (filteredScores.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
		} else {
			scores = filteredScores;
		}

		List<Pair<ScoreboardPlayerScore, Text>> scoresWText = Lists.newArrayListWithCapacity(scores.size());
		Text text = objective.getDisplayName();
		int displayNameWidth = client.textRenderer.getWidth(text);
		int maxWidth = displayNameWidth;
		int spacerWidth = client.textRenderer.getWidth(": ");

		ScoreboardPlayerScore scoreboardPlayerScore;
		MutableText formattedText;
		for(Iterator<ScoreboardPlayerScore> scoresIterator = scores.iterator(); scoresIterator.hasNext(); maxWidth = Math.max(maxWidth, client.textRenderer.getWidth(formattedText) + spacerWidth + client.textRenderer.getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
			scoreboardPlayerScore = scoresIterator.next();
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			formattedText = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
			scoresWText.add(new Pair<>(scoreboardPlayerScore, formattedText));
		}
		maxWidth = maxWidth + 2;

		if (maxWidth > width) {
			maxWidth = 200;
		}

		int scoresSize = scores.size();
		int scoreHeight = scoresSize * 9;
		DrawPosition pos = getPos();
		Rectangle bounds = getBounds();
		Rectangle inside = new Rectangle(pos.x, pos.y, maxWidth, scoreHeight + 9);
		Rectangle calculated = new Rectangle(bounds.x + bounds.width - inside.width,
			bounds.y + (bounds.height / 2 - inside.height / 2), inside.width, inside.height);
		int scoreY = calculated.y + scoreHeight + 9;
		int scoreX = calculated.x + 2;
		int num = 0;
		int textOffset = scoreX - 2;

		for (Pair<ScoreboardPlayerScore, Text> scoreboardPlayerScoreTextPair : scoresWText) {
			++num;
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboardPlayerScoreTextPair.getLeft();
			Text scoreText = scoreboardPlayerScoreTextPair.getRight();
			String score = "" + scoreboardPlayerScore2.getScore();
			int relativeY = scoreY - num * 9;
			if (background.get()) {
				fillRect(matrices, new Rectangle(textOffset, relativeY, maxWidth, 9), backgroundColor.get());
			}
			if (shadow.get()) {
				client.textRenderer.drawWithShadow(matrices, scoreText, (float) scoreX, (float) relativeY,
					-1);
			} else {
				client.textRenderer.draw(matrices, scoreText, (float) scoreX, (float) relativeY,
					-1);
			}
			if (this.scores.get()) {
				drawString(matrices, client.textRenderer, score,
					(scoreX + maxWidth - client.textRenderer.getWidth(score) - 2), relativeY,
					scoreColor.get().getAsInt(), shadow.get());
			}
			if (num == scoresSize) {
				if (background.get()) {
					fillRect(matrices, new Rectangle(textOffset, relativeY - 10, maxWidth, 9), topColor.get());
					fillRect(matrices, new Rectangle(scoreX - 2, relativeY - 1, maxWidth, 1),
						backgroundColor.get());
				}

				float title = (float) (scoreX + maxWidth / 2 - displayNameWidth / 2 - 1);
				if (shadow.get()) {
					client.textRenderer.drawWithShadow(matrices, text, title, (float) (relativeY - 9), -1);
				}
				else {
					client.textRenderer.draw(matrices, text, title, (float) (relativeY - 9), -1);
				}
			}
		}
		if(outline.get() && !placeholder) {
			outlineRect(matrices, new Rectangle(textOffset,
				calculated.y - 2, maxWidth,
				calculated.height + 2), outlineColor.get());
		}
	}

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(background);
        options.add(topColor);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(shadow);
        options.add(scores);
        options.add(scoreColor);
    }

	@Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

}
