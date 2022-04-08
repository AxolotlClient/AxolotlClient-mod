package io.github.moehreag.axolotlclient.modules.hud.util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class Color {

    private final int red, green, blue;
    private final int alpha;
    final int color;

    public Color(int color) {
        this(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        int color = a;
        color = (color << 8) + r;
        color = (color << 8) + g;
        color = (color << 8) + b;
        this.color = color;
    }
    public int getAsInt(){
        return color;
    }

    protected Color(int r, int g, int b, int a, int color) {
        this(r, g, b, a);
    }

    public static Color parse(String color) {
        try {
            return new Color(Integer.parseInt(color));
        } catch (NumberFormatException ignored) {
        }

        if(color.startsWith("#")) {
            color = color.substring(1);
        } else if(color.startsWith("0x")) {
            color = color.substring(2);
        } if(color.length() == 6) {
            color = "FF" + color;
        } else if (color.length() != 8) {
            return ERROR;
        }
        try {
            return new Color(Integer.valueOf(color.substring(2, 4), 16),
                    Integer.valueOf(color.substring(4, 6), 16),
                    Integer.valueOf(color.substring(6, 8), 16),
                    Integer.valueOf(color.substring(0, 2), 16));
        } catch (NumberFormatException error) {
            return ERROR;
        }
    }

    @Override
    public String toString() {
        return String.format("#%08X", color);
    }

    public static Color WHITE = new Color(255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0);
    public static Color GRAY = new Color(128, 128, 128);
    public static Color DARK_GRAY = new Color(49, 51, 53);
    public static Color SELECTOR_RED = new Color(191, 34, 34);
    public static Color GOLD = Color.parse("#b8860b");
    public static Color SELECTOR_GREEN = new Color(53, 219, 103);
    public static Color SELECTOR_BLUE = new Color(51, 153, 255);
    public static Color ERROR = new Color(255, 0, 255);

    /**
     * Blends two {@link Color}s based off of a percentage.
     *
     * @param original   color to start the blend with
     * @param blend      color that when fully blended, will be this
     * @param percentage the percentage to blend
     * @return the simple color
     */
    public static Color blend(Color original, Color blend, float percentage) {
        if (percentage >= 1) {
            return blend;
        }
        if (percentage <= 0) {
            return original;
        }
        int red = blendInt(original.red, blend.red, percentage);
        int green = blendInt(original.green, blend.green, percentage);
        int blue = blendInt(original.blue, blend.blue, percentage);
        int alpha = blendInt(original.alpha, blend.alpha, percentage);
        return new Color(red, green, blue, alpha);
    }

    /**
     * Blends two ints together based off of a percent.
     *
     * @param start   starting int
     * @param end     end int
     * @param percent percent to blend
     * @return the blended int
     */
    public static int blendInt(int start, int end, float percent) {
        if (percent <= 0) {
            return start;
        }
        if (start == end || percent >= 1) {
            return end;
        }
        int dif = end - start;
        int add = Math.round((float) dif * percent);
        return start + (add);
    }

}