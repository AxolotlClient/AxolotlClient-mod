package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

public class CategoryWidget extends ButtonWidget {

    public OptionCategory category;

    public BooleanWidget enabledButton;

    public CategoryWidget(OptionCategory category, int x, int y, int width, int height) {
        super(x, y, width, 20, category.getTranslatedName().copy().append("..."),
	        buttonWidget -> {});
        this.category=category;

        if (AxolotlClient.CONFIG.quickToggles.get()) {
            category.getOptions().forEach(option -> {
                if (option.getName().contains("enabled") && option instanceof BooleanOption) {
                    enabledButton = new BooleanWidget((x + width) - 35, y + 3, 30, height - 6, (BooleanOption) option);
                }
            });
        }

    }

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
            ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }

        if(enabledButton!=null && enabledButton.isMouseOver(mouseX, mouseY)) {
            this.hovered = false;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
	        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height && !(enabledButton!=null && enabledButton.isHoveredOrFocused());
            int i = this.getYImage(this.hovered);
            this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexture(matrices,this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBackground(matrices, MinecraftClient.getInstance(), mouseX, mouseY);
            int j = 14737632;
            if (!this.active) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.getMessage(), this.x + this.width / 2 - (enabledButton!=null?enabledButton.getWidth()/2:0), this.y + (this.height - 8) / 2, j);
        }

        if(enabledButton != null){
            enabledButton.y = y+2;
            enabledButton.render(matrices, mouseX, mouseY, delta);
        }
    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(enabledButton!=null &&
                enabledButton.isHoveredOrFocused()) {
	        playDownSound(MinecraftClient.getInstance().getSoundManager());
            enabledButton.option.toggle();
			return false;
        } else {
            MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, category));
        }
		return super.mouseClicked(mouseX, mouseY, button);
    }


}
