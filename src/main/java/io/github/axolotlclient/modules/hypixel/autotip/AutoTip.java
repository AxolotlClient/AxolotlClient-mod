package io.github.axolotlclient.modules.hypixel.autotip;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;

public class AutoTip implements AbstractHypixelMod {

    public static AutoTip INSTANCE = new AutoTip();

    private final OptionCategory category = new OptionCategory("axolotlclient.autotip");

    private final BooleanOption enabled = new BooleanOption("axolotlclient.enabled", false);
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
            if (System.currentTimeMillis() - lastTime > 1200000 && Util.getCurrentServerAddress() != null &&
                    Util.currentServerAddressContains("hypixel") &&
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
