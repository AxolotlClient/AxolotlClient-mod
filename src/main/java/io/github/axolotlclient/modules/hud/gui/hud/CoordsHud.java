package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class CoordsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "coordshud");

    private final ColorOption firstColor = new ColorOption("axolotlclient.firsttextcolor", Color.SELECTOR_BLUE);
    private final ColorOption secondColor = new ColorOption("axolotlclient.secondtextcolor", "#FFFFFFFF");
    private final IntegerOption decimalPlaces = new IntegerOption("axolotlclient.decimalplaces", value -> {
        StringBuilder format = new StringBuilder("#");
        if (value > 0) {
            format.append(".");
            for (int i = 0; i < value; i++) {
                format.append("0");
            }
        }
        df = new DecimalFormat(format.toString());
    }, 0, 0, 15);

    private DecimalFormat df = new DecimalFormat("#");

    public CoordsHud() {
        super(79, 31);
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public static String getZDir(int dir) {
        switch (dir) {
            case 5:
                return "++";
            case 4:
            case 6:
                return "+";
            case 8:
            case 2:
                return "-";
            case 1:
                return "--";
        }
        return "";
    }

    public static String getXDir(int dir) {
        switch (dir) {
            case 3:
                return "++";
            case 2:
            case 4:
                return "+";
            case 6:
            case 8:
                return "-";
            case 7:
                return "--";
        }
        return "";
    }

    /**
     * Get direction. 1 = North, 2 North East, 3 East, 4 South East...
     *
     * @param yaw The current rotation
     * @return better format
     */
    public static int getDirection(double yaw) {
        yaw = yaw % 360;
        int plzdontcrash = 0;
        while (yaw < 0) {
            if (plzdontcrash > 10) {
                return 0;
            }
            yaw = yaw + 360;
            plzdontcrash++;
        }
        int[] directions = {0, 23, 68, 113, 158, 203, 248, 293, 338, 360};
        for (int i = 0; i < directions.length; i++) {
            int min = directions[i];
            int max;
            if (i + 1 >= directions.length) {
                max = directions[0];
            } else {
                max = directions[i + 1];
            }
            if (yaw >= min && yaw < max) {
                if (i >= 8) {
                    return 1;
                }
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();
        if (background.get()) {
            fillRect(getBounds(), backgroundColor.get());
        }
        if(outline.get()) outlineRect(getBounds(), outlineColor.get());
        double x = client.player.getPos().x;
        double y = client.player.getPos().y;
        double z = client.player.getPos().z;
        double yaw = client.player.getHeadRotation() + 180;
        int dir = getDirection(yaw);
        String direction = getWordedDirection(dir);
        drawString("X", pos.x + 1, pos.y + 2, firstColor.get(),
                shadow.get());
        drawString(String.valueOf(df.format(x)), pos.x + 11, pos.y + 2,
                secondColor.get(), shadow.get());

        drawString("Y", pos.x + 1, pos.y + 12, firstColor.get(),
                shadow.get());
        drawString(String.valueOf(df.format(y)), pos.x + 11, pos.y + 12,
                secondColor.get(), shadow.get());

        drawString("Z", pos.x + 1, pos.y + 22, firstColor.get(),
                shadow.get());
        drawString(String.valueOf(df.format(z)), pos.x + 11, pos.y + 22,
                secondColor.get(), shadow.get());

        drawString(direction, pos.x + 60, pos.y + 12,
                firstColor.get(), shadow.get());

        drawString(getXDir(dir), pos.x + 60, pos.y + 2,
                secondColor.get(), shadow.get());
        drawString(getZDir(dir), pos.x + 60, pos.y + 22,
                secondColor.get(), shadow.get());

        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        double x = 109.2325;
        double y = 180.8981;
        double z = -5098.32698;
        double yaw = 180;
        int dir = getDirection(yaw);
        String direction = getWordedDirection(dir);
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.drawWithShadow("X", pos.x + 1, pos.y + 2, firstColor.get().getAsInt());
        textRenderer.drawWithShadow(String.valueOf(df.format(x)), pos.x + 11, pos.y + 2,
                secondColor.get().getAsInt());
        textRenderer.drawWithShadow("Y", pos.x + 1, pos.y + 12, firstColor.get().getAsInt());
        textRenderer.drawWithShadow(String.valueOf(df.format(y)), pos.x + 11, pos.y + 12,
                secondColor.get().getAsInt());
        textRenderer.drawWithShadow("Z", pos.x + 1, pos.y + 22, firstColor.get().getAsInt());
        textRenderer.drawWithShadow(String.valueOf(df.format(z)), pos.x + 11, pos.y + 22,
                secondColor.get().getAsInt());
        textRenderer.drawWithShadow(direction, pos.x + 60, pos.y + 12,
                firstColor.get().getAsInt());
        textRenderer.drawWithShadow(getXDir(dir), pos.x + 60, pos.y + 2,
                secondColor.get().getAsInt());
        textRenderer.drawWithShadow(getZDir(dir), pos.x + 60, pos.y + 22,
                secondColor.get().getAsInt());

        GlStateManager.popMatrix();
        hovered = false;
    }

    public String getWordedDirection(int dir) {
        String direction = "";
        switch (dir) {
            case 1:
                direction = "N";
                break;
            case 2:
                direction = "NE";
                break;
            case 3:
                direction = "E";
                break;
            case 4:
                direction = "SE";
                break;
            case 5:
                direction = "S";
                break;
            case 6:
                direction = "SW";
                break;
            case 7:
                direction = "W";
                break;
            case 8:
                direction = "NW";
                break;
            case 0:
                direction = "?";
                break;
        }
        return direction;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(textAlignment);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(firstColor);
        options.add(secondColor);
        options.add(decimalPlaces);
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

}
