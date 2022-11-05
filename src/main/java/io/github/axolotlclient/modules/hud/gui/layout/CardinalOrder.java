package io.github.axolotlclient.modules.hud.gui.layout;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

@AllArgsConstructor
public enum CardinalOrder {
    TOP_DOWN("topdown", false, -1),
    DOWN_TOP("downtop", false, 1),
    LEFT_RIGHT("leftright", true, 1),
    RIGHT_LEFT("rightleft", true, -1),
    ;

    private final String key;
    @Getter
    private final boolean xAxis;
    @Getter
    private final int direction;
}
