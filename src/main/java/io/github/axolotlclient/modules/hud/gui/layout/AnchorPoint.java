package io.github.axolotlclient.modules.hud.gui.layout;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AnchorPoint {
    TOP_LEFT("topleft", -1, 1),
    TOP_MIDDLE("topmiddle", 0, 1),
    TOP_RIGHT("topright", 1, 1),
    MIDDLE_LEFT("middleleft", -1, 0),
    MIDDLE_MIDDLE("middlemiddle", 0, 0),
    MIDDLE_RIGHT("middleright", 1, 0),
    BOTTOM_LEFT("bottomleft", -1, -1),
    BOTTOM_MIDDLE("bottommiddle", 0, -1),
    BOTTOM_RIGHT("bottomright", 1, -1),
    ;

    private final String key;

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
