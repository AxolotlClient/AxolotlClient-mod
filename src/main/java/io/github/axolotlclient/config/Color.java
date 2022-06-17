package io.github.axolotlclient.config;

import io.github.axolotlclient.AxolotlClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class Color {
    private final int red, green, blue;
    private int alpha;
    private int color;

    private static final List<Color> chromaColors = new ArrayList<>();
    private static int chromaColorIndex;

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

    public static void setupChroma(){
        for (int r=0; r<75; r++) chromaColors.add(new Color(r*255/75,       255,         0));
        for (int g=75; g>0; g--) chromaColors.add(new Color(      255, g*255/75,         0));
        for (int b=0; b<75; b++) chromaColors.add(new Color(      255,         0, b*255/75));
        for (int r=75; r>0; r--) chromaColors.add(new Color(r*255/75,         0,       255));
        for (int g=0; g<75; g++) chromaColors.add(new Color(        0, g*255/75,       255));
        for (int b=75; b>0; b--) chromaColors.add(new Color(        0,       255, b*255/75));
        chromaColors.add(new Color(0,255,0));
    }

    public static void tickChroma(){
        chromaColorIndex+= AxolotlClient.CONFIG.chromaSpeed.get();
        if(chromaColorIndex >= chromaColors.size()-1)chromaColorIndex=0;
        else if(chromaColorIndex<0){chromaColorIndex=0;}
    }

    private void updateColor(){
        int color = alpha;
        color = (color << 8) + red;
        color = (color << 8) + green;
        color = (color << 8) + blue;
        this.color = color;
    }

    public static Color getChroma(){
        return chromaColors.get(chromaColorIndex);
    }

    public int getRed() {
        return red;
    }

    public int getBlue(){
        return blue;
    }

    public int getGreen(){
        return green;
    }

    public int getAlpha(){
        return alpha;
    }

    public Color withAlpha(int alpha){
        this.alpha=alpha;
        updateColor();
        return this;
    }

    public int getAsInt(){
        return color;
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
    public static Color DARK_GRAY = new Color(49, 51, 53, 50);
    public static Color SELECTOR_RED = new Color(191, 34, 34);
    public static Color GOLD = Color.parse("#b8860b");
    public static Color SELECTOR_GREEN = new Color(53, 219, 103);
    public static Color SELECTOR_BLUE = new Color(51, 153, 255, 100);
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
