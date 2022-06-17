package io.github.axolotlclient.modules.hud.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
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
    public DoubleOption scale = new DoubleOption("scale", 1, 0.1F, 2);
    protected final ColorOption textColor = new ColorOption("textColor", Color.WHITE);
    protected final BooleanOption chroma = new BooleanOption("chroma", false);
    protected BooleanOption shadow = new BooleanOption("shadow",  getShadowDefault());
    protected BooleanOption background = new BooleanOption("background",  true);
    protected final ColorOption backgroundColor = new ColorOption("bgColor", Color.parse("#64000000"));
    protected final BooleanOption outline = new BooleanOption("outline", false);
    protected final ColorOption outlineColor = new ColorOption("outlineColor", "#75000000");
    private final DoubleOption x = new DoubleOption("x", getDefaultX(), 0, 1);
    private final DoubleOption y = new DoubleOption("y", getDefaultY(), 0, 1);

    private List<Option> options;

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

    public void renderHud() {
        render();
    }

    public abstract void render();

    public abstract void renderPlaceholder();

    public void renderPlaceholderBackground() {
        if (hovered) {
            fillRect(getScaledBounds(), Color.SELECTOR_BLUE);
        } else {
            fillRect(getScaledBounds(), Color.DARK_GRAY);
        }
        outlineRect(getScaledBounds(), Color.BLACK);


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
        this.x.set(intToFloat(x, (int)new Window(client).getScaledWidth(),
                Math.round(width * getScale())));
    }

    public int getY() {
       return getScaledPos().y;
    }

    public void setY(int y) {
        this.y.set(intToFloat(y, (int) new Window(client).getScaledHeight(),
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
        return new Rectangle(getX(), getY(), Math.round(width * (float) scale.get()),
                Math.round(height * (float) scale.get()));
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return new Rectangle(getPos().x, getPos().y, width, height);
    }

    public float getScale() {
        return (float) scale.get();
    }

    public void scale() {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(getScale(), getScale(), 1F);
    }

    public DrawPosition getPos() {
        return getScaledPos().divide(getScale());
    }

    public DrawPosition getScaledPos() {
        return getScaledPos(getScale());
    }

    public DrawPosition getScaledPos(float scale) {
        int scaledX = floatToInt((float) x.get(), (int) new Window(client).getScaledWidth(), Math.round(width * scale));
        int scaledY = floatToInt((float) y.get(), (int) new Window(client).getScaledHeight(), Math.round(height * scale));
        return new DrawPosition(scaledX, scaledY);
    }

    public List<Option> getOptions() {
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
        List<Option> options = new ArrayList<>(getOptions());
        options.add(x);
        options.add(y);
        OptionCategory cat = new OptionCategory(getId(), getNameKey());
        cat.add(options);
        return cat;
    }

    public void addConfigOptions(List<Option> options) {
        options.add(enabled);
        options.add(scale);
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
