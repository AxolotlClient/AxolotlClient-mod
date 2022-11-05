package io.github.axolotlclient.modules.hud.gui.entry;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class BoxHudEntry extends AbstractHudEntry {

    private final boolean backgroundAllowed;

    protected BooleanOption background = new BooleanOption("axolotlclient.background", true);
    protected ColorOption backgroundColor = new ColorOption("axolotlclient.bgcolor", 0x64000000);

    protected BooleanOption outline = new BooleanOption("axolotlclient.outline", false);
    protected ColorOption outlineColor = new ColorOption("axolotlclient.outlinecolor", Color.WHITE);

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
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        if (backgroundAllowed) {
            options.add(background);
            options.add(backgroundColor);
            options.add(outline);
            options.add(outlineColor);
        }
        return options;
    }

    @Override
    public void render(float delta) {
        GlStateManager.pushMatrix();
        scale();
        if (backgroundAllowed) {
            if (background.get() && backgroundColor.get().getAlpha() > 0) {
                fillRect(getBounds(), backgroundColor.get());
            }
            if (outline.get() && outlineColor.get().getAlpha() > 0) {
                outlineRect(getBounds(), outlineColor.get());
            }
        }
        renderComponent(delta);
        GlStateManager.popMatrix();
    }

    public abstract void renderComponent(float delta);

    @Override
    public void renderPlaceholder(float delta) {
        GlStateManager.pushMatrix();
        renderPlaceholderBackground();
        outlineRect(getTrueBounds(), Color.BLACK);
        scale();
        GlStateManager.enableTexture();
        renderPlaceholderComponent(delta);
        GlStateManager.popMatrix();
        hovered = false;
    }

    public abstract void renderPlaceholderComponent(float delta);

    @Override
    public boolean movable() {
        return true;
    }
}
