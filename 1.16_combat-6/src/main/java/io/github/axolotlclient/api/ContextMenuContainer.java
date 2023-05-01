package io.github.axolotlclient.api;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public class ContextMenuContainer implements Drawable, Element {

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

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if(menu != null){
			menu.render(matrices, mouseX, mouseY, delta);
		}
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if(menu != null){
			menu.mouseMoved(mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(menu != null){
			menu.mouseClicked(mouseX, mouseY, button);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(menu != null){
			menu.mouseReleased(mouseX, mouseY, button);
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if(menu != null){
			menu.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(menu != null){
			menu.mouseScrolled(mouseX, mouseY, amount);
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(menu != null){
			menu.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(menu != null){
			menu.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if(menu != null){
			menu.charTyped(chr, modifiers);
		}
		return false;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if(menu != null){
			menu.isMouseOver(mouseX, mouseY);
		}
		return false;
	}
}
