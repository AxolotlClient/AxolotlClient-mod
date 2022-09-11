package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class OptionSliderWidget<T extends NumericOption<N>, N extends Number> extends ButtonWidget {
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final DecimalFormat intFormat = new DecimalFormat("##");

    private double value;
    public boolean dragging;
    private final T option;
    private final N min;
    private final N max;

    public OptionSliderWidget(int id, int x, int y, T option) {
        this(id, x, y, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, int width, int height, T option) {
        this(id, x, y, width, height, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int id, int x, int y, T option, N min, N max) {
        this(id, x, y, 150, 20, option, min, max);
    }

    public OptionSliderWidget(int id, int x, int y, int width, int height, T option, N min, N max) {
        super(id, x, y, width, height, "");
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get().doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
        this.message = this.getMessage();
    }

    public void update(){
        value = (option.get().doubleValue() - min.doubleValue()) / max.doubleValue() - min.doubleValue();
        this.message = this.getMessage();
    }

    public Double getSliderValue() {
        return Double.parseDouble(format.format(this.min.doubleValue() + (this.max.doubleValue() - this.min.doubleValue()) * this.value));
    }
    public int getSliderValueAsInt() {
        intFormat.applyLocalizedPattern("##");
        return Integer.parseInt(intFormat.format(this.min.doubleValue() + (this.max.doubleValue() - this.min.doubleValue()) * this.value));
    }

    public Double getValue(){
        format.applyLocalizedPattern("###.##");
        double value = this.min.doubleValue() + (this.max.doubleValue() - this.min.doubleValue()) * this.value;
        return Double.parseDouble(format.format(value));
    }

    protected @NotNull String getMessage() {
        return ""+ (option instanceof IntegerOption? getSliderValueAsInt()+"".split("\\.")[0]: this.getSliderValue());
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    @SuppressWarnings("unchecked")
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
                    if (option instanceof FloatOption) option.set((N) (Float) getSliderValue().floatValue());
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

    @SuppressWarnings("unchecked")
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(canHover()) {

            if (super.isMouseOver(client, mouseX, mouseY) || dragging) {
                this.value = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
                this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
                if (option instanceof FloatOption) option.set((N) (Float) getSliderValue().floatValue());
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

    public NumericOption<N> getOption(){
        return option;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

}
