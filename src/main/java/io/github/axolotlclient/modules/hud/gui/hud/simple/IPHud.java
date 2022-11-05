package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class IPHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "iphud");

    public IPHud() {
        super(115, 13);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return "Singleplayer";
        }
        if (client.getCurrentServerEntry() == null) {
            return "none";
        }
        return client.getCurrentServerEntry().address;
    }

    @Override
    public String getPlaceholder() {
        return "Singleplayer";
    }
}
