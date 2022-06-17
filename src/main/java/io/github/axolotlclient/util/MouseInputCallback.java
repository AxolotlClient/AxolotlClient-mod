package io.github.axolotlclient.util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public interface MouseInputCallback {
    void onMouseButton(long window, int button, int action, int mods);
}
