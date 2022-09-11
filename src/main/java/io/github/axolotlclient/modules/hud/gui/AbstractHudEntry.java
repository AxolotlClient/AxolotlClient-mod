package io.github.axolotlclient.modules.hud.gui;

import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.DoubleOption;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class AbstractHudEntry extends DrawUtil {

    public int width;
    public int height;

    protected BooleanOption enabled = new BooleanOption("enabled",false);
    public DoubleOption scale = new DoubleOption("scale", 1, 0.1F, 5);
    protected final ColorOption textColor = new ColorOption("textColor", Color.WHITE);
    protected EnumOption textAlignment = new EnumOption("textAlignment", new String[]{"center", "left", "right"}, "center");
    protected BooleanOption shadow = new BooleanOption("shadow",  getShadowDefault());
    protected BooleanOption background = new BooleanOption("background",  true);
    protected final ColorOption backgroundColor = new ColorOption("bgColor", Color.parse("#64000000"));
    protected final BooleanOption outline = new BooleanOption("outline", false);
    protected final ColorOption outlineColor = new ColorOption("outlineColor", "#75000000");
    private final DoubleOption x = new DoubleOption("x", getDefaultX(), -0.5, 1.5);
    private final DoubleOption y = new DoubleOption("y", getDefaultY(), -0.5, 1.5);
    private final DrawPosition scaledPos = new DrawPosition(0, 0);
    private final Rectangle bounds = new Rectangle(0, 0, 0, 0);
    private final Rectangle scaledBounds = new Rectangle(0, 0, 0, 0);

    private List<OptionBase<?>> options;

    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();


    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init(){}

    public static int floatToInt(float percent, int max, int offset) {
        if(percent<0){
            return -MathHelper.clamp(Math.round((max - offset) * -percent), -1, max);
        }
        return MathHelper.clamp(Math.round((max - offset) * percent), -1, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        if(current<0){
            return -MathHelper.clamp((float) (-current) / (max - offset), 0, 2);
        }
        return MathHelper.clamp((float) (current) / (max - offset), -1, 2);
    }

    public void renderHud(MatrixStack matrices) {
        render(matrices);
    }

    public abstract void render(MatrixStack matrices);

    public abstract void renderPlaceholder(MatrixStack matrices);

    public void renderPlaceholderBackground(MatrixStack matrices) {
        if (hovered) {
            fillRect(matrices, getScaledBounds(), Color.SELECTOR_BLUE);
        } else {
            fillRect(matrices, getScaledBounds(), Color.DARK_GRAY);
        }
        outlineRect(matrices, getScaledBounds(), Color.BLACK);
    }

    public abstract Identifier getId();

    public abstract boolean movable();

    public boolean tickable() {
        return false;
    }

    public void tick() {
    }

    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return getScaledPos().x;
    }

    public void setX(int x) {
        this.x.set((double)intToFloat(x, MinecraftClient.getInstance().getWindow().getScaledWidth(),
                Math.round(width * getScale())));
    }

    public int getY() {
       return getScaledPos().y;
    }

    public void setY(int y) {
        this.y.set((double)intToFloat(y, MinecraftClient.getInstance().getWindow().getScaledHeight(),
                Math.round(height * getScale())));
    }

    protected double getDefaultX() {
        return 0;
    }

    protected float getDefaultY() {
        return 0;
    }

    protected boolean getShadowDefault() {
        return true;
    }

    public Rectangle getScaledBounds() {
        return scaledBounds.setData(getX(), getY(), (int) Math.round(width *  scale.get()),
                (int) Math.round(height * scale.get()));
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return bounds.setData(getPos().x, getPos().y, width, height);
    }

    public float getScale() {
        return scale.get().floatValue();
    }

    public void scale(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getScale(), getScale(), 1F);
    }

    public DrawPosition getPos() {
        return getScaledPos().divide(getScale());
    }

    public DrawPosition getScaledPos() {
        return getScaledPos(getScale());
    }

    public DrawPosition getScaledPos(float scale) {
        int scaledX = floatToInt( x.get().floatValue(), MinecraftClient.getInstance().getWindow().getScaledWidth(), Math.round(width * scale));
        int scaledY = floatToInt( y.get().floatValue(), MinecraftClient.getInstance().getWindow().getScaledHeight(), Math.round(height * scale));
        scaledPos.x = scaledX;
        scaledPos.y = scaledY;
        return scaledPos;
    }

    public List<OptionBase<?>> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
            addConfigOptions(options);
        }
        return options;
    }

    public OptionCategory getOptionsAsCategory(){
        OptionCategory cat = new OptionCategory(getNameKey(), false);
        cat.add(getOptions());
        return cat;
    }

    public OptionCategory getAllOptions() {
        List<OptionBase<?>> options = getOptions();
        options.add(x);
        options.add(y);
        OptionCategory cat = new OptionCategory(getNameKey());
        cat.add(options);
        return cat;
    }

    public void addConfigOptions(List<OptionBase<?>> options) {
        options.add(enabled);
        options.add(scale);
    }

    protected void drawString(MatrixStack matrices, String s, int x, int y, Color color, boolean shadow){
        switch (textAlignment.get()) {
            case "center" ->
                    drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, s, x + width / 2, y, color.getAsInt(), shadow);
            case "left" ->
                    drawString(matrices, MinecraftClient.getInstance().textRenderer, s, x, y, color.getAsInt(), shadow);
            case "right" ->
                    drawString(matrices, MinecraftClient.getInstance().textRenderer, s, x + width - MinecraftClient.getInstance().textRenderer.getWidth(s), y, color.getAsInt(), shadow);
        }
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public String getNameKey() {
        return getId().getPath();
    }

    public String getName() {
        return I18n.translate(getNameKey());
    }

    public void toggle() {
        enabled.toggle();
    }

    public boolean isHovered(int mouseX, int mouseY){
        return (mouseX >= getX() && mouseY >= getY() && mouseX < getX() + this.width && mouseY < getY() + this.height) || hovered;
    }

    public void setHovered(boolean value) {
        hovered=value;
    }
}
