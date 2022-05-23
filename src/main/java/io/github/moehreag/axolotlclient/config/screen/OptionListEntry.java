package io.github.moehreag.axolotlclient.config.screen;

import net.minecraft.client.gui.widget.EntryListWidget;

public class OptionListEntry implements EntryListWidget.Entry {

    protected boolean hovered;

    @Override
    public void updatePosition(int index, int x, int y) {

    }

    @Override
    public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
        this.hovered=hovered;
    }

    @Override
    public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
        return hovered;
    }

    @Override
    public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {

    }
}
