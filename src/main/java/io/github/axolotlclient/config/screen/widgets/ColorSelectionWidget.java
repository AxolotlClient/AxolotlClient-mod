package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class ColorSelectionWidget extends ButtonWidget {
    private final ColorOption option;

    protected Rectangle picker;

    // Texture based on https://github.com/MartinThoma/LaTeX-examples/blob/master/documents/printer-testpage/printer-testpage.tex
    protected Identifier wheel = new Identifier("axolotlclient", "textures/gui/colorwheel.png");
    protected Rectangle pickerImage;
    protected Rectangle currentRect;
    protected Rectangle pickerOutline;

    protected BooleanOption chroma = new BooleanOption("chroma", false);
    protected BooleanWidget chromaWidget;

    protected IntegerOption alpha = new IntegerOption("alpha", 0, 0, 255);
    protected OptionSliderWidget alphaSlider;

    protected IntegerOption red = new IntegerOption("red", 0, 0, 255);
    protected OptionSliderWidget redSlider;

    protected IntegerOption green = new IntegerOption("green", 0, 0, 255);
    protected OptionSliderWidget greenSlider;

    protected IntegerOption blue = new IntegerOption("blue", 0, 0, 255);
    protected OptionSliderWidget blueSlider;

    protected boolean slidersVisible;

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

        picker = new Rectangle(100, 50, width, height);

        pickerImage = new Rectangle(120, 70, width/2, height/2);
        pickerOutline = new Rectangle(pickerImage.x-1, pickerImage.y-1, pickerImage.width+2, pickerImage.height+2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 20);

        chroma.set(option.getChroma());

        alpha.set(option.get().getAlpha());

        slidersVisible = height>175;

        if(slidersVisible) {
            red.set(option.get().getRed());
            green.set(option.get().getGreen());
            blue.set(option.get().getBlue());
        }

        chromaWidget = new BooleanWidget(currentRect.x, currentRect.y + currentRect.height + 40, currentRect.width, 20, chroma){
            @Override
            protected boolean canHover() {
                return true;
            }

            @Override
            public Text getMessage() {
                return option.getTranslatedName().append(": ").append(super.getMessage());
            }
        };

        alphaSlider = new OptionSliderWidget(pickerImage.x, pickerImage.y + pickerImage.height + 20, pickerImage.width, 20, alpha){

            @Override
            protected boolean canHover() {
                return true;
            }

            @Override
            public Text getMessage() {
                return getOption().getTranslatedName().append(": ").append(super.getMessage());
            }
        };

        if(slidersVisible) {

            redSlider = new OptionSliderWidget(currentRect.x, currentRect.y + currentRect.height + 65, currentRect.width, 20, red) {

                @Override
                protected boolean canHover() {
                    return true;
                }

                @Override
                public Text getMessage() {
                    return getOption().getTranslatedName().append(": ").append(super.getMessage());
                }
            };
            greenSlider = new OptionSliderWidget(currentRect.x, currentRect.y + currentRect.height + 90, currentRect.width, 20, green) {

                @Override
                protected boolean canHover() {
                    return true;
                }

                @Override
                public Text getMessage() {
                    return getOption().getTranslatedName().append(": ").append(super.getMessage());
                }
            };
            blueSlider = new OptionSliderWidget(currentRect.x, currentRect.y + currentRect.height + 115, currentRect.width, 20, blue) {

                @Override
                protected boolean canHover() {
                    return true;
                }

                @Override
                public Text getMessage() {
                    return getOption().getTranslatedName().append(": ").append(super.getMessage());
                }
            };
        }

        textInput = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
            currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20, Text.empty());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        DrawUtil.fillRect(matrices, picker, Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(matrices, picker, Color.BLACK);

        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("pickColor"), MinecraftClient.getInstance().getWindow().getScaledWidth()/2, 54, -1);

        DrawUtil.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable("currentColor").append(":") ,currentRect.x, currentRect.y - 10, -1);

        DrawUtil.fillRect(matrices, currentRect, option.get());
        DrawUtil.outlineRect(matrices, currentRect, Color.DARK_GRAY.withAlpha(127));

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, wheel);
        DrawableHelper.drawTexture(matrices, pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(matrices, pickerOutline, Color.DARK_GRAY);

        chromaWidget.render(matrices, mouseX, mouseY, delta);

        alphaSlider.render(matrices, mouseX, mouseY, delta);

        if(slidersVisible){
            redSlider.render(matrices, mouseX, mouseY, delta);
            greenSlider.render(matrices, mouseX, mouseY, delta);
            blueSlider.render(matrices, mouseX, mouseY, delta);
        }

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
            if(alphaSlider.isHoveredOrFocused() || alphaSlider.dragging){
                option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
            } else {
                alpha.set(option.get().getAlpha());
                alphaSlider.update();
            }
        }

        if(slidersVisible) {
            if (option.get().getRed() != redSlider.getSliderValueAsInt()) {
                if (redSlider.isHoveredOrFocused() || redSlider.dragging) {
                    option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    red.set(option.get().getRed());
                    redSlider.update();
                }
            }

            if (option.get().getGreen() != greenSlider.getSliderValueAsInt()) {
                if (greenSlider.isHoveredOrFocused() || greenSlider.dragging) {
                    option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
                } else {
                    green.set(option.get().getGreen());
                    greenSlider.update();
                }
            }

            if (option.get().getBlue() != blueSlider.getSliderValueAsInt()) {
                if (blueSlider.isHoveredOrFocused() || blueSlider.dragging) {
                    option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
                } else {
                    blue.set(option.get().getBlue());
                    blueSlider.update();
                }
            }
        }
    }

    public void onClick(double mouseX, double mouseY){

        if(pickerImage.isMouseOver(mouseX, mouseY)){
            final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // Helped in the complete confusion:
            // https://github.com/MrCrayfish/MrCrayfishDeviceMod/blob/2a06b20ad8873855885285f3cee6a682e161e24c/src/main/java/com/mrcrayfish/device/util/GLHelper.java#L71

            DrawPosition pos = Util.toGlCoords((int) mouseX, (int) mouseY);
            GL11.glReadPixels(pos.x, pos.y,1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

            final int r = pixelBuffer.get(0) & 0xff;
            final int g = pixelBuffer.get(1) & 0xff;
            final int b = pixelBuffer.get(2) & 0xff;
            final Color index = new Color(r, g, b, alpha.get());

            option.set(index);

            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
        } else if (chromaWidget.isMouseOver(mouseX, mouseY)) {
            chromaWidget.mouseClicked(mouseX, mouseY, 0);
            option.setChroma(chroma.get());

        } else if (alphaSlider.isMouseOver(mouseX, mouseY)) {
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
        }
        if(slidersVisible) {
            if (redSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(red.get(), option.get().getGreen(), option.get().getBlue(), option.get().getAlpha()));
            } else if (greenSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), green.get(), option.get().getBlue(), option.get().getAlpha()));
            } else if (blueSlider.isMouseOver(mouseX, mouseY)) {
                option.set(new Color(option.get().getRed(), option.get().getGreen(), blue.get(), option.get().getAlpha()));
            }
        }
        textInput.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(slidersVisible) {
            redSlider.mouseReleased(mouseX, mouseY, button);
            greenSlider.mouseReleased(mouseX, mouseY, button);
            blueSlider.mouseReleased(mouseX, mouseY, button);
        }
        return alphaSlider.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(textInput.isFocused()){
            boolean b = textInput.keyPressed(keyCode, scanCode, modifiers);
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
            return b;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(textInput.isFocused()){
            boolean b = textInput.charTyped(chr, modifiers);
            alphaSlider.update();

            if(slidersVisible) {
                redSlider.update();
                greenSlider.update();
                blueSlider.update();
            }
            return  b;
        }
        return false;
    }
}
