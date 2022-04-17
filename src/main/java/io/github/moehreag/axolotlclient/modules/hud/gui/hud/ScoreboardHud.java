package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

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

    private final ColorOption backgroundColor = new ColorOption("backgroundcolor", Color.parse("#4C000000"));
    private final ColorOption topColor = new ColorOption("topbackgroundcolor", Color.parse("#66000000"));
    private final BooleanOption scores = new BooleanOption("scores", true);
    private final ColorOption scoreColor = new ColorOption("scorecolor", Color.parse("#FFFF5555"));
    
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    public ScoreboardHud() {
        super(200, 146);
    }

    @Override
    public void render() {
        scale();

        Scoreboard scoreboard = this.client.world.getScoreboard();
        ScoreboardObjective scoreboardObjective = null;
        Team team = scoreboard.getPlayerTeam(this.client.player.getEntity().getCustomName());
        if (team != null) {
            int t = team.getFormatting().getColorIndex();
            if (t >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
            }
        }

        ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
        if (scoreboardObjective2 != null) {
            this.renderScoreboardSidebar(scoreboardObjective2);
        }
        GlStateManager.popMatrix();

    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        GlStateManager.popMatrix();
        hovered=false;
    }

    // Abusing this could break some stuff/could allow for unfair advantages. The goal is not to do this, so it won't
    // show any more information than it would have in vanilla.
    private void renderScoreboardSidebar(ScoreboardObjective objective){

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

        List<Pair<ScoreboardPlayerScore, String>> scoresWText = Lists.newArrayListWithCapacity(scores.size());
        String text = objective.getDisplayName();
        int displayNameWidth = client.textRenderer.getStringWidth(text);
        int maxWidth = displayNameWidth;
        int spacerWidth = client.textRenderer.getStringWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        String formattedText;
        for(Iterator<ScoreboardPlayerScore> scoresIterator = scores.iterator(); scoresIterator.hasNext();
            maxWidth = Math.max(maxWidth,
                    client.textRenderer.getStringWidth(formattedText) + spacerWidth + ( this.scores.get()?
                            client.textRenderer.getStringWidth(Integer.toString(scoreboardPlayerScore.getScore())):0))) {
            scoreboardPlayerScore = scoresIterator.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            formattedText = Team.decorateName(team, scoreboardPlayerScore.getPlayerName());
            scoresWText.add(new Pair<>(scoreboardPlayerScore, formattedText));
        }
        maxWidth++;

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

        for (Pair<ScoreboardPlayerScore, String> scoreboardPlayerScoreTextPair : scoresWText) {
            ++num;
            ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboardPlayerScoreTextPair.getLeft();
            String scoreText = scoreboardPlayerScoreTextPair.getRight();
            String score = "" + scoreboardPlayerScore2.getScore();
            int relativeY = scoreY - num * 9;
            if (background.get()) {
                fillRect(new Rectangle(textOffset, relativeY, maxWidth, 9), backgroundColor.get());
            }
            if (shadow.get()) {
                client.textRenderer.drawWithShadow(scoreText, scoreX, relativeY,
                        -1);
            } else {
                client.textRenderer.draw(scoreText, scoreX, relativeY,
                        -1);
            }
            if (this.scores.get()) {
                drawString(client.textRenderer, score,
                        (scoreX + maxWidth - client.textRenderer.getStringWidth(score) - 2), relativeY,
                        scoreColor.get().getAsInt(), shadow.get());
            }
            if (num == scoresSize) {
                if (background.get()) {
                    fillRect(new Rectangle(textOffset, relativeY - 10, maxWidth, 9), topColor.get());
                    fillRect(new Rectangle(scoreX - 2, relativeY - 1, maxWidth, 1),
                            backgroundColor.get());
                }
                int title = (scoreX + maxWidth / 2 - displayNameWidth / 2 - 1);
                if (shadow.get()) {
                    client.textRenderer.drawWithShadow(text, title, (relativeY - 9), -1);
                }
                else {
                    client.textRenderer.draw(text, title, (relativeY - 9), -1);
                }
            }
        }

        /*DrawPosition pos = getPos();

        Scoreboard scoreboard = objective.getScoreboard();

        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        ArrayList<ScoreboardPlayerScore> list = collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#")).collect(Collectors.toCollection(Lists::newArrayList));
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        }

        int i = MinecraftClient.getInstance().textRenderer.getStringWidth(objective.getDisplayName());

        String Object;
        for (Iterator<ScoreboardPlayerScore> iterator = collection.iterator(); iterator.hasNext(); i = Math.max(i, MinecraftClient.getInstance().textRenderer.getStringWidth(Object))) {
            ScoreboardPlayerScore scoreboardPlayerScore = iterator.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            Object = Team.decorateName(team, scoreboardPlayerScore.getPlayerName()) + ": " + Formatting.RED + scoreboardPlayerScore.getScore();
        }

        int j = collection.size() * client.textRenderer.fontHeight;
        int k = pos.y;
        int l = 3;
        int m = pos.x;
        int n = 0;

        for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
            ++n;
            Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
            String name = Team.decorateName(team2, scoreboardPlayerScore2.getPlayerName());
            String score = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
            int p = k - n * client.textRenderer.fontHeight;
            int q = pos.x+i+5;//window.getWidth() - l + 2;
            fill(m - 2, p, q, p + client.textRenderer.fontHeight, 1342177280);
            client.textRenderer.draw(name, m, p, 553648127);
            client.textRenderer.draw(score, q - client.textRenderer.getStringWidth(score), p, 553648127);
            if (n == collection.size()) {
                String objectiveTitle = objective.getDisplayName();
                fill(m - 2, p - client.textRenderer.fontHeight - 1, q, p - 1, 1610612736);
                fill(m - 2, p - 1, q, p, 1342177280);
                client.textRenderer.draw(objectiveTitle, m + i / 2 - client.textRenderer.getStringWidth(objectiveTitle) / 2, p - client.textRenderer.fontHeight, 553648127);
            }

        }*/

    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(background);
        options.add(topColor);
        options.add(backgroundColor);
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
