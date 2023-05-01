package io.github.axolotlclient.api;

import net.minecraft.client.gui.screen.Screen;

public interface ContextMenuScreen {

	void setContextMenu(ContextMenu menu);

	boolean hasContextMenu();

	Screen getParent();
}
