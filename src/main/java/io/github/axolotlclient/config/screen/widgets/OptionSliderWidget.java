package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.DoubleOption;
import io.github.axolotlclient.config.options.FloatOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class OptionSliderWidget extends ButtonWidget {
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final DecimalFormat intformat = new DecimalFormat("##");

    private double value;
    public boolean dragging;
    private final Option option;
    private final double min;
    private final double max;

    public OptionSliderWidget(int id, int x, int y, FloatOption option) {
        this(id, x, y, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, FloatOption option, float min, float max) {
        super(id, x, y, 150, 20, "");
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get() - min) / (max - min);
        this.message = this.getMessage();
    }

    public OptionSliderWidget(int id, int x, int y, IntegerOption option) {
        this(id, x, y, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, IntegerOption option, float min, float max) {
        super(id, x, y, 150, 20, "");
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get() - min) / (max - min);
        this.message = this.getMessage();
    }

    public OptionSliderWidget(int id, int x, int y, DoubleOption option) {
        this(id, x, y, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, DoubleOption option, double min, double max) {
        super(id, x, y, 150, 20, "");
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get() - min) / (max - min);
        this.message = this.getMessage();
    }

    public float getSliderValue() {
        format.applyLocalizedPattern("###.#");
        return Float.parseFloat(format.format(this.min + (this.max - this.min) * this.value));
    }
    public int getSliderValueAsInt() {
        intformat.applyLocalizedPattern("##");
        return Integer.parseInt(intformat.format(this.min + (this.max - this.min) * this.value));
    }

    private @NotNull String getMessage() {
        return ""+ (option instanceof IntegerOption? getSliderValueAsInt(): this.getSliderValue());
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);

                if (this.value < 0.0F) {
                    this.value = 0.0F;
                }
                if (this.value > 1.0F) {
                    this.value = 1.0F;
                }

                if(option!=null) {
                    if (option instanceof FloatOption) ((FloatOption) option).set(getSliderValue());
                    else if (option instanceof DoubleOption) ((DoubleOption) option).set(getSliderValue());
                    else if (option instanceof IntegerOption) ((IntegerOption) option).set(getSliderValueAsInt());
                }

                this.message = this.getMessage();
            }

            client.getTextureManager().bindTexture(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66 + (hovered ? 20:0), 4, 20);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66 + (hovered ? 20:0), 4, 20);
        }
    }

    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if (super.isMouseOver(client, mouseX, mouseY)) {
            this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
            if (option instanceof FloatOption) ((FloatOption) option).set(getSliderValue());
            else if (option instanceof DoubleOption) ((DoubleOption) option).set(getSliderValue());
            else if (option instanceof IntegerOption) ((IntegerOption) option).set(getSliderValueAsInt());
            this.message = getMessage();
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

}
