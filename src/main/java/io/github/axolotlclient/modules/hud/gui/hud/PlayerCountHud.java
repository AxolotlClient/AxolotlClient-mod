package io.github.axolotlclient.modules.hud.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class PlayerCountHud extends CleanHudEntry {
    public static Identifier ID = new Identifier("axolotlclient", "playercounthud");

    public PlayerCountHud() {
        super(75, 13);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return MinecraftClient.getInstance().world.playerEntities.size() + " "+ I18n.translate("players");
    }

    @Override
    public String getPlaceholder() {
        return 3.141592 + " " + I18n.translate("players");
    }
}
