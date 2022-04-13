package io.github.moehreag.axolotlclient.modules.hypixel.autogg;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.modules.hypixel.AbstractHypixelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on https://github.com/DragonEggBedrockBreaking/AutoGG/blob/trunk/src/main/java/uk/debb/autogg/mixin/MixinChatHud.java
 * License: MPL-2.0
 */


public class AutoGG implements AbstractHypixelMod {

    public static AutoGG Instance = new AutoGG();

    private final OptionCategory category = new OptionCategory(new Identifier("autogg"), "autogg");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private long lastTime = 0;
    public BooleanOption gg = new BooleanOption("printGG", false);
    public StringOption ggString = new StringOption("ggString", "gg");

    public BooleanOption gf = new BooleanOption("printGF", false);
    public StringOption gfString = new StringOption("gfString", "gf");

    public BooleanOption glhf = new BooleanOption("printGLHF", false);
    public StringOption glhfString = new StringOption("glhfString", "glhf");

    private final BooleanOption onHypixel = new BooleanOption("onHypixel", false);
    private final BooleanOption onBWP = new BooleanOption("onBWP", false);
    private final BooleanOption onPVPL = new BooleanOption("onPVPL", false);

    private final List<String> hypixelGGStrings = new ArrayList<>();
    private final List<String> hypixelGFStrings = new ArrayList<>();
    private final List<String> hypixelGLHFStrings = new ArrayList<>();

    private final List<String> bedwarsPracticeGGStrings = new ArrayList<>();
    private final List<String> bedwarsPracticeGFStrings = new ArrayList<>();
    private final List<String> bedwarsPracticeGLHFStrings = new ArrayList<>();

    private final List<String> pvpLandGGStrings = new ArrayList<>();
    private final List<String> pvpLandGFStrings = new ArrayList<>();
    private final List<String> pvpLandGLHFStrings = new ArrayList<>();

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

        category.add(gg);
        category.add(ggString);
        category.add(gf);
        category.add(gfString);
        category.add(glhf);
        category.add(glhfString);
        category.add(onHypixel);
        category.add(onBWP);
        category.add(onPVPL);

    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    private void processChat(Text messageReceived, List<String> options, String messageToSend) {
        if (System.currentTimeMillis() - this.lastTime > 3000) {
            for (String s : options) {
                if (messageReceived.getString().contains(s)) {
                    client.player.sendChatMessage(messageToSend);
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

    public void onMessage(Text message) {
        if (onHypixel.get() && client.getCurrentServerEntry().address.contains("hypixel")) {
            if (gf.get()) {
                processChat(message, hypixelGFStrings, gfString.get());
            }
            if (gg.get()) {
                processChat(message, hypixelGGStrings, ggString.get());
            }
            if (glhf.get()) {
                processChat(message, hypixelGLHFStrings, glhfString.get());
            }
        } else if (onBWP.get() && client.getCurrentServerEntry().address.contains("bedwarspractice.club")) {
            if (gf.get()) {
                processChat(message, bedwarsPracticeGFStrings, gfString.get());
            }
            if (gg.get()) {
                processChat(message, bedwarsPracticeGGStrings, ggString.get());
            }
            if (glhf.get()) {
                processChat(message, bedwarsPracticeGLHFStrings, glhfString.get());
            }
        } else if (onPVPL.get() && client.getCurrentServerEntry().address.contains("pvp.land")) {
            if (gf.get()) {
                processChat(message, pvpLandGFStrings, gfString.get());
            }
            if (gg.get()) {
                processChat(message, pvpLandGGStrings, ggString.get());
            }
            if (glhf.get()) {
                processChat(message, pvpLandGLHFStrings, glhfString.get());
            }
        }
    }
}