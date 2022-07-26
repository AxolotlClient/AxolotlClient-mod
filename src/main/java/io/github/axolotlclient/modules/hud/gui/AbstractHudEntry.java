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
 * <a href="https://github.com/DarkKronicle/KronHUD">https://github.com/DarkKronicle/KronHUD</a>
 * Licensed under GPL-3.0
 */

public abstract class AbstractHudEntry extends DrawUtil {

    public int width;
    public int height;

    protected BooleanOption enabled = new BooleanOption("enabled",false);
    public DoubleOption scale = new DoubleOption("scale", 1, 0.1F, 5);
    protected EnumOption textAlignment = new EnumOption("textAlignment", new String[]{"center", "left", "right"}, "center");
    protected final ColorOption textColor = new ColorOption("textColor", Color.WHITE);
    protected final BooleanOption chroma = new BooleanOption("chroma", false);
    protected BooleanOption shadow = new BooleanOption("shadow",  getShadowDefault());
    protected BooleanOption background = new BooleanOption("background",  true);
    protected final ColorOption backgroundColor = new ColorOption("bgColor", Color.parse("#64000000"));
    protected final BooleanOption outline = new BooleanOption("outline", false);
    protected final ColorOption outlineColor = new ColorOption("outlineColor", "#75000000");
    private final DoubleOption x = new DoubleOption("x", getDefaultX(), 0, 1);
    private final DoubleOption y = new DoubleOption("y", getDefaultY(), 0, 1);

    private List<OptionBase<?>> options;

    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();


    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init(){}

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
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
        this.x.set(intToFloat(x, MinecraftClient.getInstance().getWindow().getScaledWidth(),
                Math.round(width * getScale())));
    }

    public int getY() {
       return getScaledPos().y;
    }

    public void setY(int y) {
        this.y.set(intToFloat(y, MinecraftClient.getInstance().getWindow().getScaledHeight(),
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
        return new Rectangle(getX(), getY(), Math.round(width * scale.get().floatValue()),
                Math.round(height * scale.get().floatValue()));
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return new Rectangle(getPos().x, getPos().y, width, height);
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
        int scaledX = floatToInt(x.get().floatValue(), MinecraftClient.getInstance().getWindow().getScaledWidth(), Math.round(width * scale));
        int scaledY = floatToInt(y.get().floatValue(), MinecraftClient.getInstance().getWindow().getScaledHeight(), Math.round(height * scale));
        return new DrawPosition(scaledX, scaledY);
    }

    public List<OptionBase<?>> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
            addConfigOptions(options);
        }
        return options;
    }

    public OptionCategory getOptionsAsCategory(){
        OptionCategory cat = new OptionCategory(getId(), getNameKey());
        cat.add(getOptions());
        return cat;
    }

    public OptionCategory getAllOptions() {
        List<OptionBase<?>> options = new ArrayList<>(getOptions());
        options.add(x);
        options.add(y);
        OptionCategory cat = new OptionCategory(getId(), getNameKey());
        cat.add(options);
        return cat;
    }

    public void addConfigOptions(List<OptionBase<?>> options) {
        options.add(enabled);
        options.add(scale);
    }

    protected void drawString(MatrixStack matrices, String s, int x, int y, Color color, boolean shadow){
        switch (textAlignment.get()){
            case "center":
                drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, s, x+width/2, y, color.getAsInt());
                break;
            case "left":
                DrawUtil.drawString(matrices, MinecraftClient.getInstance().textRenderer, s, x, y, color.getAsInt(), shadow);
                break;
            case "right":
                DrawUtil.drawString(matrices, MinecraftClient.getInstance().textRenderer, s, x+width - MinecraftClient.getInstance().textRenderer.getWidth(s), y, color.getAsInt(), shadow);
                break;
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
