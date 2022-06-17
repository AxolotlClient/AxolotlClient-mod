package io.github.axolotlclient.util;

import net.minecraft.client.options.KeyBinding;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public final class KeyBindingCallback {
    public interface OnPress {
        void onPress(KeyBinding binding);
    }
}
