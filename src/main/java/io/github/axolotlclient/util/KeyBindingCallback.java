package io.github.axolotlclient.util;

import net.minecraft.client.options.KeyBinding;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public final class KeyBindingCallback {
    public interface OnPress {
        void onPress(KeyBinding binding);
    }
}
