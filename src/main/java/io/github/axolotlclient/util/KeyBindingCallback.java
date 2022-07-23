package io.github.axolotlclient.util;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public final class KeyBindingCallback {

	public interface ChangeBind {
		void setBoundKey(InputUtil.Key boundKey);
	}

    public interface OnPress {
        void onPress(KeyBinding binding);
    }
}
