package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class ColorSelectionWidget extends ButtonWidget {
    private final ColorOption option;

    // Texture based on https://github.com/MartinThoma/LaTeX-examples/blob/master/documents/printer-testpage/printer-testpage.tex
    protected Identifier wheel = new Identifier("axolotlclient", "textures/gui/colorwheel.png");
    protected Rectangle pickerImage;
    protected Rectangle currentRect;

    protected IntegerOption alpha = new IntegerOption("alpha", 0, 0, 255);
    protected OptionSliderWidget alphaSlider;

    protected TextFieldWidget textInput;

    public ColorSelectionWidget(ColorOption option) {
        super(100, 50, 0, 0, Text.empty(), (buttonWidget)->{});
        this.option=option;
        init();
    }

    public void init(){
        Window window= MinecraftClient.getInstance().getWindow();
        width=window.getScaledWidth()-200;
        height=window.getScaledHeight()-100;

        pickerImage = new Rectangle(120, 70, width/2, height/2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 20);

        alpha.set(option.get().getAlpha());

        alphaSlider = new OptionSliderWidget(pickerImage.x, pickerImage.y + pickerImage.height + 20, pickerImage.width, 20, alpha){

            @Override
            protected boolean canHover() {
                return true;
            }

            @Override
            public @NotNull Text getMessage() {
                return getOption().getTranslatedName().append(": ").append(super.getMessage());
            }
        };

        textInput = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
            currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20, Text.empty());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        DrawUtil.fillRect(matrices, new Rectangle(100, 50, width, height), Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(matrices, new Rectangle(100, 50, width, height), Color.BLACK);

        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("pickColor"), MinecraftClient.getInstance().getWindow().getScaledWidth()/2, 54, -1);

        DrawUtil.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("currentColor").append(":") ,currentRect.x, currentRect.y - 10, -1);

        DrawUtil.fillRect(matrices, currentRect, option.get());
        DrawUtil.outlineRect(matrices, currentRect, Color.DARK_GRAY.withAlpha(127));

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, wheel);
        DrawableHelper.drawTexture(matrices, pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(matrices, pickerImage, Color.DARK_GRAY);

        alphaSlider.render(matrices, mouseX, mouseY, delta);

        textInput.render(matrices, mouseX, mouseY, delta);


    }

    public void tick(){
        textInput.tick();

        if(!Objects.equals(textInput.getText(), option.get().toString())){
            if(textInput.isFocused()){
                option.set(Color.parse(textInput.getText()));
            } else {
                textInput.setText(option.get().toString());
            }
        }

        if(option.get().getAlpha() != alphaSlider.getSliderValueAsInt()){
            if(alphaSlider.isHovered() || alphaSlider.dragging){
                option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            } else {
                alpha.set(option.get().getAlpha());
            }
        }
    }

    public void onClick(double mouseX, double mouseY){

        if(pickerImage.isMouseOver(mouseX, mouseY)){
            final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // Helped in the complete confusion:
            // https://github.com/MrCrayfish/MrCrayfishDeviceMod/blob/2a06b20ad8873855885285f3cee6a682e161e24c/src/main/java/com/mrcrayfish/device/util/GLHelper.java#L71

            int scale = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
            GL11.glReadPixels((int) (mouseX * scale),
                (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - mouseY * scale - scale),
                1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

            final int r = pixelBuffer.get(0) & 0xff;
            final int g = pixelBuffer.get(1) & 0xff;
            final int b = pixelBuffer.get(2) & 0xff;
            final Color index = new Color(r, g, b, alpha.get());

            option.set(index);
        } else if (alphaSlider.isMouseOver(mouseX, mouseY)) {
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
        }
        textInput.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return alphaSlider.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(textInput.isFocused()){
            return textInput.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(textInput.isFocused()){
            return textInput.charTyped(chr, modifiers);
        }
        return false;
    }
}
