package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class OptionSliderWidget<T extends NumericOption<N>, N extends Number> extends ButtonWidget {
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final DecimalFormat intFormat = new DecimalFormat("##");

    private double value;
    public boolean dragging;
    private final T option;
    private final N min;
    private final N max;

    public OptionSliderWidget(int x, int y, T option) {
        this(x, y, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int x, int y, int width, int height, T option) {
        this(x, y, width, height, option, option.getMin(), option.getMax());
    }

    public OptionSliderWidget(int x, int y, T option, N min, N max) {
        this(x, y, 150, 20, option, min, max);
    }

    public OptionSliderWidget(int x, int y, int width, int height, T option, N min, N max) {
        super(x, y, width, height, Text.empty(), (buttonWidget)->{});
        this.option = option;
        this.min = min;
        this.max = max;
        this.value = (option.get().doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
        this.setMessage(this.getMessage());
    }

    public void update(){
        value = (option.get().doubleValue() - min.doubleValue()) / max.doubleValue() - min.doubleValue();
        this.setMessage(this.getMessage());
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

    public Text getMessage() {
        return Text.of(""+ (option instanceof IntegerOption? getSliderValueAsInt()+"".split("\\.")[0]: this.getSliderValue()));
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float f) {
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

                this.setMessage(this.getMessage());
            }

            RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexture(matrices, this.x, this.y, 0, 46, this.width / 2, this.height);
            this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46, this.width / 2, this.height);

            this.drawTexture(matrices, this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66 + (isHoveredOrFocused() ? 20:0), 4, 20);
            this.drawTexture(matrices, this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66 + (isHoveredOrFocused() ? 20:0), 4, 20);

            int j = this.active ? 16777215 : 10526880;
            drawCenteredText(
                    matrices, MinecraftClient.getInstance().textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24
            );
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(canHover()) {

            if (super.isMouseOver(mouseX, mouseY) || dragging) {
                this.value = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
                this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
                if (option instanceof FloatOption) option.set((N) (Float) getSliderValue().floatValue());
                else if (option instanceof DoubleOption) ((DoubleOption) option).set(getSliderValue());
                else if (option instanceof IntegerOption) ((IntegerOption) option).set(getSliderValueAsInt());
                this.setMessage(getMessage());
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

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return super.isHoveredOrFocused() || dragging;
    }
}
