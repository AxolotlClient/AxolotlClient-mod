package io.github.moehreag.axolotlclient.modules.hud.gui.hud;


import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;

import java.util.List;

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
            //fillRect(getBounds(), backgroundColor.getColor());
            fillRect(getBounds(), new Color(-1));
        }
        drawCenteredString(client.textRenderer, getValue(), new DrawPosition(pos.x + (Math.round(width) / 2),
                pos.y + (Math.round((float) height / 2)) - 4), -1, shadow.get());
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        drawCenteredString(client.textRenderer, getPlaceholder(),
                new DrawPosition(pos.x + (Math.round(width) / 2),
                pos.y + (Math.round((float) height / 2)) - 4), -1, shadow.get());
        hovered = false;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        //options.add(textColor);
        options.add(shadow);
        options.add(background);
        //options.add(backgroundColor);
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

}
