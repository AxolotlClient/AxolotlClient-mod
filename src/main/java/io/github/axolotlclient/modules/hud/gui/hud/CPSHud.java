package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class CPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "cpshud");

    private final BooleanOption fromKeybindings = new BooleanOption("cpskeybind", false);
    private final BooleanOption rmb = new BooleanOption("rightcps", false);

    boolean rc;
    boolean lc;

    public CPSHud() {
        super();
        Hooks.MOUSE_INPUT.register((window, button, action, mods) -> {
            if (!fromKeybindings.get()) {
                if (button == 0) {
                    ClickList.LEFT.click();
                } else if (button == 1) {
                    ClickList.RIGHT.click();
                }
            }
        });
        Hooks.KEYBIND_PRESS.register((key) -> {
            if (fromKeybindings.get()) {
                if (key.equals(client.options.keyAttack)) {
                    ClickList.LEFT.click();
                } else if (key.equals(client.options.keyUse)) {
                    ClickList.RIGHT.click();
                }
            }
        });
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        ClickList.LEFT.update();
        ClickList.RIGHT.update();
    }

    public void click(){
        int button = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if(button==0){
                ClickList.LEFT.click();
            }
            if(button==1)ClickList.RIGHT.click();
        }
    }

    @Override
    public String getValue() {
        if (rmb.get()) {
            return ClickList.LEFT.clicks() + " | " + ClickList.RIGHT.clicks() + " CPS";
        } else {
            return ClickList.LEFT.clicks() + " CPS";
        }
    }

    @Override
    public String getPlaceholder() {
        if (rmb.get()) {
            return "0 | 0 CPS";
        } else {
            return "0 CPS";
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(fromKeybindings);
        options.add(rmb);
    }

    public static class ClickList {

        public static ClickList LEFT = new ClickList();
        public static ClickList RIGHT = new ClickList();
        private final List<Long> clicks;

        public ClickList() {
            clicks = new ArrayList<>();
        }

        public void update() {
            clicks.removeIf((click) -> MinecraftClient.getTime() - click > 1000);
        }

        public void click() {
            clicks.add(MinecraftClient.getTime());
        }

        public int clicks() {
            return clicks.size();
        }

    }

}
