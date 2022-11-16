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
    TOP_DOWN(false, -1),
    DOWN_TOP(false, 1),
    LEFT_RIGHT(true, 1),
    RIGHT_LEFT(true, -1),
    ;

    @Getter
    private final boolean xAxis;
    @Getter
    private final int direction;
}
