package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class ComboHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "combohud");

    private long lastTime = 0;
    private int target = -1;
    private int count = 0;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (count == 0) {
            return I18n.translate("combocounter.no_hits");
        }
        if (lastTime + 2000 < Util.getMeasuringTimeMs()) {
            count = 0;
            return "0 hits";
        }
        if (count == 1) {
            return I18n.translate("combocounter.one_hit");
        }
        return I18n.translate("combocounter.hits", count);
    }

    @Override
    public String getPlaceholder() {
        return I18n.translate("combocounter.hits", 3);
    }

    public void onEntityAttack(Entity attacked) {
        target = attacked.getId();
    }

    public void onEntityDamage(Entity entity) {
        if (client.player == null) {
            return;
        }
        if (entity.getId() == client.player.getId()) {
            target = -1;
            count = 0;
            return;
        }
        if (entity.getId() == target) {
            count++;
            lastTime = Util.getMeasuringTimeMs();
        }
    }

}
