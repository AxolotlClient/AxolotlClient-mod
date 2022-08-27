package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.StringOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class StringOptionWidget extends TextFieldWidget {

	public final StringOption option;

    public TextFieldWidget textField;

    public StringOptionWidget(int x, int y, StringOption option){
        super(MinecraftClient.getInstance().textRenderer, x, y, 150, 40, new LiteralText(option.get()));
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, Text.of("")){
	        @Override
	        public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOver(mouseX, mouseY)) {
                    return super.mouseClicked(mouseX, mouseY, button);
                } else {
                    this.setFocused(false);
					return false;
                }
            }

	        @Override
	        public boolean charTyped(char chr, int modifiers) {
		        boolean bool = super.charTyped(chr, modifiers);
		        option.set(textField.getText());
				return bool;
	        }
        };
        this.option=option;
        textField.setText(option.get());
        textField.setVisible(true);
        textField.setEditable(true);
        textField.setMaxLength(512);
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(textField.isFocused()) {
            this.textField.keyPressed(keyCode, scanCode, modifiers);
            this.option.set(textField.getText());
            return true;//super.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return textField.charTyped(chr, modifiers);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        textField.y = y;
        textField.x = x;
        textField.render(matrices, mouseX, mouseY, delta);
    }

	@Override
	public void tick() {
		if(textField.isFocused()) {
			textField.tick();
		}
	}

    @Override
    public boolean changeFocus(boolean bl) {
        return textField.changeFocus(bl) || super.changeFocus(bl);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(canHover()) {
            return super.isMouseOver(mouseX, mouseY);
        }
        return false;
    }

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return true;
    }
}
