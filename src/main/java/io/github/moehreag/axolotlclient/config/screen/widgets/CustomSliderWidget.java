package io.github.moehreag.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

public class CustomSliderWidget extends ButtonWidget {
    private float progress;
    public boolean focused;
    private final float min;
    private final float max;
    private final DecimalFormat format = new DecimalFormat("##.#");
    private final DecimalFormat intformat = new DecimalFormat("##");
    protected static final Identifier WIDGETS_LOCATION = new Identifier("axolotlclient", "textures/gui/slider.png");

    public CustomSliderWidget(int id, int x, int y, int width, int height, float min, float max, float f) {
        super(id, x, y, width, height, "");
        this.min = min;
        this.max = max;
        this.progress = (f - min) / (max - min);
        this.message = this.getMessage();
    }

    public CustomSliderWidget(int id, int x, int y, int width, int height, double min, double max, double f) {
        super(id, x, y, width, height, "");
        this.min = (float) min;
        this.max = (float) max;
        this.progress = (float) ((f - min) / (max - min));
        this.message = this.getMessage();
    }

    public float getSliderValue() {
        return Float.parseFloat(format.format(this.min + (this.max - this.min) * this.progress));
    }
    public int getSliderValueAsInt() {
        return Integer.parseInt(intformat.format(this.min + (this.max - this.min) * this.progress));
    }

    private String getMessage() {
        return ""+this.getSliderValue();
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.focused) {
                this.progress = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
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
            //this.drawTexture(this.x + (int)(this.progress * (float)(this.width - 8)), this.y, 0, 66, 4, height);
            drawTexture(this.x + (int)(this.progress * (float)(this.width - 8)) + 4, this.y, 0, 0, 4, 18, 4, 18);
        }
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        this.renderBg(client, mouseX, mouseY);
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

    public void setFocused(boolean focus){
        this.focused=focus;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.focused = false;
    }
}