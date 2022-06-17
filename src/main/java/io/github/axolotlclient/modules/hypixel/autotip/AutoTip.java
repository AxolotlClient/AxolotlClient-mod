package io.github.axolotlclient.modules.hypixel.autotip;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class AutoTip implements AbstractHypixelMod {

    public static AutoTip INSTANCE = new AutoTip();

    private final OptionCategory category = new OptionCategory(new Identifier("autotip"), "autotip");

    private final BooleanOption enabled = new BooleanOption("enabled", false);
    private long lastTime;
    private boolean init = false;

    @Override
    public void init() {
        category.add(enabled);
        init=true;
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    @Override
    public void tick() {
        if(init) {
            if (System.currentTimeMillis() - lastTime > 1200000 && MinecraftClient.getInstance().getCurrentServerEntry() != null &&
                    MinecraftClient.getInstance().getCurrentServerEntry().address.contains("hypixel") &&
                    enabled.get()) {

                if(MinecraftClient.getInstance().player!=null) {
                    MinecraftClient.getInstance().player.sendChatMessage("/tip all");
                    lastTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public boolean tickable() {
        return true;
    }
}
