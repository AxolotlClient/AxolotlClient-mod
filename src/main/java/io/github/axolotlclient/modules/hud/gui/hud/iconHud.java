package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class iconHud extends BoxHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "iconhud");

    public iconHud() {
        super(15, 15, false);
    }

    @Override
    public void renderComponent(float delta) {
        DrawPosition pos = getPos();

        MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

        drawTexture(pos.x, pos.y, 0, 0, width, height, width, height);

    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        render(delta);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }
}
