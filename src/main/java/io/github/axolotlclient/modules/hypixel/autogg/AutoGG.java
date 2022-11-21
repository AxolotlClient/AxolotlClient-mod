/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
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
 */

package io.github.axolotlclient.modules.hypixel.autogg;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on <a href="https://github.com/DragonEggBedrockBreaking/AutoGG/blob/trunk/src/main/java/uk/debb/autogg/mixin/MixinChatHud.java">DragonEggBedrockBreaking's AutoGG Mod</a>
 * @license MPL-2.0
 */


public class AutoGG implements AbstractHypixelMod {

    public static AutoGG Instance = new AutoGG();

    private final OptionCategory category = new OptionCategory("axolotlclient.autogg");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private long lastTime = 0;
    public BooleanOption gg = new BooleanOption("axolotlclient.printGG", false);
    public StringOption ggString = new StringOption("axolotlclient.ggString", "gg");

    public BooleanOption gf = new BooleanOption("axolotlclient.printGF", false);
    public StringOption gfString = new StringOption("axolotlclient.gfString", "gf");

    public BooleanOption glhf = new BooleanOption("axolotlclient.printGLHF", false);
    public StringOption glhfString = new StringOption("axolotlclient.glhfString", "glhf");

    private final BooleanOption onHypixel = new BooleanOption("axolotlclient.onHypixel", false);
    private final BooleanOption onBWP = new BooleanOption("axolotlclient.onBWP", false);
    private final BooleanOption onPVPL = new BooleanOption("axolotlclient.onPVPL", false);
    private final BooleanOption onMMC = new BooleanOption("axolotlclient.onMMC", false);

    private final List<String> hypixelGGStrings = new ArrayList<>();
    private final List<String> hypixelGFStrings = new ArrayList<>();
    private final List<String> hypixelGLHFStrings = new ArrayList<>();

    private final List<String> bedwarsPracticeGGStrings = new ArrayList<>();
    private final List<String> bedwarsPracticeGFStrings = new ArrayList<>();
    private final List<String> bedwarsPracticeGLHFStrings = new ArrayList<>();

    private final List<String> pvpLandGGStrings = new ArrayList<>();
    private final List<String> pvpLandGFStrings = new ArrayList<>();
    private final List<String> pvpLandGLHFStrings = new ArrayList<>();

    private final List<String> minemenGGStrings = new ArrayList<>();
    private final List<String> minemenGFStrings = new ArrayList<>();
    private final List<String> minemenGLHFStrings = new ArrayList<>();

    @Override
    public void init() {
        populateHypixelGGStrings();
        populateHypixelGFStrings();
        populateHypixelGLHFStrings();
        
        populateBedwarsPracticeGGStrings();
        populateBedwarsPracticeGFStrings();
        populateBedwarsPracticeGLHFStrings();
        
        populatePvpLandGGStrings();
        populatePvpLandGFStrings();

        populateMinemenGGStrings();
        populateMinemenGFStrings();
        populateMinemenGLHFStrings();

        category.add(gg);
        category.add(ggString);
        category.add(gf);
        category.add(gfString);
        category.add(glhf);
        category.add(glhfString);
        category.add(onHypixel);
        category.add(onBWP);
        category.add(onPVPL);
        category.add(onMMC);

    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    private void processChat(Text messageReceived, List<String> options, String messageToSend) {
        if (System.currentTimeMillis() - this.lastTime > 3000) {
            for (String s : options) {
                if (messageReceived.asUnformattedString().contains(s)) {
                    Util.sendChatMessage(messageToSend);
                    this.lastTime = System.currentTimeMillis();
                    return;
                }
            }
        }
    }

    private void populateHypixelGGStrings() {
        hypixelGGStrings.add("1st Killer -");
        hypixelGGStrings.add("1st Place -");
        hypixelGGStrings.add("Winner:");
        hypixelGGStrings.add(" - Damage Dealt -");
        hypixelGGStrings.add("Winning Team -");
        hypixelGGStrings.add("1st -");
        hypixelGGStrings.add("Winners:");
        hypixelGGStrings.add("Winner:");
        hypixelGGStrings.add("Winning Team:");
        hypixelGGStrings.add(" won the game!");
        hypixelGGStrings.add("Top Seeker:");
        hypixelGGStrings.add("1st Place:");
        hypixelGGStrings.add("Last team standing!");
        hypixelGGStrings.add("Winner #1 (");
        hypixelGGStrings.add("Top Survivors");
        hypixelGGStrings.add("Winners -");
        hypixelGGStrings.add("Sumo Duel -");
        hypixelGGStrings.add("Most Wool Placed -");
        hypixelGGStrings.add("Your Overall Winstreak:");
    }
    private void populateHypixelGFStrings() {
        hypixelGFStrings.add("SkyWars Experience (Kill)");
        hypixelGFStrings.add("coins! (Final Kill)");
    }
    private void populateHypixelGLHFStrings() {
        hypixelGLHFStrings.add("The game starts in 1 second!");
    }

    private void populateBedwarsPracticeGGStrings() {
        bedwarsPracticeGGStrings.add("Winners -");
        bedwarsPracticeGGStrings.add("Game Won!");
        bedwarsPracticeGGStrings.add("Game Lost!");
        bedwarsPracticeGGStrings.add("The winning team is");
    }

    private void populateBedwarsPracticeGFStrings() {
        bedwarsPracticeGFStrings.add(client.getSession().getUsername() + " FINAL KILL!");
    }

    private void populateBedwarsPracticeGLHFStrings() {
        bedwarsPracticeGLHFStrings.add("Game starting in 1 seconds!");
        bedwarsPracticeGLHFStrings.add("Game has started!");
    }
    
    private void populatePvpLandGGStrings() {
        pvpLandGGStrings.add("The match has ended!");
        pvpLandGGStrings.add("Match Results");
        pvpLandGGStrings.add("Winner:");
        pvpLandGGStrings.add("Loser:");
    }

    private void populatePvpLandGFStrings() {
        pvpLandGFStrings.add("slain by " + client.getSession().getUsername());
    }

    private void populateMinemenGGStrings() {
        minemenGGStrings.add("Match Results");
    }

    private void populateMinemenGFStrings() {
        minemenGFStrings.add("killed by " + client.getSession().getUsername() + "!");
    }

    private void populateMinemenGLHFStrings() {
        minemenGLHFStrings.add("1...");
    }

    public void onMessage(Text message) {
        if(Util.getCurrentServerAddress() != null) {
            if (onHypixel.get() && Util.getCurrentServerAddress().contains("hypixel")) {
                if (gf.get()) {
                    processChat(message, hypixelGFStrings, gfString.get());
                }
                if (gg.get()) {
                    processChat(message, hypixelGGStrings, ggString.get());
                }
                if (glhf.get()) {
                    processChat(message, hypixelGLHFStrings, glhfString.get());
                }
            } else if (onBWP.get() && Util.getCurrentServerAddress().contains("bedwarspractice.club")) {
                if (gf.get()) {
                    processChat(message, bedwarsPracticeGFStrings, gfString.get());
                }
                if (gg.get()) {
                    processChat(message, bedwarsPracticeGGStrings, ggString.get());
                }
                if (glhf.get()) {
                    processChat(message, bedwarsPracticeGLHFStrings, glhfString.get());
                }
            } else if (onPVPL.get() && Util.getCurrentServerAddress().contains("pvp.land")) {
                if (gf.get()) {
                    processChat(message, pvpLandGFStrings, gfString.get());
                }
                if (gg.get()) {
                    processChat(message, pvpLandGGStrings, ggString.get());
                }
                if (glhf.get()) {
                    processChat(message, pvpLandGLHFStrings, glhfString.get());
                }
            } else if (onMMC.get() && Util.getCurrentServerAddress().contains("minemen.club")) {
                if (gf.get()) {
                    if (minemenGFStrings.size() == 0) populateMinemenGFStrings();
                    processChat(message, minemenGFStrings, "gf");
                }
                if (gg.get()) {
                    if (minemenGGStrings.size() == 0) populateMinemenGGStrings();
                    processChat(message, minemenGGStrings, "gg");
                }
                if (glhf.get()) {
                    if (minemenGLHFStrings.size() == 0) populateMinemenGLHFStrings();
                    processChat(message, minemenGLHFStrings, "glhf");
                }

            }
        }
    }
}
