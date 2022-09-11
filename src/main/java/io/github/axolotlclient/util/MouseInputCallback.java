package io.github.axolotlclient.util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public interface MouseInputCallback {
    void onMouseButton(long window, int button, int action, int mods);
}
