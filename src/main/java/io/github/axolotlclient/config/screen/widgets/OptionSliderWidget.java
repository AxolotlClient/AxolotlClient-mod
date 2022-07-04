package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
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
    private final OptionBase option;
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

    public OptionSliderWidget(int id, int x, int y, int width, int height, IntegerOption option) {
        this(id, x, y, width, height, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, int width, int height, IntegerOption option, float min, float max) {
        super(id, x, y, width, height, "");
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get() - min) / (max - min);
        this.message = this.getMessage();
    }

    public OptionSliderWidget(int id, int x, int y, IntegerOption option, float min, float max) {
        this(id, x, y, 150, 20, option, min, max);
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

    public void update(){
        if (option instanceof FloatOption) value =  (((FloatOption) option).get() - min) / (max - min);
        else if (option instanceof DoubleOption) value =  (((DoubleOption) option).get() - min) / (max - min);
        else if (option instanceof IntegerOption) value =  (((IntegerOption) option).get() - min) / (max - min);
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

    protected @NotNull String getMessage() {
        return ""+ (option instanceof IntegerOption? getSliderValueAsInt()+"".split("\\.")[0]: this.getSliderValue());
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

    protected boolean canHover(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).isPickerOpen()){
            this.hovered = false;
            return false;
        }
        return true;
    }

    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(canHover()) {

            if (super.isMouseOver(client, mouseX, mouseY) || dragging) {
                this.value = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
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
        return false;
    }

    public OptionBase getOption(){
        return option;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

}
