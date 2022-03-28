package io.github.moehreag.axolotlclient.modules.hud.gui;

import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawUtil;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public abstract class AbstractHudEntry extends DrawUtil {

    public boolean enabled;
    public double x;
    public double y;
    public int width;
    public int height;

    /*protected KronBoolean enabled = new KronBoolean("enabled", null, false);
    public KronDouble scale = new KronDouble("scale", null, 1, 0.1F, 2);
    protected KronColor textColor = new KronColor("textcolor", null, "#FFFFFFFF");
    protected KronBoolean shadow = new KronBoolean("shadow", null, getShadowDefault());
    protected KronBoolean background = new KronBoolean("background", null, true);
    protected KronColor backgroundColor = new KronColor("backgroundcolor", null, "#64000000");
    private KronDouble x = new KronDouble("x", null, getDefaultX(), 0, 1);
    private KronDouble y = new KronDouble("y", null, getDefaultY(), 0, 1);

    private List<IConfigBase> options;

    public int width;
    public int height;
    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();
    protected Window window = new Window(client);


    */public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
    }

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

    /*public void renderPlaceholderBackground() {
        if (hovered) {
            fillRect(getScaledBounds(), Color.SELECTOR_BLUE);
        } else {
            fillRect(getScaledBounds(), Color.WHITE);
        }
        outlineRect( getScaledBounds(), Color.BLACK);
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
        this.x.setDoubleValue(intToFloat(x, (int) window.getScaledWidth(),
                Math.round(width * getScale())));
    }

    public int getY() {
       return getScaledPos().y();
    }

    public void setY(int y) {
        this.y.setDoubleValue(intToFloat(y, client.getWindow().getScaledHeight(),
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
        return new Rectangle(getX(), getY(), Math.round(width * (float) scale.getDoubleValue()),
                Math.round(height * (float) scale.getDoubleValue()));
    }*/

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     * @return The bounds.
     */
    /*public Rectangle getBounds() {
        return new Rectangle(getPos().x, getPos().y, width, height);
    }

    public float getScale() {
        return (float) scale.getDoubleValue();
    }

    public void scale() {

    }

    public DrawPosition getPos() {
        return getScaledPos().divide(getScale());
    }

    public DrawPosition getScaledPos() {
        return getScaledPos(getScale());
    }

    public DrawPosition getScaledPos(float scale) {
        int scaledX = floatToInt((float) x.getDoubleValue(), client.getWindow().getScaledWidth(),
                Math.round(width * scale));
        int scaledY = floatToInt((float) y.getDoubleValue(), client.getWindow().getScaledHeight(),
                Math.round(height * scale));
        return new DrawPosition(scaledX, scaledY);
    }

    public List<GuiConfigsBase.ConfigOptionWrapper> getOptionWrappers() {
        return GuiConfigsBase.ConfigOptionWrapper.createFor(getOptions());
    }

    public List<IConfigBase> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
            addConfigOptions(options);
        }
        return options;
    }

    public List<IConfigBase> getAllOptions() {
        List<IConfigBase> options = new ArrayList<>(getOptions());
        options.add(x);
        options.add(y);
        return options;
    }

    public void addConfigOptions(List<IConfigBase> options) {
        options.add(enabled);
        options.add(scale);
    }

    public boolean isEnabled() {
        return enabled.getBooleanValue();
    }

    public String getNameKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath();
    }

    public String getName() {
        return StringUtils.translate(getNameKey());
    }

    public void toggle() {
        enabled.toggleBooleanValue();
    }*/

}
