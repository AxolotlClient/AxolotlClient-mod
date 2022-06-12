package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class iconHud extends AbstractHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "iconhud");
    private final ColorOption color = new ColorOption("color", new Color(255, 255, 255, 0));

    public iconHud() {
        super(15, 15);
    }

    @Override
    public void render(MatrixStack matrices) {
        scale(matrices);
        DrawPosition pos = getPos();
        RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);
        if(chroma.get())RenderSystem.setShaderColor(textColor.getChroma().getRed(), textColor.getChroma().getGreen(), textColor.getChroma().getBlue(), 1F);
        else {RenderSystem.setShaderColor(color.get().getRed(), color.get().getGreen(), color.get().getBlue(), 1F);}
        drawTexture(matrices, pos.x, pos.y, 0, 0, width, height, width, height);

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        scale(matrices);
        DrawPosition pos = getPos();
        RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawTexture(matrices, pos.x, pos.y, 0, 0, width, height, width, height);
        matrices.pop();
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
        options.add(color);
        options.add(chroma);
    }
}
