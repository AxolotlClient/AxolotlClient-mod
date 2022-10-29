package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class iconHud extends BoxHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "iconhud");

    public iconHud() {
        super(15, 15, false);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);

        drawTexture(matrices, pos.x, pos.y, 0, 0, width, height, width, height);
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        render(matrices, delta);
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
