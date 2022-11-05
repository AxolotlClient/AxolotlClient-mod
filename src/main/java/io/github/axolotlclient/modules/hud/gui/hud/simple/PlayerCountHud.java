package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class PlayerCountHud extends SimpleTextHudEntry {

    public static Identifier ID = new Identifier("axolotlclient", "playercounthud");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return client.world.playerEntities.size() + " "+ I18n.translate("axolotlclient.players");
    }
    @Override
    public String getPlaceholder() {
        return 3.141592 + " " + I18n.translate("axolotlclient.players");
    }
}
