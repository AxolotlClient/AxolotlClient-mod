package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

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
    public void addConfigOptions(List<OptionBase<?>> options) {
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
            clicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }

        public void click() {
            clicks.add(Util.getMeasuringTimeMs());
        }

        public int clicks() {
            return clicks.size();
        }

    }

}
