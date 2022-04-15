package io.github.moehreag.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.DoubleOption;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.config.options.IntegerOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

public class CustomSliderWidget extends ButtonWidget {
    private float progress;
    public boolean focused;
    private final float min;
    private final float max;
    private final Option option;
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final DecimalFormat intformat = new DecimalFormat("##");
    private boolean showText = true;
    private boolean integer = false;
    protected static final Identifier WIDGETS_LOCATION = new Identifier("axolotlclient", "textures/gui/slider.png");

    public CustomSliderWidget(int id, int x, int y, int width, int height, float min, float max, float f, FloatOption option) {
        super(id, x, y, width, height, "");
        this.min = min;
        this.max = max;
        this.progress = (f - min) / (max - min);
        this.message = this.getMessage();
        integer=true;
        this.option=option;
    }

    public CustomSliderWidget(int id, int x, int y, int width, int height, double min, double max, double f, DoubleOption option) {
        super(id, x, y, width, height, "");
        this.min = (float) min;
        this.max = (float) max;
        this.progress = (float) ((f - min) / (max - min));
        this.message = this.getMessage();
        this.option=option;
    }

    public CustomSliderWidget(int id, int x, int y, int width, int height, double min, double max, double f, IntegerOption option) {
        super(id, x, y, width, height, "");
        this.min = (float) min;
        this.max = (float) max;
        this.progress = (float) ((f - min) / (max - min));
        this.message = this.getMessage();
        this.option=option;
    }

    public float getSliderValue() {
        format.applyLocalizedPattern("##.#");
        return Float.parseFloat(format.format(this.min + (this.max - this.min) * this.progress));
    }
    public int getSliderValueAsInt() {
        intformat.applyLocalizedPattern("##");
        return Integer.parseInt(intformat.format(this.min + (this.max - this.min) * this.progress));
    }

    private String getMessage() {
        return ""+ (integer? getSliderValueAsInt(): this.getSliderValue());
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
            if (this.focused) {
                this.progress = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                if(option!=null) {
                    if (option instanceof FloatOption) ((FloatOption) option).set(getSliderValue());
                    else if (option instanceof DoubleOption) ((DoubleOption) option).set(getSliderValue());
                    else if (option instanceof IntegerOption) ((IntegerOption) option).set(getSliderValueAsInt());
                }

                if (this.progress < 0.0F) {
                    this.progress = 0.0F;
                }
                if (this.progress > 1.0F) {
                    this.progress = 1.0F;
                }
                this.message = this.getMessage();
            }

            client.getTextureManager().bindTexture(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexture(this.x + (int)(this.progress * (float)(this.width - 8)) + 4, this.y, 0, 0, 4, height, 4, height);

    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        this.renderBg(client, mouseX, mouseY);
        if(showText)
            drawCenteredString(client.textRenderer, message, x+width/2, y+(height-8)/2, -1);
    }

    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if (super.isMouseOver(client, mouseX, mouseY)) {
            this.progress = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            if (this.progress < 0.0F) {
                this.progress = 0.0F;
            }

            if (this.progress > 1.0F) {
                this.progress = 1.0F;
            }

            this.message = this.getMessage();
            this.focused = true;
            return true;
        } else {
            return false;
        }
    }

    public void showText(boolean show){
        this.showText=show;
    }

    public void setFocused(boolean focus){
        this.focused=focus;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.focused = false;
    }
}