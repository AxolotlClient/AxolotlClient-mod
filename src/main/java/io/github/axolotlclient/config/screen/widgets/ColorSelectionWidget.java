package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
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
        super(0, 100, 50, "");
        this.option=option;
        init();
    }

    public void init(){
        Window window= new Window(MinecraftClient.getInstance());
        width=window.getWidth()-200;
        height=window.getHeight()-100;

        pickerImage = new Rectangle(120, 70, width/2, height/2);
        currentRect = new Rectangle(pickerImage.x + pickerImage.width + 20, pickerImage.y + 10, width - pickerImage.width - 60, 20);

        alpha.set(option.get().getAlpha());

        alphaSlider = new OptionSliderWidget(0, pickerImage.x, pickerImage.y + pickerImage.height + 20, pickerImage.width, 20, alpha){

            @Override
            protected boolean canHover() {
                return true;
            }

            @Override
            protected @NotNull String getMessage() {
                return getOption().getTranslatedName()+": " + super.getMessage();
            }
        };

        textInput = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer,
                currentRect.x, currentRect.y + currentRect.height + 10, currentRect.width, 20);
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {

        DrawUtil.fillRect(new Rectangle(100, 50, width, height), Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(new Rectangle(100, 50, width, height), Color.BLACK);

        drawCenteredString(MinecraftClient.getInstance().textRenderer, I18n.translate("pickColor"), new Window(MinecraftClient.getInstance()).getWidth()/2, 54, -1);

        DrawUtil.drawString(MinecraftClient.getInstance().textRenderer, I18n.translate("currentColor") + ":" ,currentRect.x, currentRect.y - 10, -1, true);

        DrawUtil.fillRect(currentRect, option.get());
        DrawUtil.outlineRect(currentRect, Color.DARK_GRAY.withAlpha(127));

        GlStateManager.color3f(1, 1, 1);

        MinecraftClient.getInstance().getTextureManager().bindTexture(wheel);
        DrawableHelper.drawTexture(pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);
        DrawUtil.outlineRect(pickerImage, Color.DARK_GRAY);

        alphaSlider.render(client, mouseX, mouseY);

        textInput.render();


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

    public void onClick(int mouseX, int mouseY){

        if(pickerImage.isMouseOver(mouseX, mouseY)){
            final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // Helped in the complete confusion:
            // https://github.com/MrCrayfish/MrCrayfishDeviceMod/blob/2a06b20ad8873855885285f3cee6a682e161e24c/src/main/java/com/mrcrayfish/device/util/GLHelper.java#L71

            Window window = new Window(MinecraftClient.getInstance());
            int scale = window.getScaleFactor();
            GL11.glReadPixels(mouseX * scale,
                    MinecraftClient.getInstance().height - mouseY * scale - scale,
                    1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

            final int r = pixelBuffer.get(0) & 0xff;
            final int g = pixelBuffer.get(1) & 0xff;
            final int b = pixelBuffer.get(2) & 0xff;
            final Color index = new Color(r, g, b, alpha.get());

            option.set(index);
        } else if (alphaSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), alpha.get()));
        }
        textInput.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        alphaSlider.mouseReleased(mouseX, mouseY);
    }

    public void keyPressed(char c, int code){
        if(textInput.isFocused()){
            textInput.keyPressed(c, code);
        }
    }

}
