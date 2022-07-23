package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
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

        MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

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
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
    }
}
