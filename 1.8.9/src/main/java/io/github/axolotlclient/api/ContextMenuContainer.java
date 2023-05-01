package io.github.axolotlclient.api;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import org.jetbrains.annotations.Nullable;

public class ContextMenuContainer extends DrawableHelper {

	@Getter @Setter @Nullable
	private ContextMenu menu;

	public ContextMenuContainer(){

	}

	public void removeMenu(){
		menu = null;
	}

	public boolean hasMenu(){
		return menu != null;
	}

	public void render(MinecraftClient client, int mouseX, int mouseY) {
		if(menu != null){
			menu.render(client, mouseX, mouseY);
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(menu != null){
			return menu.mouseClicked(mouseX, mouseY, button);
		}
		return false;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		if(menu != null){
			return menu.isMouseOver(mouseX, mouseY);
		}
		return false;
	}
}
