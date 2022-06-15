package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class StringOptionWidget extends TextFieldWidget {

	public final StringOption option;
	/*public StringOptionWidget(int x, int y, int width, StringOption option) {
		super(MinecraftClient.getInstance().textRenderer, x, y, width, 20, Text.of(option.get()));
		this.option=option;
	}*/

    public TextFieldWidget textField;

    public StringOptionWidget(int x, int y, StringOption option){
        super(MinecraftClient.getInstance().textRenderer, x, y, 150, 40, Text.literal(option.get()));
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, Text.empty()){
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
		this.textField.keyPressed(keyCode, scanCode, modifiers);
		this.option.set(textField.getText());
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return textField.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        //GlStateManager.disableDepthTest();
        //MinecraftClient.getInstance().textRenderer.draw(I18n.translate(option.getName()), x, y, -1);
        textField.y = y;
        textField.x = x;
        textField.render(matrices, mouseX, mouseY, delta);
        //GlStateManager.enableDepthTest();
    }

	@Override
	public void tick() {
		if(textField.isFocused()) {
			textField.tick();
		}
	}

	@Override
	public void setTextFieldFocused(boolean focused) {
		textField.setTextFieldFocused(focused);
		super.setTextFieldFocused(focused);
	}
}
