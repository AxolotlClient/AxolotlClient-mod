package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class BooleanWidget extends ButtonWidget {

    public final BooleanOption option;

    public BooleanWidget(int x, int y, int width, int height, BooleanOption option) {
        super(x, y, width, height, Text.of(""), buttonWidget -> option.toggle());
        this.active=true;
        this.option=option;
    }

    public Text getMessage(){
        return option.get()? Text.translatable("options."+"on"): Text.translatable ("options."+"off");
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

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = isMouseOver(mouseX, mouseY);

        renderBg(matrices);
        if(!option.getForceDisabled()) {
            renderSwitch(matrices);
        }

        int color = option.get()? 0x55FF55 : 0xFF5555;

        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color);

    }

    private void renderSwitch(MatrixStack matrixStack){
        int x = option.get() ? this.x + width - 8: this.x;
        this.drawTexture(matrixStack, x, this.y, 0, 66 + (hovered ? 20:0), 4, this.height/2);
        this.drawTexture(matrixStack, x, this.y + height/2, 0, 86 - height/2 + (hovered ? 20:0), 4, this.height/2);
        this.drawTexture(matrixStack, x + 4, this.y, 200 - 4, 66 + (hovered ? 20:0), 4, this.height);
        this.drawTexture(matrixStack, x + 4, this.y + height/2, 200 - 4, 86 - height/2 + (hovered ? 20:0), 4, this.height/2);
    }

    private void renderBg(MatrixStack matrixStack){
        this.drawTexture(matrixStack, this.x, this.y, 0, 46, this.width / 2, this.height/2);
        this.drawTexture(matrixStack, this.x, this.y + height/2, 0, 66 - height/2, this.width / 2, this.height/2);
        this.drawTexture(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46, this.width / 2, this.height);
        this.drawTexture(matrixStack, this.x + this.width / 2, this.y + height/2, 200 - this.width / 2, 66 - height/2, this.width / 2, this.height/2);
    }
}
