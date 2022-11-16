package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class iconHud extends BoxHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "iconhud");

    public iconHud() {
        super(15, 15, false);
    }

    @Override
    public void renderComponent(float delta) {
        GlStateManager.color4f(1, 1, 1,1);
        MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

        drawTexture(getX(), getY(), 0, 0, width, height, width, height);

    }

    @Override
    public void renderPlaceholder(float delta) {
        GlStateManager.pushMatrix();
        scale();
        GlStateManager.color4f(1, 1, 1,1);
        renderComponent(delta);
        GlStateManager.popMatrix();
        hovered = false;
    }

    @Override
    public void renderPlaceholderComponent(float delta) {

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
