package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class CoordsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "coordshud");

    private final ColorOption firstColor = new ColorOption("firsttextcolor", Color.SELECTOR_BLUE);
    private final ColorOption secondColor = new ColorOption("secondtextcolor", "#FFFFFFFF");
    private final IntegerOption decimalPlaces = new IntegerOption("decimalplaces", 0, 0, 15);

    public CoordsHud() {
        super(79, 31);
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
	public void render(MatrixStack matrices) {
		matrices.push();
		scale(matrices);
		DrawPosition pos = getPos();
		if (background.get()) {
			fillRect(matrices, getBounds(), backgroundColor.get());
		}
		if(outline.get()) {
			outlineRect(matrices, getBounds(), outlineColor.get());
		}
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.get() > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces.get(); i++) {
                format.append("0");
            }
        }
		DecimalFormat df = new DecimalFormat(format.toString());
		df.setRoundingMode(RoundingMode.CEILING);
		double x = client.player.getX();
		double y = client.player.getY();
		double z = client.player.getZ();
		double yaw = client.player.getYaw(0) + 180;
		int dir = getDirection(yaw);
		String direction = getWordedDirection(dir);
		TextRenderer textRenderer = client.textRenderer;
		drawString(matrices, textRenderer, "X", pos.x + 1, pos.y + 2, firstColor.get().getAsInt(),
			shadow.get());
		drawString(matrices, textRenderer, String.valueOf(df.format(x)), pos.x + 11, pos.y + 2,
			secondColor.get().getAsInt(), shadow.get());

		drawString(matrices, textRenderer, "Y", pos.x + 1, pos.y + 12, firstColor.get().getAsInt(),
			shadow.get());
		drawString(matrices, textRenderer, String.valueOf(df.format(y)), pos.x + 11, pos.y + 12,
			secondColor.get().getAsInt(), shadow.get());

		drawString(matrices, textRenderer, "Z", pos.x + 1, pos.y + 22, firstColor.get().getAsInt(),
			shadow.get());
		drawString(matrices, textRenderer, String.valueOf(df.format(z)), pos.x + 11, pos.y + 22,
			secondColor.get().getAsInt(), shadow.get());

		drawString(matrices, textRenderer, direction, pos.x + 60, pos.y + 12,
			firstColor.get().getAsInt(), shadow.get());

		drawString(matrices, textRenderer, getXDir(dir), pos.x + 60, pos.y + 2,
			secondColor.get().getAsInt(), shadow.get());
		textRenderer.drawWithShadow(matrices, getZDir(dir), pos.x + 60, pos.y + 22,
			secondColor.get().getAsInt(), shadow.get());

		matrices.pop();
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		DrawPosition pos = getPos();
		StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.get() > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces.get(); i++) {
                format.append("0");
            }
        }

		DecimalFormat df = new DecimalFormat(format.toString());
		df.setRoundingMode(RoundingMode.FLOOR);
		double x = 109.2325;
		double y = 180.8981;
		double z = -5098.32698;
		double yaw = 180;
		int dir = getDirection(yaw);
		String direction = getWordedDirection(dir);
		TextRenderer textRenderer = client.textRenderer;
		textRenderer.drawWithShadow(matrices, "X", pos.x + 1, pos.y + 2, firstColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.x + 11, pos.y + 2,
			secondColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, "Y", pos.x + 1, pos.y + 12, firstColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.x + 11, pos.y + 12,
			secondColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, "Z", pos.x + 1, pos.y + 22, firstColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.x + 11, pos.y + 22,
			secondColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, direction, pos.x + 60, pos.y + 12,
			firstColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, getXDir(dir), pos.x + 60, pos.y + 2,
			secondColor.get().getAsInt());
		textRenderer.drawWithShadow(matrices, getZDir(dir), pos.x + 60, pos.y + 22,
			secondColor.get().getAsInt());

		matrices.pop();
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
