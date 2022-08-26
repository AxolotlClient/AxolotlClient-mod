package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ColorOptionWidget extends ButtonWidget {

    private final ColorOption option;

    public final TextFieldWidget textField;
    private final ButtonWidget openPicker;

    protected Identifier pipette = new Identifier("axolotlclient", "textures/gui/pipette.png");

    public ColorOptionWidget(int x, int y, ColorOption option) {
        super(x, y, 150, 20, Text.of(""), buttonWidget -> {});
        this.option=option;
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, 128, 19, getMessage());
        textField.write(option.get().toString());

        openPicker = new ButtonWidget(x+128, y, 21, 21, Text.of(""), buttonWidget -> {}){
	        @Override
	        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                DrawUtil.fill(matrices, x, y, x+width, y+height, option.get().getAsInt());
                DrawUtil.outlineRect(matrices, new Rectangle(x, y, width, height), new Color(-6250336));

                RenderSystem.setShaderTexture(0, pipette);
                drawTexture(matrices, x, y, 0, 0, 20, 20, 21, 21);
            }
        };
    }

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        textField.y = y;
        textField.x = x;
        textField.render(matrices, mouseX, mouseY, delta);

        openPicker.y = y-1;
        openPicker.x = x+128;
        openPicker.render(matrices, mouseX, mouseY, delta);

    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(openPicker.isMouseOver(mouseX, mouseY)){
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).openColorPicker(option);
            }
            return true;
        } else if(textField.isMouseOver(mouseX, mouseY)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return textField.mouseClicked(mouseX, mouseY, 0);

        } else {
			textField.setTextFieldFocused(false);
	        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
		        ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).closeColorPicker();
	        }
        }
		return super.mouseClicked(mouseX, mouseY, button);
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

    public void tick(){
        if(textField.isFocused()) {
            textField.tick();
        } else {
            if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                    ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen() &&
                    !Objects.equals(textField.getText(), option.get().toString())){
                textField.setText(option.get().toString());
            }
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if(textField.isFocused()) {
            textField.keyPressed(keyCode, scanCode, modifiers);
            option.set(Color.parse(textField.getText()));
			return true;
        }
	    return false;
    }

	@Override
	public boolean charTyped(char c, int modifiers) {
		if(textField.isFocused()) {
			textField.charTyped(c, modifiers);
			option.set(Color.parse(textField.getText()));
			return true;
		}
		return false;
	}
}
