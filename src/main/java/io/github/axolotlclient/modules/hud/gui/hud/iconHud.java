package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class iconHud extends AbstractHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "iconhud");

    public iconHud() {
        super(15, 15);
    }

    @Override
    public void render(MatrixStack matrices) {
        scale(matrices);
        DrawPosition pos = getPos();

        RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);

        drawTexture(matrices, pos.x, pos.y, 0, 0, width, height, width, height);

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        render(matrices);
        hovered = false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
    }
}
