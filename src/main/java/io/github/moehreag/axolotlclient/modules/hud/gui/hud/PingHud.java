package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class PingHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "pinghud");

    public PingHud() {
        // super(x, y, scale);
        super();
    }

    @Override
    public String getValue() {
        PlayerListEntry entry = client.player.networkHandler.getPlayerListEntry(client.player.getUuid());
        if (entry != null) {
            return entry.getLatency() + " ms";
        }
        return "0 ms";
    }

    @Override
    public String getPlaceholder() {
        return "68 ms";
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
