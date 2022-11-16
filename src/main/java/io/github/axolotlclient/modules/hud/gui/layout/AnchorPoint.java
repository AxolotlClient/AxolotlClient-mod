package io.github.axolotlclient.modules.hud.gui.layout;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

@AllArgsConstructor
public enum AnchorPoint {
    TOP_LEFT(-1, 1),
    TOP_MIDDLE(0, 1),
    TOP_RIGHT(1, 1),
    MIDDLE_LEFT(-1, 0),
    MIDDLE_MIDDLE(0, 0),
    MIDDLE_RIGHT(1, 0),
    BOTTOM_LEFT(-1, -1),
    BOTTOM_MIDDLE(0, -1),
    BOTTOM_RIGHT(1, -1),
    ;

    @Getter
    private final int xComponent;

    @Getter
    private final int yComponent;

    public int getX(int anchorX, int width) {
        switch (xComponent) {
            case 0: return anchorX - (width / 2);
            case 1: return anchorX - width;
            default: return anchorX;
        }
    }

    public int getY(int anchorY, int height) {
        switch (yComponent) {
            case 0: return anchorY - (height / 2);
            case 1: return anchorY - height;
            default: return anchorY;
        }
    }

    public int offsetWidth(int width) {
        switch (xComponent) {
            case 0: return width / 2;
            case 1: return width;
            default: return 0;
        }
    }

    public int offsetHeight(int height) {
        switch (yComponent) {
            case 0: return (height / 2);
            case 1: return 0;
            default: return height;
        }
    }
}
