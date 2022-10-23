package io.github.axolotlclient.modules.hud.gui.entry;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class BoxHudEntry extends AbstractHudEntry {

    private final boolean backgroundAllowed;

    protected BooleanOption background = new BooleanOption("background", true);
    protected ColorOption backgroundColor = new ColorOption("backgroundcolor", 0x64000000);

    protected BooleanOption outline = new BooleanOption("outline", false);
    protected ColorOption outlineColor = new ColorOption("outlinecolor", Color.WHITE);

    public BoxHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height);
        this.backgroundAllowed = backgroundAllowed;
        if (!backgroundAllowed) {
            background = null;
            backgroundColor = null;
            outline = null;
            outlineColor = null;
        }
    }

    @Override
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        if (backgroundAllowed) {
            options.add(background);
            options.add(backgroundColor);
            options.add(outline);
            options.add(outlineColor);
        }
        return options;
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        if (backgroundAllowed) {
            if (background.get() && backgroundColor.get().getAlpha() > 0) {
                fillRect(matrices, getBounds(), backgroundColor.get());
            }
            if (outline.get() && outlineColor.get().getAlpha() > 0) {
                outlineRect(matrices, getBounds(), outlineColor.get());
            }
        }
        renderComponent(matrices, delta);
        matrices.pop();
    }

    public abstract void renderComponent(MatrixStack matrices, float delta);

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        outlineRect(matrices, getTrueBounds(), Color.BLACK);
        scale(matrices);
        renderPlaceholderComponent(matrices, delta);
        matrices.pop();
        hovered = false;
    }

    public abstract void renderPlaceholderComponent(MatrixStack matrices, float delta);

    @Override
    public boolean movable() {
        return true;
    }
}
