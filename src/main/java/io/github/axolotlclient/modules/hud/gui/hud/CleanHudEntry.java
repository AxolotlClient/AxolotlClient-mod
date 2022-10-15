package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class CleanHudEntry extends AbstractHudEntry {

    public CleanHudEntry() {
        super(53, 13);
    }

    protected CleanHudEntry(int width, int height) {
        super(width, height);
    }

    @Override
    public void render() {

        scale();
        DrawPosition pos = getPos();
        if (background.get()) {
            fillRect(getBounds(), backgroundColor.get());
        }
        if(outline.get()) outlineRect(getBounds(), outlineColor.get());
        drawString(getValue(),
                pos.x,
                pos.y + (Math.round((float) height / 2) - 4),
                textColor.get(),
                shadow.get());
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        drawString(getPlaceholder(),
                pos.x,
                pos.y + (Math.round((float) height / 2) - 4), Color.WHITE, shadow.get());
        GlStateManager.popMatrix();
        hovered = false;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(textAlignment);
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
