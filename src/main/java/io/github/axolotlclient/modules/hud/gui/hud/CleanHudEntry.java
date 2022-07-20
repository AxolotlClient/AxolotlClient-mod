package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public abstract class CleanHudEntry extends AbstractHudEntry {

    public CleanHudEntry() {
        super(53, 13);
    }

    protected CleanHudEntry(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(MatrixStack matrices) {

        scale(matrices);
        DrawPosition pos = getPos();
        if (background.get()) {
            fillRect(matrices, getBounds(), backgroundColor.get());
        }
        if(outline.get()) outlineRect(matrices, getBounds(), outlineColor.get());
        drawCenteredString(matrices, client.textRenderer, getValue(),
                new DrawPosition(pos.x + (Math.round(width) / 2),
                pos.y + (Math.round((float) height / 2)) - 4),
                chroma.get()? textColor.getChroma() : textColor.get(),
                shadow.get());
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        drawCenteredString(matrices, client.textRenderer, getPlaceholder(),
                new DrawPosition(pos.x + (width / 2),
                pos.y + (height / 2) - 4), -1, shadow.get());
        matrices.pop();
        hovered = false;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(chroma);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

}
