package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CPSHud extends SimpleTextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "cpshud");

    private final BooleanOption fromKeybindings = new BooleanOption("cpskeybind", ID.getPath(), false);
    private final BooleanOption rmb = new BooleanOption("rightcps", ID.getPath(), false);

    public CPSHud() {
        super();
        Hooks.MOUSE_INPUT.register(button -> {
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
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        options.add(fromKeybindings);
        options.add(rmb);
        return options;
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
